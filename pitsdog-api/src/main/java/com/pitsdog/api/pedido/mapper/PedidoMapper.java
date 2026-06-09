package com.pitsdog.api.pedido.mapper;

import com.pitsdog.api.pedido.dto.ItemPedidoAdicionalDTO;
import com.pitsdog.api.pedido.dto.ItemPedidoDTO;
import com.pitsdog.api.pedido.dto.PedidoDTO;
import com.pitsdog.api.pedido.dto.PedidoResponseDTO;
import com.pitsdog.api.pedido.dto.PedidoResumoResponseDTO;
import com.pitsdog.api.pedido.entity.ItemPedido;
import com.pitsdog.api.pedido.entity.ItemPedidoAdicional;
import com.pitsdog.api.pedido.entity.Pedido;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoMapper {

    public PedidoDTO toPedidoDTO(Pedido pedido) {
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
        dto.setStatusPagamento(pedido.getStatusPagamento());
        dto.setPagamentoConfirmado(pedido.getPagamentoConfirmado());
        dto.setMomentoPagamentoConfirmado(pedido.getMomentoPagamentoConfirmado());
        dto.setTrocoPara(pedido.getTrocoPara());
        dto.setValorTroco(pedido.getValorTroco());
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
        dto.setItens(toItemPedidoDTOList(pedido.getItens()));

        return dto;
    }

    public PedidoResponseDTO toPedidoResponseDTO(Pedido pedido) {
        PedidoDTO pedidoDTO = toPedidoDTO(pedido);
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
        dto.setStatusPagamento(pedidoDTO.getStatusPagamento());
        dto.setPagamentoConfirmado(pedidoDTO.getPagamentoConfirmado());
        dto.setMomentoPagamentoConfirmado(pedidoDTO.getMomentoPagamentoConfirmado());
        dto.setTrocoPara(pedidoDTO.getTrocoPara());
        dto.setValorTroco(pedidoDTO.getValorTroco());
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

    public PedidoResumoResponseDTO toPedidoResumoResponseDTO(Pedido pedido) {
        PedidoResumoResponseDTO dto = new PedidoResumoResponseDTO();

        dto.setId(pedido.getId());
        dto.setNumeroPedido(pedido.getNumeroPedido());
        dto.setTipoPedido(pedido.getTipoPedido());
        dto.setNumeroMesa(pedido.getNumeroMesa());
        dto.setNomeCliente(pedido.getNomeCliente());
        dto.setTelefoneCliente(pedido.getTelefoneCliente());
        dto.setStatus(pedido.getStatus());
        dto.setStatusPagamento(pedido.getStatusPagamento());
        dto.setPagamentoConfirmado(pedido.getPagamentoConfirmado());
        dto.setMomentoPagamentoConfirmado(pedido.getMomentoPagamentoConfirmado());
        dto.setTrocoPara(pedido.getTrocoPara());
        dto.setValorTroco(pedido.getValorTroco());
        dto.setMomentoPedido(pedido.getMomentoPedido());
        dto.setTotal(pedido.getTotal());
        dto.setFormaPagamento(pedido.getFormaPagamento());

        return dto;
    }

    public ItemPedidoDTO toItemPedidoDTO(ItemPedido item) {
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
        dto.setAdicionais(toItemPedidoAdicionalDTOList(item.getAdicional()));

        return dto;
    }

    public ItemPedidoAdicionalDTO toItemPedidoAdicionalDTO(ItemPedidoAdicional adicional) {
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

    private List<ItemPedidoDTO> toItemPedidoDTOList(List<ItemPedido> itens) {
        List<ItemPedidoDTO> itensDTO = new ArrayList<>();

        if (itens != null) {
            for (ItemPedido item : itens) {
                itensDTO.add(toItemPedidoDTO(item));
            }
        }

        return itensDTO;
    }

    private List<ItemPedidoAdicionalDTO> toItemPedidoAdicionalDTOList(List<ItemPedidoAdicional> adicionais) {
        List<ItemPedidoAdicionalDTO> adicionaisDTO = new ArrayList<>();

        if (adicionais != null) {
            for (ItemPedidoAdicional adicional : adicionais) {
                adicionaisDTO.add(toItemPedidoAdicionalDTO(adicional));
            }
        }

        return adicionaisDTO;
    }
}
