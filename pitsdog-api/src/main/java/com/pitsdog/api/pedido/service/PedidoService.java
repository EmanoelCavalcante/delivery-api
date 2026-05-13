package com.pitsdog.api.pedido.service;

import com.pitsdog.api.pedido.dto.*;
import com.pitsdog.api.pedido.entity.ItemPedido;
import com.pitsdog.api.pedido.entity.Pedido;
import com.pitsdog.api.pedido.entity.StatusPedido;
import com.pitsdog.api.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    private ItemPedidoResponseDTO converterItemParaResponseDTO(ItemPedido item){
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();

        dto.setId(item.getId());
        dto.setNomeProduto(item.getNomeProduto());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());

        return dto;
    }

    private Pedido buscarPedidoEntityById(Long id){
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    private PedidoResponseDTO converterParaResponseDTO(Pedido pedido){
        PedidoResponseDTO dto = new PedidoResponseDTO();

        dto.setId(pedido.getId());
        dto.setNomeCliente(pedido.getNomeCliente());
        dto.setTelefoneCliente(pedido.getTelefoneCliente());
        dto.setBairroEntrega(pedido.getBairroEntrega());
        dto.setRuaEntrega(pedido.getRuaEntrega());
        dto.setNumeroCasa(pedido.getNumeroCasa());
        dto.setComplemento(pedido.getComplmeneto());
        dto.setFormaPagamento(pedido.getFormaPagamento());
        dto.setSubtotal(pedido.getSubtotal());
        dto.setTaxaEntrega(pedido.getTaxaEntrega());
        dto.setTotal(pedido.getTotal());
        dto.setStatus(pedido.getStatus());
        dto.setCriadoEm(pedido.getCriadoEm().atStartOfDay());

        List<ItemPedidoResponseDTO> itensResponse = new ArrayList<>();

        for(ItemPedido itemPedido :  pedido.getItens()){
            itensResponse.add(converterItemParaResponseDTO(itemPedido));
        }

        dto.setItens(itensResponse);

        return dto;
    }

    private BigDecimal calcularSubtotal(List<ItemPedido> itens) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            subtotal = subtotal.add(item.getSubtotal());
        }

        return subtotal;
    }

    private List<ItemPedido> converterItensParaEntity(
            List<ItemPedidoRequestDTO> itensDTO,
            Pedido pedido
    ){
        List<ItemPedido> itens = new ArrayList<>();

        for(ItemPedidoRequestDTO itemDTO : itensDTO){
            ItemPedido itemPedido = new ItemPedido();

            itemPedido.setNomeProduto(itemDTO.getNomeProduto());
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());

            BigDecimal subTotalItem = itemDTO.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));

            itemPedido.setSubTotal(subTotalItem);
            itemPedido.setPedido(pedido);

            itens.add(itemPedido);
        }
        return itens;
    }

    private String limparTelefone(String telefone){
        return telefone.replaceAll("\\D", "");
    }

    public PedidoResponseDTO createPedido(CriarPedidoRequestDTO dto){
        Pedido pedido = new Pedido();

        pedido.setNomeCliente(dto.getNomeCliente());
        pedido.setTelefoneCliente(limparTelefone(dto.getTelefoneCliente()));
        pedido.setBairroEntrega((dto.getBairroEntrega()));
        pedido.setRuaEntrega(dto.getRuaEntrega());
        pedido.setNumeroCasa(dto.getNumeroCasa());
        pedido.setComplmeneto(dto.getComplemento());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setTaxaEntrega(dto.getTaxaEntrega());
        pedido.setStatus(StatusPedido.AGUARDANDO_APROVACAO);

        List<ItemPedido> itens = converterItensParaEntity(dto.getItens(), pedido);

        BigDecimal subTotal = calcularSubtotal(itens);
        BigDecimal total = subTotal.add(dto.getTaxaEntrega());

        pedido.setItens(itens);
        pedido.setSubtotal(subTotal);
        pedido.setTotal(total);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoSalvo);
    }

    public List<PedidoResponseDTO> listPedidos(){
        List<Pedido> pedidos = pedidoRepository.findAll();

        List<PedidoResponseDTO> pedidoResponse = new ArrayList<>();

        for(Pedido pedido : pedidos){
            pedidoResponse.add(converterParaResponseDTO(pedido));
        }

        return pedidoResponse;
    }

    public PedidoResponseDTO buscarPedidoById(Long id){
        Pedido pedido = buscarPedidoEntityById(id);

        return converterParaResponseDTO(pedido);
    }

    public PedidoResponseDTO editarPedido(Long id, PedidoRequestDTO dto){
        Pedido pedido = buscarPedidoEntityById(id);

        pedido.setNomeCliente(dto.getNomeCliente());
        pedido.setTelefoneCliente(limparTelefone(dto.getTelefoneCliente()));
        pedido.setBairroEntrega(dto.getBairroEntrega());
        pedido.setRuaEntrega(dto.getRuaEntrega());
        pedido.setNumeroCasa(dto.getNumeroCasa());
        pedido.setComplmeneto(dto.getComplemento());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setTaxaEntrega(dto.getTaxaEntrega());

        pedido.getItens().clear();

        List<ItemPedido> novosItens = converterItensParaEntity(dto.getItens(), pedido);

        BigDecimal subtotal = calcularSubtotal(novosItens);
        BigDecimal total = subtotal.add(dto.getTaxaEntrega());

        pedido.getItens().addAll(novosItens);
        pedido.setSubtotal(subtotal);
        pedido.setTotal(total);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO atualizarStatusPedido(Long id, AtualizarStatusPedidoDTO dto){
        Pedido pedido = buscarPedidoEntityById(id);

        if (dto.getStatus() == null){
            throw new RuntimeException("Status não pode ser null");
        }
        pedido.setStatus(dto.getStatus());


        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponseDTO(pedidoAtualizado);
    }

    public void removerPedido(Long id){
        Pedido pedido = buscarPedidoEntityById(id);

        pedidoRepository.delete(pedido);
    }
}

