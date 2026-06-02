package com.pitsdog.api.pedido.service;

import com.pitsdog.api.pedido.dto.*;
import com.pitsdog.api.pedido.entity.*;
import com.pitsdog.api.pedido.repository.ComboRepository;
import com.pitsdog.api.pedido.repository.PedidoRepository;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ComboRepository comboRepository;
    private final AdicionalService adicionalService;
    private final BigDecimal taxaEntregaPadrao;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ComboRepository comboRepository,
            AdicionalService adicionalService,
            @Value("${TAXA_ENTREGA_PADRAO:0.00}") BigDecimal taxaEntregaPadrao
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.comboRepository = comboRepository;
        this.adicionalService = adicionalService;
        this.taxaEntregaPadrao = taxaEntregaPadrao;

        if(taxaEntregaPadrao == null || taxaEntregaPadrao.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalStateException("TAXA_ENTREGA_PADRAO deve ser maior ou igual a zero.");
        }
    }

    private BigDecimal calcularTaxaEntrega(TipoPedido tipoPedido){
        if(tipoPedido == TipoPedido.ENTREGA){
            return taxaEntregaPadrao;
        }
        return BigDecimal.ZERO;
    }

    private Pedido buscarPedidoEntityById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private Produto buscarProdutoById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    private Combo buscarComboById(Long id) {
        return comboRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Combo não encontrado"));
    }

    private String limparTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        return telefone.replaceAll("\\D", "");
    }

    private void validarPedidoRequest(CriarPedidoRequestDTO dto) {
        if (dto.getTipoPedido() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipoPedido é obrigatório");
        }
        if (dto.getFormaPagamento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "formaPagamento é obrigatório");
        }
        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido deve possuir pelo menos um item");
        }

        if (dto.getTipoPedido() == TipoPedido.ENTREGA) {
            if (dto.getNomeCliente() == null || dto.getNomeCliente().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nomeCliente é obrigatório para ENTREGA");
            }
            if (dto.getBairroEntrega() == null || dto.getBairroEntrega().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bairroEntrega é obrigatório para ENTREGA");
            }
            if (dto.getRuaEntrega() == null || dto.getRuaEntrega().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ruaEntrega é obrigatório para ENTREGA");
            }
            if (dto.getNumeroCasa() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numeroCasa é obrigatório para ENTREGA");
            }
        }

        if (dto.getTipoPedido() == TipoPedido.MESA) {
            if (dto.getNumeroMesa() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numeroMesa é obrigatório para MESA");
            }
        }
    }

    private void validarTransacaoStatus(Pedido pedido, StatusPedido novoStatus){
        StatusPedido statusAtual = pedido.getStatus();

        if(statusAtual == null){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido não possui status atual válido."
            );
        }
        if(statusAtual == novoStatus){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido já está com o status " + novoStatus
            );
        }
        boolean transicaoPermitida = switch (statusAtual){
            case ABERTO ->
                novoStatus == StatusPedido.AGUARDANDO_APROVACAO ||
                        novoStatus == StatusPedido.CANCELADO;

            case AGUARDANDO_APROVACAO ->
                novoStatus == StatusPedido.APROVADO ||
                        novoStatus == StatusPedido.CANCELADO;

            case APROVADO ->
                novoStatus == StatusPedido.PREPARANDO ||
                        novoStatus == StatusPedido.CANCELADO;

            case PRONTO -> {
                if(pedido.getTipoPedido() == TipoPedido.ENTREGA){
                    yield novoStatus == StatusPedido.SAIU_PARA_ENTREGA ||
                            novoStatus == StatusPedido.CANCELADO;
                }

                yield novoStatus == StatusPedido.FINALIZADO ||
                        novoStatus == StatusPedido.CANCELADO;

            }
            case SAIU_PARA_ENTREGA ->
                novoStatus == StatusPedido.FINALIZADO ||
                        novoStatus == StatusPedido.CANCELADO;

            case FINALIZADO, CANCELADO -> false;
            default -> throw new IllegalStateException("Unexpected value: " + statusAtual);
        };

        if(!transicaoPermitida){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transição de status inválida: "
                    + statusAtual
                    + " -> "
                    + novoStatus
            );
        }
    }

    private void validarPedidoNaoEncerrado(Pedido pedido){
        if (pedido.getStatus() == StatusPedido.FINALIZADO){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido finalizado não pode mais ser alterado"
            );
        }

        if(pedido.getStatus() == StatusPedido.CANCELADO){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido cancelado não pode mais ser alterado"
            );
        }
    }

    private BigDecimal valorOurZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private Integer validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior ou igual a 1");
        }

        return quantidade;
    }

    private BigDecimal calcularSubtotalItem(ItemPedido item) {
        BigDecimal quantidade = BigDecimal.valueOf(validarQuantidade(item.getQuantidade()));

        BigDecimal precoUnitario = valorOurZero(item.getPrecoUnitario());
        BigDecimal subtotalBase = precoUnitario.multiply(quantidade);

        BigDecimal subtotalAdicionais = BigDecimal.ZERO;

        if (item.getAdicional() != null) {
            for (ItemPedidoAdicional adicional : item.getAdicional()) {
                subtotalAdicionais = subtotalAdicionais.add(valorOurZero(adicional.getSubtotal()));
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

        BigDecimal descontoManualValor = valorOurZero(pedido.getDescontoManualValor());
        BigDecimal descontoManualPercentual = valorOurZero(pedido.getDescontoManualPercentual());

        BigDecimal descontoPercentualCalculado = BigDecimal.ZERO;

        if (descontoManualPercentual.compareTo(BigDecimal.ZERO) > 0) {
            descontoPercentualCalculado = subtotal
                    .multiply(descontoManualPercentual)
                    .divide(BigDecimal.valueOf(100));
        }

        BigDecimal descontoFidelidadeValor = valorOurZero(pedido.getDescontoFidelidadeValor());
        BigDecimal taxaEntrega = valorOurZero(pedido.getTaxaEntrega());

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

    private PedidoResponseDTO converterParaResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();

        dto.setId(pedido.getId());
        dto.setNumeroPedido(pedido.getNumeroPedido());

        dto.setTipoPedido(pedido.getTipoPedido());
        dto.setNumeroMesa(pedido.getNumeroMesa());

        dto.setNomeCliente(pedido.getNomeCliente());
        dto.setTelefoneCliente(pedido.getTelefoneCliente());

        dto.setBairroEntrega(pedido.getBairroEntrega());
        dto.setRuaEntrega(pedido.getRuaEntrega());
        dto.setNumeroCasa(pedido.getNumeroCasa());

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
        dto.setComplemento(pedido.getComplemento());

        List<ItemPedidoResponseDTO> itensResponse = new ArrayList<>();

        if (pedido.getItens() != null) {
            for (ItemPedido itemPedido : pedido.getItens()) {
                itensResponse.add(converterItemParaResponseDTO(itemPedido));
            }
        }

        dto.setItens(itensResponse);

        return dto;
    }

    private ItemPedidoAdicionalResponseDTO converterAdicionalParaResponseDTO(ItemPedidoAdicional adicional) {
        ItemPedidoAdicionalResponseDTO dto = new ItemPedidoAdicionalResponseDTO();

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

    private ItemPedidoResponseDTO converterItemParaResponseDTO(ItemPedido item) {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();

        dto.setId(item.getId());
        dto.setTipoItem(item.getTipoItem());

        if (item.getProduto() != null) {
            dto.setProdutoId(item.getProduto().getId());
        }

        if (item.getCombo() != null) {
            dto.setComboId(item.getCombo().getId());
            dto.setNomeCombo(item.getCombo().getNome());
        }

        dto.setNomeProduto(item.getNomeProduto());
        dto.setObservacao(item.getObservacao());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());

        List<ItemPedidoAdicionalResponseDTO> adicionaisResponse = new ArrayList<>();

        if (item.getAdicional() != null) {
            for (ItemPedidoAdicional adicional : item.getAdicional()) {
                adicionaisResponse.add(converterAdicionalParaResponseDTO(adicional));
            }
        }

        dto.setAdicionais(adicionaisResponse);

        return dto;
    }

    private List<ItemPedido> converterItensParaEntity(
            List<ItemPedidoRequestDTO> itensDTO,
            Pedido pedido
    ) {
        List<ItemPedido> itens = new ArrayList<>();

        if (itensDTO == null || itensDTO.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido deve possuir pelo menos um item");
        }

        for (ItemPedidoRequestDTO itemDTO : itensDTO) {
            ItemPedido itemPedido = new ItemPedido();

            if (itemDTO.getTipoItem() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo do item é obrigatório");
            }

            Integer quantidade = validarQuantidade(itemDTO.getQuantidade());

            itemPedido.setPedido(pedido);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setObservacao(itemDTO.getObservacao());

            if (itemDTO.getTipoItem() == TipoItemPedido.PRODUTO) {
                if (itemDTO.getProdutoId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto é obrigatório para item do tipo PRODUTO");
                }
                if (itemDTO.getComboId() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "comboId deve ser null para item do tipo PRODUTO");
                }

                Produto produto = buscarProdutoById(itemDTO.getProdutoId());
                if (produto.getPreco() == null) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Preço do produto não configurado");
                }

                itemPedido.setTipoItem(TipoItemPedido.PRODUTO);
                itemPedido.setProduto(produto);
                itemPedido.setCombo(null);

                itemPedido.setNomeProduto(produto.getNome());
                itemPedido.setPrecoUnitario(produto.getPreco());
            } else if (itemDTO.getTipoItem() == TipoItemPedido.COMBO) {
                if (itemDTO.getComboId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Combo é obrigatório para item do tipo COMBO");
                }
                if (itemDTO.getProdutoId() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "produtoId deve ser null para item do tipo COMBO");
                }

                Combo combo = buscarComboById(itemDTO.getComboId());
                if (combo.getPreco() == null) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Preço do combo não configurado");
                }

                itemPedido.setTipoItem(TipoItemPedido.COMBO);
                itemPedido.setCombo(combo);
                itemPedido.setProduto(null);

                // Mantemos compatibilidade com o response atual (nomeProduto já existe).
                itemPedido.setNomeProduto(combo.getNome());
                itemPedido.setPrecoUnitario(combo.getPreco());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo do item inválido");
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

    private ItemPedido buscarItemPedido(Pedido pedido, Long itemId) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não possui itens");
        }

        for (ItemPedido item : pedido.getItens()) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado neste pedido");
    }

    private void validarPedidoEditavel(Pedido pedido) {
        if (pedido.getStatus() != StatusPedido.ABERTO && pedido.getStatus() != StatusPedido.AGUARDANDO_APROVACAO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não pode mais ser editado");
        }
    }

    public PedidoResponseDTO createPedido(CriarPedidoRequestDTO dto) {
        validarPedidoRequest(dto);

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

        pedido.setStatus(StatusPedido.AGUARDANDO_APROVACAO);

        pedido.setTaxaEntrega(calcularTaxaEntrega(pedido.getTipoPedido()));
        pedido.setDescontoManualPercentual(BigDecimal.ZERO);
        pedido.setDescontoManualValor(BigDecimal.ZERO);

        pedido.setDescontoFidelidadePercentual(BigDecimal.ZERO);
        pedido.setDescontoFidelidadeValor(BigDecimal.ZERO);

        List<ItemPedido> itens = converterItensParaEntity(dto.getItens(), pedido);

        pedido.setItens(itens);
        recalcularValores(pedido);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        if(pedidoSalvo.getNumeroPedido() == null && pedidoSalvo.getId() != null){
            pedidoSalvo.setNumeroPedido(Math.toIntExact(pedidoSalvo.getId()));
            pedidoSalvo = pedidoRepository.save(pedidoSalvo);
        }
        return converterParaResponseDTO(pedidoSalvo);
    }

    public List<PedidoResponseDTO> listPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        List<PedidoResponseDTO> response = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            response.add(converterParaResponseDTO(pedido));
        }

        return response;
    }

    public PedidoResponseDTO buscarPedidoById(Long id) {
        Pedido pedido = buscarPedidoEntityById(id);

        return converterParaResponseDTO(pedido);
    }

    public List<PedidoResponseDTO> buscarPedidoByMesa(Integer numeroMesa) {
        List<Pedido> pedidos = pedidoRepository.findByNumeroMesa(numeroMesa);

        List<PedidoResponseDTO> response = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            response.add(converterParaResponseDTO(pedido));
        }

        return response;
    }

    public PedidoResponseDTO editarPedido(Long id, CriarPedidoRequestDTO dto) {
        validarPedidoRequest(dto);

        Pedido pedido = buscarPedidoEntityById(id);

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

        if (pedido.getTipoPedido() == TipoPedido.ENTREGA) {
            pedido.setTaxaEntrega(valorOurZero(dto.getTaxaEntrega()));
        } else {
            pedido.setTaxaEntrega(BigDecimal.ZERO);
        }

        pedido.setDescontoManualPercentual(valorOurZero(dto.getDescontoManualPercentual()));
        pedido.setDescontoManualValor(valorOurZero(dto.getDescontoManualValor()));

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

    public PedidoResponseDTO atualizarStatusPedido(Long id, AtualizarStatusPedidoDTO dto) {
        Pedido pedido = buscarPedidoEntityById(id);

        if(dto == null || dto.getStatus() == null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status não pode ser null"
            );
        }

        validarTransacaoStatus(pedido, dto.getStatus());

        pedido.setStatus(dto.getStatus());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO atualizarFormaDePagamento(Long id, AtualizarPagamentoPedidoDTO dto) {
        Pedido pedido = buscarPedidoEntityById(id);

        if (dto.getFormaPagamento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Forma de pagamento não pode ser null");
        }

        pedido.setFormaPagamento(dto.getFormaPagamento());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO aplicarDescontoManual(Long id, AplicarDescontoPedidoDTO dto) {
        Pedido pedido = buscarPedidoEntityById(id);

        pedido.setDescontoManualPercentual(valorOurZero(dto.getDescontoManualPercentual()));
        pedido.setDescontoManualValor(valorOurZero(dto.getDescontoManualValor()));

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO adicionarItemAoPedido(Long pedidoId, ItemPedidoRequestDTO dto) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        List<ItemPedidoRequestDTO> itensDTO = List.of(dto);

        List<ItemPedido> novosItens = converterItensParaEntity(itensDTO, pedido);

        if (pedido.getItens() == null) {
            pedido.setItens(new ArrayList<>());
        }
        pedido.getItens().addAll(novosItens);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO removerItemDoPedido(Long pedidoId, Long itemId) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        if (pedido.getItens() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não possui itens");
        }

        boolean removido = pedido.getItens().removeIf(item -> item.getId().equals(itemId));

        if (!removido) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado neste pedido");
        }

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO atualizarQuantidadeItem(Long pedidoId, Long itemId, Integer quantidade) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        ItemPedido item = buscarItemPedido(pedido, itemId);

        item.setQuantidade(validarQuantidade(quantidade));

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO atualizarObservacaoItem(Long pedidoId, Long itemId, String observacao) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        ItemPedido item = buscarItemPedido(pedido, itemId);

        item.setObservacao(observacao);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO atualizarAdicionaisDoItem(
            Long pedidoId,
            Long itemId,
            List<ItemPedidoAdicionalRequestDTO> adicionalDTO
    ) {
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        validarPedidoEditavel(pedido);

        ItemPedido item = buscarItemPedido(pedido, itemId);

        List<ItemPedidoAdicional> novosAdicionais =
                adicionalService.converterAdicionaisParaItemPedido(adicionalDTO, item);

        if (item.getAdicional() == null) {
            item.setAdicional(new ArrayList<>());
        }

        item.getAdicional().clear();
        item.getAdicional().addAll(novosAdicionais);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public void removerPedido(Long id) {
        Pedido pedido = buscarPedidoEntityById(id);

        pedidoRepository.delete(pedido);
    }
}
