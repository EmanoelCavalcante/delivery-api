package com.pitsdog.api.pedido.service;

import com.pitsdog.api.loja.service.LojaService;
import com.pitsdog.api.notificacao.service.NotificacaoPedidoService;
import com.pitsdog.api.pedido.dto.*;
import com.pitsdog.api.pedido.entity.*;
import com.pitsdog.api.pedido.repository.ComboRepository;
import com.pitsdog.api.pedido.repository.PedidoRepository;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private static final BigDecimal LIMITE_DESCONTO_PERCENTUAL = BigDecimal.valueOf(35);

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ComboRepository comboRepository;
    private final AdicionalService adicionalService;
    private final LojaService lojaService;
    private final NotificacaoPedidoService notificacaoPedidoService;
    private final BigDecimal taxaEntregaPadrao;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ComboRepository comboRepository,
            AdicionalService adicionalService,
            LojaService lojaService,
            NotificacaoPedidoService notificacaoPedidoService,
            @Value("${TAXA_ENTREGA_PADRAO:0.00}") BigDecimal taxaEntregaPadrao
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.comboRepository = comboRepository;
        this.adicionalService = adicionalService;
        this.lojaService = lojaService;
        this.notificacaoPedidoService = notificacaoPedidoService;
        this.taxaEntregaPadrao = taxaEntregaPadrao;

        if (taxaEntregaPadrao == null || taxaEntregaPadrao.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("TAXA_ENTREGA_PADRAO deve ser maior ou igual a zero.");
        }
    }

    private Pedido buscarPedidoEntityById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido não encontrado"
                ));
    }

    private Pedido buscarPedidoCompletoEntityById(Long id) {
        return pedidoRepository.findPedidoCompletoById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido não encontrado"
                ));
    }

    private Produto buscarProdutoById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado"
                ));
    }

    private Combo buscarComboById(Long id) {
        return comboRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Combo não encontrado"
                ));
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String limparTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        return telefone.replaceAll("\\D", "");
    }

    private BigDecimal calcularTaxaEntrega(TipoPedido tipoPedido) {
        if (tipoPedido == TipoPedido.ENTREGA) {
            return taxaEntregaPadrao;
        }

        return BigDecimal.ZERO;
    }

    private Integer validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantidade deve ser maior ou igual a 1"
            );
        }

        return quantidade;
    }

    private OrigemPedido validarOrigemPedidoPublico(OrigemPedido origemPedido) {
        if (origemPedido == null) {
            return OrigemPedido.SITE;
        }

        if (origemPedido == OrigemPedido.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Origem ADMIN não pode ser utilizada na criação pública de pedidos"
            );
        }

        return origemPedido;
    }

    private void validarPedidoRequest(CriarPedidoRequestDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados do pedido são obrigatórios"
            );
        }

        if (dto.getTipoPedido() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "tipoPedido é obrigatório"
            );
        }

        if (dto.getFormaPagamento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "formaPagamento é obrigatório"
            );
        }

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido deve possuir pelo menos um item"
            );
        }

        if (dto.getTipoPedido() == TipoPedido.ENTREGA) {
            if (dto.getNomeCliente() == null || dto.getNomeCliente().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "nomeCliente é obrigatório para ENTREGA"
                );
            }

            if (dto.getBairroEntrega() == null || dto.getBairroEntrega().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "bairroEntrega é obrigatório para ENTREGA"
                );
            }

            if (dto.getRuaEntrega() == null || dto.getRuaEntrega().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "ruaEntrega é obrigatório para ENTREGA"
                );
            }

            if (dto.getNumeroCasa() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "numeroCasa é obrigatório para ENTREGA"
                );
            }
        }

        if (dto.getTipoPedido() == TipoPedido.MESA) {
            if (dto.getNumeroMesa() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "numeroMesa é obrigatório para MESA"
                );
            }
        }
    }

    private void validarPedidoNaoEncerrado(Pedido pedido) {
        if (pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido finalizado não pode mais ser alterado"
            );
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido cancelado não pode mais ser alterado"
            );
        }
    }

    private void validarPedidoEditavel(Pedido pedido) {
        validarPedidoNaoEncerrado(pedido);

        if (pedido.getStatus() != StatusPedido.ABERTO
                && pedido.getStatus() != StatusPedido.AGUARDANDO_APROVACAO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido não pode mais ser editado"
            );
        }
    }

    private void validarDescontoManual(BigDecimal percentual, BigDecimal valor) {
        BigDecimal percentualSeguro = valorOuZero(percentual);
        BigDecimal valorSeguro = valorOuZero(valor);

        if (percentualSeguro.compareTo(BigDecimal.ZERO) < 0
                || valorSeguro.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Desconto não pode ser negativo"
            );
        }

        if (percentualSeguro.compareTo(BigDecimal.ZERO) > 0
                && valorSeguro.compareTo(BigDecimal.ZERO) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe desconto por percentual ou por valor, não os dois ao mesmo tempo"
            );
        }

        if (percentualSeguro.compareTo(LIMITE_DESCONTO_PERCENTUAL) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Desconto percentual não pode ser maior que 35%"
            );
        }
    }

    private void validarTransicaoStatus(Pedido pedido, StatusPedido novoStatus) {
        StatusPedido statusAtual = pedido.getStatus();

        if (statusAtual == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido não possui status atual válido"
            );
        }

        if (novoStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status não pode ser null"
            );
        }

        if (statusAtual == novoStatus) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido já está com o status " + novoStatus
            );
        }

        boolean transicaoPermitida = switch (statusAtual) {
            case ABERTO ->
                    novoStatus == StatusPedido.AGUARDANDO_APROVACAO
                            || novoStatus == StatusPedido.CANCELADO;

            case AGUARDANDO_APROVACAO ->
                    novoStatus == StatusPedido.APROVADO
                            || novoStatus == StatusPedido.CANCELADO;

            case APROVADO ->
                    novoStatus == StatusPedido.PREPARANDO
                            || novoStatus == StatusPedido.CANCELADO;

            case PREPARANDO ->
                    novoStatus == StatusPedido.PRONTO
                            || novoStatus == StatusPedido.CANCELADO;

            case PRONTO -> {
                if (pedido.getTipoPedido() == TipoPedido.ENTREGA) {
                    yield novoStatus == StatusPedido.SAIU_PARA_ENTREGA
                            || novoStatus == StatusPedido.CANCELADO;
                }

                yield novoStatus == StatusPedido.FINALIZADO
                        || novoStatus == StatusPedido.CANCELADO;
            }

            case SAIU_PARA_ENTREGA ->
                    novoStatus == StatusPedido.FINALIZADO
                            || novoStatus == StatusPedido.CANCELADO;

            case FINALIZADO, CANCELADO -> false;
        };

        if (!transicaoPermitida) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transição de status inválida: " + statusAtual + " -> " + novoStatus
            );
        }
    }

    private void notificarMudancaDeStatusSeNecessario(
            Pedido pedido,
            StatusPedido statusAnterior,
            StatusPedido novoStatus
    ) {
        if (pedido == null || novoStatus == null) {
            return;
        }

        if (statusAnterior == novoStatus) {
            return;
        }

        if (deveNotificarPedidoProntoParaRetirada(pedido, novoStatus)) {
            enviarNotificacaoSemQuebrarPedido(() ->
                    notificacaoPedidoService.notificarPedidoProntoParaRetirada(pedido)
            );
            return;
        }

        if (deveNotificarPedidoSaiuParaEntrega(pedido, novoStatus)) {
            enviarNotificacaoSemQuebrarPedido(() ->
                    notificacaoPedidoService.notificarPedidoSaiuParaEntrega(pedido)
            );
        }
    }

    private boolean deveNotificarPedidoProntoParaRetirada(
            Pedido pedido,
            StatusPedido novoStatus
    ) {
        return pedido.getTipoPedido() == TipoPedido.RETIRADA
                && novoStatus == StatusPedido.PRONTO
                && pedido.getTelefoneCliente() != null
                && !pedido.getTelefoneCliente().isBlank();
    }

    private boolean deveNotificarPedidoSaiuParaEntrega(
            Pedido pedido,
            StatusPedido novoStatus
    ) {
        return pedido.getTipoPedido() == TipoPedido.ENTREGA
                && novoStatus == StatusPedido.SAIU_PARA_ENTREGA
                && pedido.getTelefoneCliente() != null
                && !pedido.getTelefoneCliente().isBlank();
    }

    private void enviarNotificacaoSemQuebrarPedido(Runnable acaoNotificacao) {
        try {
            acaoNotificacao.run();
        } catch (Exception exception) {
            System.out.println("Falha ao enviar notificação do pedido: " + exception.getMessage());
        }
    }

    private BigDecimal calcularSubtotalItem(ItemPedido item) {
        BigDecimal quantidade = BigDecimal.valueOf(validarQuantidade(item.getQuantidade()));

        BigDecimal precoUnitario = valorOuZero(item.getPrecoUnitario());
        BigDecimal subtotalBase = precoUnitario.multiply(quantidade);

        BigDecimal subtotalAdicionais = BigDecimal.ZERO;

        if (item.getAdicional() != null) {
            for (ItemPedidoAdicional adicional : item.getAdicional()) {
                subtotalAdicionais = subtotalAdicionais.add(valorOuZero(adicional.getSubtotal()));
            }
        }

        return subtotalBase.add(subtotalAdicionais);
    }

    private void recalcularValores(Pedido pedido) {
        BigDecimal subtotal = BigDecimal.ZERO;

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                BigDecimal subtotalItem = calcularSubtotalItem(item);
                item.setSubtotal(subtotalItem);
                subtotal = subtotal.add(subtotalItem);
            }
        }

        BigDecimal descontoManualValor = valorOuZero(pedido.getDescontoManualValor());
        BigDecimal descontoManualPercentual = valorOuZero(pedido.getDescontoManualPercentual());

        BigDecimal descontoPercentualCalculado = BigDecimal.ZERO;

        if (descontoManualPercentual.compareTo(BigDecimal.ZERO) > 0) {
            descontoPercentualCalculado = subtotal
                    .multiply(descontoManualPercentual)
                    .divide(BigDecimal.valueOf(100));
        }

        BigDecimal descontoFidelidadeValor = valorOuZero(pedido.getDescontoFidelidadeValor());
        BigDecimal taxaEntrega = valorOuZero(pedido.getTaxaEntrega());

        BigDecimal total = subtotal
                .subtract(descontoManualValor)
                .subtract(descontoPercentualCalculado)
                .subtract(descontoFidelidadeValor)
                .add(taxaEntrega);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        pedido.setSubtotal(subtotal);
        pedido.setTotal(total);
    }

    private List<ItemPedido> converterItensParaEntity(
            List<ItemPedidoRequestDTO> itensDTO,
            Pedido pedido
    ) {
        if (itensDTO == null || itensDTO.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido deve possuir pelo menos um item"
            );
        }

        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoRequestDTO itemDTO : itensDTO) {
            if (itemDTO == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Item do pedido não pode ser null"
                );
            }

            if (itemDTO.getTipoItem() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Tipo do item é obrigatório"
                );
            }

            ItemPedido itemPedido = new ItemPedido();

            Integer quantidade = validarQuantidade(itemDTO.getQuantidade());

            itemPedido.setPedido(pedido);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setObservacao(itemDTO.getObservacao());

            if (itemDTO.getTipoItem() == TipoItemPedido.PRODUTO) {
                configurarItemProduto(itemDTO, itemPedido);
            } else if (itemDTO.getTipoItem() == TipoItemPedido.COMBO) {
                configurarItemCombo(itemDTO, itemPedido);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Tipo do item inválido"
                );
            }

            List<ItemPedidoAdicional> adicionais =
                    adicionalService.converterAdicionaisParaItemPedido(
                            itemDTO.getAdicionais(),
                            itemPedido
                    );

            itemPedido.setAdicional(adicionais);

            BigDecimal subtotalItem = calcularSubtotalItem(itemPedido);
            itemPedido.setSubtotal(subtotalItem);

            itens.add(itemPedido);
        }

        return itens;
    }

    private void configurarItemProduto(ItemPedidoRequestDTO itemDTO, ItemPedido itemPedido) {
        if (itemDTO.getProdutoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Produto é obrigatório para item do tipo PRODUTO"
            );
        }

        if (itemDTO.getComboId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "comboId deve ser null para item do tipo PRODUTO"
            );
        }

        Produto produto = buscarProdutoById(itemDTO.getProdutoId());

        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Produto indisponível para pedido: " + produto.getNome()
            );
        }

        if (!Boolean.TRUE.equals(produto.getPermiteAdicionais())
                && itemDTO.getAdicionais() != null
                && !itemDTO.getAdicionais().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Este produto não permite adicionais: " + produto.getNome()
            );
        }

        if (produto.getPreco() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Preço do produto não configurado"
            );
        }

        itemPedido.setTipoItem(TipoItemPedido.PRODUTO);
        itemPedido.setProduto(produto);
        itemPedido.setCombo(null);
        itemPedido.setNomeProduto(produto.getNome());
        itemPedido.setPrecoUnitario(produto.getPreco());
    }

    private void configurarItemCombo(ItemPedidoRequestDTO itemDTO, ItemPedido itemPedido) {
        if (itemDTO.getComboId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Combo é obrigatório para item do tipo COMBO"
            );
        }

        if (itemDTO.getProdutoId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "produtoId deve ser null para item do tipo COMBO"
            );
        }

        Combo combo = buscarComboById(itemDTO.getComboId());

        if (!Boolean.TRUE.equals(combo.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Combo indisponível para pedido: " + combo.getNome()
            );
        }

        if (combo.getPreco() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Preço do combo não configurado"
            );
        }

        itemPedido.setTipoItem(TipoItemPedido.COMBO);
        itemPedido.setCombo(combo);
        itemPedido.setProduto(null);
        itemPedido.setNomeProduto(combo.getNome());
        itemPedido.setPrecoUnitario(combo.getPreco());
    }

    private ItemPedido buscarItemPedido(Pedido pedido, Long itemId) {
        if (itemId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "itemId é obrigatório"
            );
        }

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido não possui itens"
            );
        }

        for (ItemPedido item : pedido.getItens()) {
            if (itemId.equals(item.getId())) {
                return item;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Item não encontrado neste pedido"
        );
    }

    private PedidoDTO converterParaPedidoDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();

        dto.setId(pedido.getId());
        dto.setNumeroPedido(pedido.getNumeroPedido());

        dto.setTipoPedido(pedido.getTipoPedido());
        dto.setNumeroMesa(pedido.getNumeroMesa());

        dto.setNomeCliente(pedido.getNomeCliente());
        dto.setTelefoneCliente(pedido.getTelefoneCliente());

        dto.setBairroEntrega(pedido.getBairroEntrega());
        dto.setRuaEntrega(pedido.getRuaEntrega());
        dto.setNumeroCasa(pedido.getNumeroCasa());
        dto.setComplemento(pedido.getComplemento());

        dto.setOrigemPedido(pedido.getOrigemPedido());
        dto.setObservacao(pedido.getObservacao());
        dto.setStatus(pedido.getStatus());

        dto.setMomentoPedido(pedido.getMomentoPedido());
        dto.setPrevisaoRetirada(pedido.getPrevisaoRetirada());

        dto.setSubtotal(pedido.getSubtotal());
        dto.setDescontoManualPercentual(pedido.getDescontoManualPercentual());
        dto.setDescontoManualValor(pedido.getDescontoManualValor());

        dto.setDescontoFidelidadePercentual(pedido.getDescontoFidelidadePercentual());
        dto.setDescontoFidelidadeValor(pedido.getDescontoFidelidadeValor());

        dto.setTaxaEntrega(pedido.getTaxaEntrega());
        dto.setTotal(pedido.getTotal());

        dto.setFormaPagamento(pedido.getFormaPagamento());

        List<ItemPedidoDTO> itensDTO = new ArrayList<>();

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                itensDTO.add(converterItemParaItemPedidoDTO(item));
            }
        }

        dto.setItens(itensDTO);

        return dto;
    }

    private ItemPedidoDTO converterItemParaItemPedidoDTO(ItemPedido item) {
        ItemPedidoDTO dto = new ItemPedidoDTO();

        dto.setId(item.getId());
        dto.setTipoItem(item.getTipoItem());

        if (item.getProduto() != null) {
            dto.setProdutoId(item.getProduto().getId());
            dto.setNomeProduto(item.getProduto().getNome());
        }

        if (item.getCombo() != null) {
            dto.setComboId(item.getCombo().getId());
            dto.setNomeCombo(item.getCombo().getNome());
        }

        dto.setNomeItem(item.getNomeProduto());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        dto.setObservacao(item.getObservacao());

        List<ItemPedidoAdicionalDTO> adicionaisDTO = new ArrayList<>();

        if (item.getAdicional() != null) {
            for (ItemPedidoAdicional adicional : item.getAdicional()) {
                adicionaisDTO.add(converterAdicionalParaItemPedidoAdicionalDTO(adicional));
            }
        }

        dto.setAdicionais(adicionaisDTO);

        return dto;
    }

    private ItemPedidoAdicionalDTO converterAdicionalParaItemPedidoAdicionalDTO(
            ItemPedidoAdicional adicional
    ) {
        ItemPedidoAdicionalDTO dto = new ItemPedidoAdicionalDTO();

        dto.setId(adicional.getId());

        if (adicional.getAdicional() != null) {
            dto.setAdicionalId(adicional.getAdicional().getId());
        }

        dto.setNomeAdicional(adicional.getNomeAdicional());
        dto.setQuantidade(adicional.getQuantidade());
        dto.setPrecoUnitario(adicional.getPrecoUnitario());
        dto.setSubtotal(adicional.getSubtotal());

        return dto;
    }

    private PedidoResponseDTO converterParaResponseDTO(Pedido pedido) {
        return converterParaResponseDTO(converterParaPedidoDTO(pedido));
    }

    private PedidoResponseDTO converterParaResponseDTO(PedidoDTO pedidoDTO) {
        PedidoResponseDTO dto = new PedidoResponseDTO();

        dto.setId(pedidoDTO.getId());
        dto.setNumeroPedido(pedidoDTO.getNumeroPedido());

        dto.setTipoPedido(pedidoDTO.getTipoPedido());
        dto.setNumeroMesa(pedidoDTO.getNumeroMesa());

        dto.setNomeCliente(pedidoDTO.getNomeCliente());
        dto.setTelefoneCliente(pedidoDTO.getTelefoneCliente());

        dto.setBairroEntrega(pedidoDTO.getBairroEntrega());
        dto.setRuaEntrega(pedidoDTO.getRuaEntrega());
        dto.setNumeroCasa(pedidoDTO.getNumeroCasa());
        dto.setComplemento(pedidoDTO.getComplemento());

        dto.setOrigemPedido(pedidoDTO.getOrigemPedido());
        dto.setObservacao(pedidoDTO.getObservacao());
        dto.setStatus(pedidoDTO.getStatus());

        dto.setMomentoPedido(pedidoDTO.getMomentoPedido());
        dto.setPrevisaoRetirada(pedidoDTO.getPrevisaoRetirada());

        dto.setSubtotal(pedidoDTO.getSubtotal());
        dto.setDescontoManualPercentual(pedidoDTO.getDescontoManualPercentual());
        dto.setDescontoManualValor(pedidoDTO.getDescontoManualValor());

        dto.setDescontoFidelidadePercentual(pedidoDTO.getDescontoFidelidadePercentual());
        dto.setDescontoFidelidadeValor(pedidoDTO.getDescontoFidelidadeValor());

        dto.setTaxaEntrega(pedidoDTO.getTaxaEntrega());
        dto.setTotal(pedidoDTO.getTotal());

        dto.setFormaPagamento(pedidoDTO.getFormaPagamento());
        dto.setItens(pedidoDTO.getItens());

        return dto;
    }

    private void carregarAdicionaisDosItens(Pedido pedido) {
        if (pedido.getItens() == null) {
            return;
        }

        for (ItemPedido item : pedido.getItens()) {
            if (item.getAdicional() != null) {
                item.getAdicional().size();

                for (ItemPedidoAdicional adicional : item.getAdicional()) {
                    if (adicional.getAdicional() != null) {
                        adicional.getAdicional().getId();
                    }
                }
            }
        }
    }

    @Transactional
    public PedidoResponseDTO createPedido(CriarPedidoRequestDTO dto) {
        validarPedidoRequest(dto);

        lojaService.validarRecebimentoPedido(dto.getTipoPedido());

        Pedido pedido = new Pedido();

        pedido.setTipoPedido(dto.getTipoPedido());
        pedido.setNumeroMesa(dto.getNumeroMesa());

        pedido.setNomeCliente(dto.getNomeCliente());
        pedido.setTelefoneCliente(limparTelefone(dto.getTelefoneCliente()));

        pedido.setBairroEntrega(dto.getBairroEntrega());
        pedido.setRuaEntrega(dto.getRuaEntrega());
        pedido.setNumeroCasa(dto.getNumeroCasa());
        pedido.setComplemento(dto.getComplemento());

        pedido.setPrevisaoRetirada(dto.getPrevisaoRetirada());
        pedido.setFormaPagamento(dto.getFormaPagamento());

        pedido.setMomentoPedido(LocalDateTime.now());
        pedido.setOrigemPedido(validarOrigemPedidoPublico(dto.getOrigemPedido()));
        pedido.setObservacao(dto.getObservacao());

        pedido.setStatus(StatusPedido.AGUARDANDO_APROVACAO);

        pedido.setTaxaEntrega(calcularTaxaEntrega(dto.getTipoPedido()));
        pedido.setDescontoManualPercentual(BigDecimal.ZERO);
        pedido.setDescontoManualValor(BigDecimal.ZERO);
        pedido.setDescontoFidelidadePercentual(BigDecimal.ZERO);
        pedido.setDescontoFidelidadeValor(BigDecimal.ZERO);

        List<ItemPedido> itens = converterItensParaEntity(dto.getItens(), pedido);
        pedido.setItens(itens);

        recalcularValores(pedido);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        if (pedidoSalvo.getNumeroPedido() == null && pedidoSalvo.getId() != null) {
            pedidoSalvo.setNumeroPedido(Math.toIntExact(pedidoSalvo.getId()));
            pedidoSalvo = pedidoRepository.save(pedidoSalvo);
        }

        return converterParaResponseDTO(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        List<PedidoResponseDTO> response = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            response.add(converterParaResponseDTO(pedido));
        }

        return response;
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoById(Long id) {
        Pedido pedido = buscarPedidoEntityById(id);
        return converterParaResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoDTO buscarPedidoDTOCompletoPorId(Long id) {
        Pedido pedido = buscarPedidoCompletoEntityById(id);

        carregarAdicionaisDosItens(pedido);

        return converterParaPedidoDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidoByMesa(Integer numeroMesa) {
        List<Pedido> pedidos = pedidoRepository.findByNumeroMesa(numeroMesa);
        List<PedidoResponseDTO> response = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            response.add(converterParaResponseDTO(pedido));
        }

        return response;
    }

    @Transactional
    public PedidoResponseDTO editarPedido(Long id, CriarPedidoRequestDTO dto) {
        validarPedidoRequest(dto);
        validarDescontoManual(dto.getDescontoManualPercentual(), dto.getDescontoManualValor());

        Pedido pedido = buscarPedidoEntityById(id);

        validarPedidoEditavel(pedido);

        pedido.setTipoPedido(dto.getTipoPedido());
        pedido.setNumeroMesa(dto.getNumeroMesa());

        pedido.setNomeCliente(dto.getNomeCliente());
        pedido.setTelefoneCliente(limparTelefone(dto.getTelefoneCliente()));

        pedido.setBairroEntrega(dto.getBairroEntrega());
        pedido.setRuaEntrega(dto.getRuaEntrega());
        pedido.setNumeroCasa(dto.getNumeroCasa());
        pedido.setComplemento(dto.getComplemento());

        pedido.setPrevisaoRetirada(dto.getPrevisaoRetirada());
        pedido.setFormaPagamento(dto.getFormaPagamento());

        pedido.setOrigemPedido(
                dto.getOrigemPedido() != null
                        ? dto.getOrigemPedido()
                        : pedido.getOrigemPedido()
        );

        pedido.setObservacao(dto.getObservacao());

        if (pedido.getTipoPedido() == TipoPedido.ENTREGA) {
            pedido.setTaxaEntrega(
                    dto.getTaxaEntrega() != null
                            ? dto.getTaxaEntrega()
                            : calcularTaxaEntrega(TipoPedido.ENTREGA)
            );
        } else {
            pedido.setTaxaEntrega(BigDecimal.ZERO);
        }

        pedido.setDescontoManualPercentual(valorOuZero(dto.getDescontoManualPercentual()));
        pedido.setDescontoManualValor(valorOuZero(dto.getDescontoManualValor()));

        if (pedido.getItens() == null) {
            pedido.setItens(new ArrayList<>());
        } else {
            pedido.getItens().clear();
        }

        List<ItemPedido> novosItens = converterItensParaEntity(dto.getItens(), pedido);
        pedido.getItens().addAll(novosItens);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, AtualizarStatusPedidoDTO dto) {
        if (dto == null || dto.getStatus() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status não pode ser null"
            );
        }

        Pedido pedido = buscarPedidoEntityById(id);

        StatusPedido statusAnterior = pedido.getStatus();
        StatusPedido novoStatus = dto.getStatus();

        validarTransicaoStatus(pedido, novoStatus);

        pedido.setStatus(novoStatus);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        notificarMudancaDeStatusSeNecessario(
                pedidoAtualizado,
                statusAnterior,
                novoStatus
        );

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO atualizarFormaDePagamento(Long id, AtualizarPagamentoPedidoDTO dto) {
        if (dto == null || dto.getFormaPagamento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Forma de pagamento não pode ser null"
            );
        }

        Pedido pedido = buscarPedidoEntityById(id);

        validarPedidoNaoEncerrado(pedido);

        pedido.setFormaPagamento(dto.getFormaPagamento());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO aplicarDescontoManual(Long id, AplicarDescontoPedidoDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados do desconto são obrigatórios"
            );
        }

        validarDescontoManual(dto.getDescontoManualPercentual(), dto.getDescontoManualValor());

        Pedido pedido = buscarPedidoEntityById(id);

        validarPedidoNaoEncerrado(pedido);

        pedido.setDescontoManualPercentual(valorOuZero(dto.getDescontoManualPercentual()));
        pedido.setDescontoManualValor(valorOuZero(dto.getDescontoManualValor()));

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO adicionarItemAoPedido(Long pedidoId, ItemPedidoRequestDTO dto) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        List<ItemPedido> novosItens = converterItensParaEntity(List.of(dto), pedido);

        if (pedido.getItens() == null) {
            pedido.setItens(new ArrayList<>());
        }

        pedido.getItens().addAll(novosItens);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO removerItemDoPedido(Long pedidoId, Long itemId) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido não possui itens"
            );
        }

        boolean removido = pedido.getItens()
                .removeIf(item -> itemId != null && itemId.equals(item.getId()));

        if (!removido) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item não encontrado neste pedido"
            );
        }

        if (pedido.getItens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido deve possuir pelo menos um item"
            );
        }

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO atualizarQuantidadeItem(Long pedidoId, Long itemId, Integer quantidade) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        ItemPedido item = buscarItemPedido(pedido, itemId);

        item.setQuantidade(validarQuantidade(quantidade));

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO atualizarObservacaoItem(Long pedidoId, Long itemId, String observacao) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        ItemPedido item = buscarItemPedido(pedido, itemId);

        item.setObservacao(observacao);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO atualizarAdicionaisDoItem(
            Long pedidoId,
            Long itemId,
            List<ItemPedidoAdicionalRequestDTO> adicionalDTO
    ) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        ItemPedido item = buscarItemPedido(pedido, itemId);

        if (item.getTipoItem() == TipoItemPedido.PRODUTO
                && item.getProduto() != null
                && !Boolean.TRUE.equals(item.getProduto().getPermiteAdicionais())
                && adicionalDTO != null
                && !adicionalDTO.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Este produto não permite adicionais: " + item.getProduto().getNome()
            );
        }

        List<ItemPedidoAdicional> novosAdicionais =
                adicionalService.converterAdicionaisParaItemPedido(adicionalDTO, item);

        if (item.getAdicional() == null) {
            item.setAdicional(new ArrayList<>());
        } else {
            item.getAdicional().clear();
        }

        item.getAdicional().addAll(novosAdicionais);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public void removerPedido(Long id) {
        Pedido pedido = buscarPedidoEntityById(id);
        pedidoRepository.delete(pedido);
    }
}