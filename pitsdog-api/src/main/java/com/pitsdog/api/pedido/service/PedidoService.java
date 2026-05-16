package com.pitsdog.api.pedido.service;

import com.pitsdog.api.pedido.dto.*;
import com.pitsdog.api.pedido.entity.*;
import com.pitsdog.api.pedido.repository.AdicionalRepository;
import com.pitsdog.api.pedido.repository.PedidoRepository;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final AdicionalRepository adicionalRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            AdicionalRepository adicionalRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.adicionalRepository = adicionalRepository;
    }

    private Pedido buscarPedidoEntityById(Long id){
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    private Produto buscarProdutoById(Long id){
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    private Adicional buscarAdicionalById(Long id){
        return adicionalRepository.findById(id)
                .orElseThrow(() ->new RuntimeException("Adicional não encontrado"));
    }

    private String limparTelefone(String telefone){
        if(telefone == null || telefone.isBlank()){
            return null;
        }
        return telefone.replaceAll("\\D", "");
    }

    private BigDecimal valorOurZero(BigDecimal valor){
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private Integer quantidadeOuUm(Integer quantidade){
        if (quantidade == null || quantidade <= 0){
            return 1;
        }
        return quantidade;
    }

    private BigDecimal calcularSubtotalItem(ItemPedido item){
        BigDecimal quantidade = BigDecimal.valueOf(quantidadeOuUm(item.getQuantidade()));

        BigDecimal subtotalProduto = item.getPrecoUnitario().multiply(quantidade);

        BigDecimal subtotalAdicionais = BigDecimal.ZERO;

        if(item.getAdicional() != null){
            for(ItemPedidoAdicional adicional : item.getAdicional()){
                subtotalAdicionais = subtotalAdicionais.add((valorOurZero(adicional.getSubtotal())));
            }
        }

        return subtotalProduto.add(subtotalAdicionais);
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
        BigDecimal descontoManutalPercentual = valorOurZero(pedido.getDescontoManualPercentual());

        BigDecimal descontoPercentualCalculado = BigDecimal.ZERO;

        if (descontoManutalPercentual.compareTo(BigDecimal.ZERO) > 0) {
            descontoPercentualCalculado = subtotal
                    .multiply(descontoManutalPercentual)
                    .divide(BigDecimal.valueOf(100));
        }


        BigDecimal descontoFidelidadeValor = valorOurZero(pedido.getDescontoFidelidadeValor());

        BigDecimal taxaEntrega = valorOurZero((pedido.getTaxaEntrega()));

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

    private PedidoResponseDTO converterParaResponseDTO(Pedido pedido){
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

        dto.setMomentoPedido(pedido.getMomentoPedido().atStartOfDay());
        dto.setPrevisaoRetirada(pedido.getPrevisaoRetirada());

        dto.setSubtotal(pedido.getSubtotal());

        dto.setDescontoManualPercentual(pedido.getDescontoManualPercentual());
        dto.setDescontoManualValor(pedido.getDescontoManualValor());

        dto.setDescontoFidelidadePercentual(pedido.getDescontoFidelidadePercentual());
        dto.setDescontoFidelidadeValor(pedido.getDescontoFidelidadeValor());

        dto.setTaxaEntrega(pedido.getTaxaEntrega());
        dto.setTotal(pedido.getTotal());

        dto.setFormaPagamento(pedido.getFormaPagamento());

        List<ItemPedidoResponseDTO> itensResponse = new ArrayList<>();

        if(pedido.getItens() != null){
            for(ItemPedido itemPedido : pedido.getItens()){
                itensResponse.add(converterItemParaResponseDTO(itemPedido));
            }
        }

        dto.setItens(itensResponse);

        return dto;
    }

    private ItemPedidoAdicionalResponseDTO converterAdicionalParaResponseDTO (ItemPedidoAdicional adicional){
        ItemPedidoAdicionalResponseDTO dto = new ItemPedidoAdicionalResponseDTO();

        dto.setId(adicional.getId());

        if(adicional.getNomeAdicional() != null){
            dto.setAdicionalId(adicional.getId());
        }
        dto.setNomeAdicional(adicional.getNomeAdicional());
        dto.setQuantidade(adicional.getQuantidade());
        dto.setPrecoUnitario(adicional.getPrecoUnitario());
        dto.setSubtotal(adicional.getSubtotal());

        return dto;
    }

    private ItemPedidoResponseDTO converterItemParaResponseDTO(ItemPedido item){
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();

        dto.setId(item.getId());

        if(item.getProduto() != null){
            dto.setProdutoId(item.getProduto().getId());
        }
        dto.setNomeProduto(item.getNomeProduto());
        dto.setObservacao(item.getObservacao());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());

        List<ItemPedidoAdicionalResponseDTO> adicionalResponse = new ArrayList<>();

        if(item.getAdicional() != null){
            for(ItemPedidoAdicional adicional : item.getAdicional()){
                adicionalResponse.add(converterAdicionalParaResponseDTO(adicional));
            }
        }

        dto.setAdicionais(adicionalResponse);

        return dto;
    }

    private List<ItemPedidoAdicional> converterAdicionaisParaEntity(
            List<ItemPedidoAdicionalRequestDTO> adicionaisDTO,
            ItemPedido itemPedido
    ){
        List<ItemPedidoAdicional> adicionais = new ArrayList<>();

        if(adicionaisDTO == null || adicionaisDTO.isEmpty()){
            return adicionais;
        }

        for(ItemPedidoAdicionalRequestDTO adicionalDTO : adicionaisDTO){
            Adicional adicional = buscarAdicionalById(adicionalDTO.getAdicionalId());

            Integer quantidade = quantidadeOuUm(adicionalDTO.getQuantidade());

            ItemPedidoAdicional itemPedidoAdicional = new ItemPedidoAdicional();

            itemPedidoAdicional.setItemPedido(itemPedido);
            itemPedidoAdicional.setAdicional(adicional);

            itemPedidoAdicional.setNomeAdicional(adicional.getNomeAdicional());
            itemPedidoAdicional.setPrecoUnitario(adicional.getPreco());
            itemPedidoAdicional.setQuantidade(quantidade);

            BigDecimal subtotalAdicional = adicional.getPreco()
                    .multiply(BigDecimal.valueOf(quantidade));

            itemPedidoAdicional.setSubtotal(subtotalAdicional);

            adicionais.add(itemPedidoAdicional);
        }
        return adicionais;
    }

    private List<ItemPedido> converterItensParaEntity(
            List<ItemPedidoRequestDTO> itensDTO,
            Pedido pedido
    ) {
        List<ItemPedido> itens = new ArrayList<>();

        if (itensDTO == null || itensDTO.isEmpty()) {
            throw new RuntimeException("Pedido precisa ter pelo menos um item");
        }

        for (ItemPedidoRequestDTO itemDTO : itensDTO) {
            ItemPedido itemPedido = new ItemPedido();

            Produto produto = buscarProdutoById(itemDTO.getProdutoId());

            Integer quantidade = quantidadeOuUm(itemDTO.getQuantidade());

            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);

            itemPedido.setNomeProduto(produto.getNome());
            itemPedido.setPrecoUnitario(produto.getPreco());
            itemPedido.setQuantidade(quantidade);
            itemPedido.setObservacao(itemDTO.getObservacao());

            List<ItemPedidoAdicional> adicionais = converterAdicionaisParaEntity(
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

    public PedidoResponseDTO createPedido(CriarPedidoRequestDTO dto){
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
        pedido.setStatus(StatusPedido.ABERTO);

        if(pedido.getTipoPedido() == TipoPedido.ENTREGA){
            pedido.setTaxaEntrega(valorOurZero(dto.getTaxaEntrega()));
        }
        else{
            pedido.setTaxaEntrega(valorOurZero(BigDecimal.ZERO));
        }

        pedido.setDescontoManualPercentual(valorOurZero(dto.getDescontoManualPercentual()));
        pedido.setDescontoManualValor(valorOurZero(dto.getDescontoManualValor()));

        pedido.setDescontoFidelidadePercentual(BigDecimal.ZERO);
        pedido.setDescontoFidelidadeValor(BigDecimal.ZERO);

        List<ItemPedido> itens = converterItensParaEntity(dto.getItens(), pedido);

        pedido.setItens(itens);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }


    public List<PedidoResponseDTO> listPedidos(){
        List<Pedido> pedidos = pedidoRepository.findAll();

        List<PedidoResponseDTO> response = new ArrayList<>();

        for (Pedido pedido : pedidos){
            response.add(converterParaResponseDTO(pedido));
        }

        return response;
    }

    public PedidoResponseDTO buscarPedidoById(Long id){
        Pedido pedido = buscarPedidoEntityById(id);

        return converterParaResponseDTO(pedido);
    }

    public List<PedidoResponseDTO> buscarPedidoByMesa(Integer numeroMesa){
        List<Pedido> pedidos = pedidoRepository.findByNumeroMesa(numeroMesa);

        List<PedidoResponseDTO> response = new ArrayList<>();

        for (Pedido pedido : pedidos){
            response.add(converterParaResponseDTO(pedido));
        }
        return response;
    }

    public PedidoResponseDTO editarPedido(Long id, CriarPedidoRequestDTO dto){
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

        if(pedido.getTipoPedido() == TipoPedido.ENTREGA){
            pedido.setTaxaEntrega(valorOurZero(dto.getTaxaEntrega()));
        }
        else{
            pedido.setTaxaEntrega(BigDecimal.ZERO);
        }

        pedido.setDescontoManualPercentual(valorOurZero(dto.getDescontoManualPercentual()));
        pedido.setDescontoManualValor(valorOurZero(dto.getDescontoManualValor()));

        pedido.getItens().clear();

        List<ItemPedido> novosItens = converterItensParaEntity(dto.getItens(), pedido);

        pedido.getItens().addAll(novosItens);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO arualizarSattusPedido (Long id, AtualizarStatusPedidoDTO dto){
        Pedido pedido = buscarPedidoEntityById(id);

        if(dto.getStatus() == null){
            throw new RuntimeException("Status não pode ser null");
        }

        pedido.setStatus(dto.getStatus());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

   public PedidoResponseDTO atualizarFormaDePagamento(Long id, AtualizarPagamentoPedidoDTO dto){
        Pedido pedido = buscarPedidoEntityById(id);

        if(dto.getFormaPagamento() == null){
            throw new RuntimeException("Forma de pagamento não pode ser null");
        }
       pedido.setFormaPagamento(dto.getFormaPagamento());

       Pedido pedidoAtualizado = pedidoRepository.save(pedido);

       return converterParaResponseDTO(pedidoAtualizado);
   }

   public PedidoResponseDTO aplicarDescontoManual (Long id, AplicarDescontoPedidoDTO dto){
        Pedido pedido = buscarPedidoEntityById(id);

       pedido.setDescontoManualPercentual(valorOurZero(dto.getDescontoManualPercentual()));
       pedido.setDescontoManualValor(valorOurZero(dto.getDescontoManualValor()));

       recalcularValores(pedido);

       Pedido pedidoAtualizado = pedidoRepository.save(pedido);

       return converterParaResponseDTO(pedidoAtualizado);

   }

    public PedidoResponseDTO adicionarItemAoPedido(Long pedidoId, ItemPedidoRequestDTO dto){
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        List<ItemPedidoRequestDTO> itensDTO = List.of(dto);

        List<ItemPedido> novosItens = converterItensParaEntity(itensDTO, pedido);

        pedido.getItens().addAll(novosItens);

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO removerItemDoPedido (Long pedidoId, Long itemId){
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        boolean removido = pedido.getItens().removeIf(item -> item.getId().equals(itemId));

        if(!removido){
            throw new RuntimeException("Item não encontrado neste pedido");
        }

        recalcularValores(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO atualizarQuantidadeItem(Long pedidoId, Long itemId, Integer quantidade){
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        for(ItemPedido item : pedido.getItens()){
            if(item.getId().equals(itemId)){
                item.setQuantidade(quantidadeOuUm(quantidade));
                recalcularValores(pedido);

                Pedido pedidoAtualizado = pedidoRepository.save(pedido);
                return converterParaResponseDTO(pedidoAtualizado);
            }
        }
        throw new RuntimeException("Item não encontrado neste pedido");
    }

    public PedidoResponseDTO atualizarObservacaoItem(Long pedidoId, Long itemId, String observacao){
        Pedido pedido = buscarPedidoEntityById(pedidoId);

        for(ItemPedido item : pedido.getItens()){
            if(item.getId().equals(itemId)){
                item.setObservacao(observacao);

                Pedido pedidoAtualizado = pedidoRepository.save(pedido);
                return converterParaResponseDTO(pedidoAtualizado);
            }
        }
        throw new RuntimeException("Item não encontrado neste pedido");
    }

    public void removerPedido(Long id){
        Pedido pedido = buscarPedidoEntityById(id);

        pedidoRepository.delete(pedido);
    }
}

