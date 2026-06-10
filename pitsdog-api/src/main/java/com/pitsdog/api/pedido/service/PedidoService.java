package com.pitsdog.api.pedido.service;

import com.pitsdog.api.loja.service.LojaService;
import com.pitsdog.api.pagamento.service.PagamentoService;
import com.pitsdog.api.pedido.dto.*;
import com.pitsdog.api.pedido.entity.*;
import com.pitsdog.api.pedido.enums.OrigemPedido;
import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.pedido.enums.TipoItemPedido;
import com.pitsdog.api.pedido.enums.TipoPedido;
import com.pitsdog.api.pedido.mapper.PedidoMapper;
import com.pitsdog.api.pedido.repository.ComboRepository;
import com.pitsdog.api.pedido.repository.PedidoRepository;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);
    private static final BigDecimal LIMITE_DESCONTO_PERCENTUAL = BigDecimal.valueOf(35);

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ComboRepository comboRepository;
    private final AdicionalService adicionalService;
    private final LojaService lojaService;
    private final PagamentoService pagamentoService;
    private final PedidoMapper pedidoMapper;
    private final BigDecimal taxaEntregaPadrao;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ComboRepository comboRepository,
            AdicionalService adicionalService,
            LojaService lojaService,
            PagamentoService pagamentoService,
            PedidoMapper pedidoMapper,
            @Value("${TAXA_ENTREGA_PADRAO:0.00}") BigDecimal taxaEntregaPadrao
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.comboRepository = comboRepository;
        this.adicionalService = adicionalService;
        this.lojaService = lojaService;
        this.pagamentoService = pagamentoService;
        this.pedidoMapper = pedidoMapper;
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

    private void validarPedidoNaoCancelado(Pedido pedido) {
        // ALTERAÇÃO:
        // FINALIZADO não bloqueia edição.
        // O único status que bloqueia edição direta é CANCELADO.
        // Para editar um pedido cancelado, primeiro o ADMIN precisa restaurar.
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido cancelado não pode ser editado. Restaure o pedido antes de editar."
            );
        }
    }

    private void validarPedidoEditavel(Pedido pedido) {
        // ALTERAÇÃO:
        // O admin pode editar pedidos em qualquer etapa:
        // AGUARDANDO_APROVACAO, EM_PREPARO, CONCLUIDO,
        // SAIU_PARA_ENTREGA, PRONTO_PARA_RETIRADA e FINALIZADO.
        // Bloqueia somente CANCELADO.
        validarPedidoNaoCancelado(pedido);
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

        if (statusAtual == StatusPedido.FINALIZADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido finalizado não pode mais ter status alterado"
            );
        }

        if (statusAtual == StatusPedido.CANCELADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido cancelado não pode mais ter status alterado. Restaure o pedido antes."
            );
        }

        if (novoStatus == StatusPedido.CANCELADO) {
            return;
        }

        if (pedido.getTipoPedido() == null) {
            throw new IllegalStateException("Pedido possui tipo de pedido inválido ou ausente");
        }

        boolean transicaoPermitida = switch (pedido.getTipoPedido()) {
            case MESA -> validarTransicaoMesa(statusAtual, novoStatus);
            case ENTREGA -> validarTransicaoEntrega(statusAtual, novoStatus);
            case RETIRADA -> validarTransicaoRetirada(statusAtual, novoStatus);
        };

        if (!transicaoPermitida) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transição de status inválida para pedido do tipo "
                            + pedido.getTipoPedido()
                            + ": "
                            + statusAtual
                            + " -> "
                            + novoStatus
            );
        }
    }

    private boolean validarTransicaoMesa(StatusPedido statusAtual, StatusPedido novoStatus) {
        return switch (statusAtual) {
            case AGUARDANDO_APROVACAO ->
                    novoStatus == StatusPedido.EM_PREPARO;

            case EM_PREPARO ->
                    novoStatus == StatusPedido.CONCLUIDO;

            case CONCLUIDO ->
                    novoStatus == StatusPedido.FINALIZADO;

            default -> false;
        };
    }

    private boolean validarTransicaoEntrega(StatusPedido statusAtual, StatusPedido novoStatus) {
        return switch (statusAtual) {
            case AGUARDANDO_APROVACAO ->
                    novoStatus == StatusPedido.EM_PREPARO;

            case EM_PREPARO ->
                    novoStatus == StatusPedido.SAIU_PARA_ENTREGA;

            case SAIU_PARA_ENTREGA ->
                    novoStatus == StatusPedido.FINALIZADO;

            default -> false;
        };
    }

    private boolean validarTransicaoRetirada(StatusPedido statusAtual, StatusPedido novoStatus) {
        return switch (statusAtual) {
            case AGUARDANDO_APROVACAO ->
                    novoStatus == StatusPedido.EM_PREPARO;

            case EM_PREPARO ->
                    novoStatus == StatusPedido.PRONTO_PARA_RETIRADA;

            case PRONTO_PARA_RETIRADA ->
                    novoStatus == StatusPedido.FINALIZADO;

            default -> false;
        };
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
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
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

    private PedidoResponseDTO converterParaResponseDTO(Pedido pedido) {
        return pedidoMapper.toPedidoResponseDTO(pedido);
    }

    private PedidoResumoResponseDTO converterParaResumoResponseDTO(Pedido pedido) {
        return pedidoMapper.toPedidoResumoResponseDTO(pedido);
    }

    private PedidoDTO converterParaPedidoDTO(Pedido pedido) {
        return pedidoMapper.toPedidoDTO(pedido);
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

        pagamentoService.resetarPagamentoParaPendente(pedido);

        pedido.setTaxaEntrega(calcularTaxaEntrega(dto.getTipoPedido()));
        pedido.setDescontoManualPercentual(BigDecimal.ZERO);
        pedido.setDescontoManualValor(BigDecimal.ZERO);
        pedido.setDescontoFidelidadePercentual(BigDecimal.ZERO);
        pedido.setDescontoFidelidadeValor(BigDecimal.ZERO);

        List<ItemPedido> itens = converterItensParaEntity(dto.getItens(), pedido);
        pedido.setItens(itens);

        recalcularValores(pedido);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        Long idGerado = pedidoSalvo.getId();
        if (idGerado != null && idGerado > Integer.MAX_VALUE) {
            log.error("ID gerado {} excede capacidade do campo numeroPedido", idGerado);
            throw new IllegalStateException(
                    "Limite de numeroPedido atingido. Contate o suporte."
            );
        }

        return converterParaResponseDTO(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResumoResponseDTO> listPedidosHistorico(
            Pageable pageable,
            StatusPedido status,
            TipoPedido tipoPedido,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        LocalDateTime limite15Dias = LocalDateTime.now().minusDays(15);

        LocalDateTime inicioFiltro =
                dataInicio != null && dataInicio.isAfter(limite15Dias)
                        ? dataInicio
                        : limite15Dias;

        Specification<Pedido> filtros = (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("momentoPedido"),
                        inicioFiltro
                );

        if (status != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (tipoPedido != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("tipoPedido"), tipoPedido));
        }

        if (dataFim != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("momentoPedido"), dataFim));
        }

        return pedidoRepository.findAll(filtros, pageable)
                .map(this::converterParaResumoResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResumoResponseDTO> listPedidosResumo(
            Pageable pageable,
            StatusPedido status,
            TipoPedido tipoPedido,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        LocalDateTime limite15Dias = LocalDateTime.now().minusDays(15);
        LocalDateTime inicioFiltro =
                dataInicio != null && dataInicio.isAfter(limite15Dias)
                        ? dataInicio
                        : limite15Dias;

        Specification<Pedido> filtros = (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("momentoPedido"),
                        inicioFiltro
                );

        if (status != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        } else {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    root.get("status").in(
                            StatusPedido.AGUARDANDO_APROVACAO,
                            StatusPedido.EM_PREPARO,
                            StatusPedido.CONCLUIDO,
                            StatusPedido.SAIU_PARA_ENTREGA,
                            StatusPedido.PRONTO_PARA_RETIRADA
                    )
            );
        }

        if (tipoPedido != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("tipoPedido"), tipoPedido));
        }

        if (dataFim != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("momentoPedido"), dataFim));
        }

        return pedidoRepository.findAll(filtros, pageable)
                .map(this::converterParaResumoResponseDTO);
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
            log.warn("Falha ao atualizar status do pedido: pedidoId={}, motivo=status ausente", id);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status não pode ser null"
            );
        }

        Pedido pedido = buscarPedidoEntityById(id);

        StatusPedido novoStatus = dto.getStatus();
        StatusPedido statusAtual = pedido.getStatus();

        log.info(
                "Atualizando status do pedido: pedidoId={}, statusAtual={}, statusSolicitado={}",
                id,
                statusAtual,
                novoStatus
        );

        try {
            validarTransicaoStatus(pedido, novoStatus);

            pedido.setStatus(novoStatus);

            if (novoStatus == StatusPedido.CANCELADO) {
                pagamentoService.marcarPagamentoComoCancelado(pedido);
            }

            Pedido pedidoAtualizado = pedidoRepository.save(pedido);

            log.info(
                    "Status do pedido atualizado com sucesso: pedidoId={}, statusAnterior={}, statusNovo={}",
                    id,
                    statusAtual,
                    novoStatus
            );

            return converterParaResponseDTO(pedidoAtualizado);
        } catch (RuntimeException ex) {
            log.warn(
                    "Falha ao atualizar status do pedido: pedidoId={}, statusAtual={}, statusSolicitado={}, motivo={}",
                    id,
                    statusAtual,
                    novoStatus,
                    ex.getMessage()
            );
            throw ex;
        }
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

        validarPedidoNaoCancelado(pedido);

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

        validarPedidoNaoCancelado(pedido);

        pedido.setDescontoManualPercentual(valorOuZero(dto.getDescontoManualPercentual()));
        pedido.setDescontoManualValor(valorOuZero(dto.getDescontoManualValor()));

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO restaurarPedidoCancelado(Long id) {
        Pedido pedido = buscarPedidoEntityById(id);

        if (pedido.getStatus() != StatusPedido.CANCELADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Apenas pedidos cancelados podem ser restaurados"
            );
        }

        pedido.setStatus(StatusPedido.AGUARDANDO_APROVACAO);

        pagamentoService.resetarPagamentoParaPendente(pedido);

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

}
