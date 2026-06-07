package com.pitsdog.api.pedido.service;

import com.pitsdog.api.pedido.dto.*;
import com.pitsdog.api.pedido.enums.TipoItemPedido;
import com.pitsdog.api.pedido.enums.TipoPedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ComandaService {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(PT_BR);
    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    @Transactional(readOnly = true)
    public ComandaPedidoResponseDTO gerarComanda(PedidoDTO pedidoDTO){
        ComandaPedidoResponseDTO dto = new ComandaPedidoResponseDTO();

        dto.setPedidoId(pedidoDTO.getId());
        dto.setNumeroPedido(pedidoDTO.getNumeroPedido());
        dto.setMomentoPedido(pedidoDTO.getMomentoPedido());
        dto.setStatus(pedidoDTO.getStatus());
        dto.setTipoPedido(pedidoDTO.getTipoPedido());

        dto.setNomeCliente(pedidoDTO.getNomeCliente());
        dto.setTelefoneCliente(pedidoDTO.getTelefoneCliente());

        dto.setNumeroMesa(pedidoDTO.getNumeroMesa());
        dto.setBairroEntrega(pedidoDTO.getBairroEntrega());
        dto.setRuaEntrega(pedidoDTO.getRuaEntrega());
        dto.setNumeroCasa(pedidoDTO.getNumeroCasa());
        dto.setComplemento(pedidoDTO.getComplemento());

        dto.setObservacao(pedidoDTO.getObservacao());
        dto.setFormaPagamento(pedidoDTO.getFormaPagamento());

        dto.setSubtotal(valorOuZero(pedidoDTO.getSubtotal()));
        dto.setTaxaEntrega(valorOuZero(pedidoDTO.getTaxaEntrega()));
        dto.setDescontoManualValor(valorOuZero(pedidoDTO.getDescontoManualValor()));
        dto.setDescontoFidelidadeValor(valorOuZero(pedidoDTO.getDescontoFidelidadeValor()));
        dto.setTotal(valorOuZero(pedidoDTO.getTotal()));

        List<ItemComandaDTO> itens = new ArrayList<>();

        for(ItemPedidoDTO item : pedidoDTO.getItens()){
            ItemComandaDTO itemDTO = new ItemComandaDTO();

            itemDTO.setItemId(item.getId());
            itemDTO.setTipoItem(item.getTipoItem());

           if(item.getProdutoId() != null){
               itemDTO.setProdutoId(item.getProdutoId());
           }

            if(item.getComboId() != null){
                itemDTO.setComboId(item.getComboId());
            }

            itemDTO.setNomeItem(item.getNomeProduto());
            itemDTO.setQuantidade(item.getQuantidade());
            itemDTO.setPrecoUnitario(valorOuZero(item.getPrecoUnitario()));
            itemDTO.setSubtotal(valorOuZero(item.getSubtotal()));
            itemDTO.setObservacao(item.getObservacao());

            itemDTO.setAdicionais(item.getAdicionais()
                    .stream()
                    .map(adicional -> adicional.getNomeAdicional())
                    .toList()
            );

            itens.add(itemDTO);
        }

        dto.setItens(itens);
        dto.setTextoImpressao(gerarTextoImpressao(dto));

        return dto;
    }

    private BigDecimal valorOuZero (BigDecimal valor){
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String gerarTextoImpressao(ComandaPedidoResponseDTO dto){
        StringBuilder sb = new StringBuilder();

        sb.append("================================\n");
        sb.append("            PITS DOG\n");
        sb.append("================================\n");

        sb.append("PEDIDO Nº ").append(dto.getNumeroPedido()).append("\n");
        sb.append("STATUS: ").append(dto.getStatus()).append("\n");

        if(dto.getMomentoPedido() != null){
            sb.append("DATA: ")
                    .append(dto.getMomentoPedido().format(DATA_HORA))
                    .append("\n");
        }

        sb.append("\n");
        sb.append("TIPO: ").append(dto.getTipoPedido()).append("\n");

        if(dto.getTipoPedido() == TipoPedido.MESA){
            sb.append("MESA: ").append(dto.getNumeroMesa()).append("\n");
        }

        sb.append("\n");

        if(dto.getNomeCliente() != null && !dto.getNomeCliente().isBlank()){
            sb.append("CLIENTE: ").append(dto.getNomeCliente());
        }

        if(dto.getTelefoneCliente() != null && !dto.getTelefoneCliente().isBlank()){
            sb.append("TEL: ").append(dto.getTelefoneCliente());
        }

        if(dto.getTipoPedido() == TipoPedido.ENTREGA){
            sb.append("\n");
            sb.append("ENDEREÇO:\n");

            if(dto.getRuaEntrega() != null){
                sb.append(dto.getRuaEntrega());
            }

            if(dto.getNumeroCasa() != null){
                sb.append(", Nº ").append(dto.getNumeroMesa());
            }

            sb.append("\n");


            if(dto.getBairroEntrega() != null){
                sb.append("Bairro: ").append(dto.getBairroEntrega()).append("\n");
            }

            if(dto.getComplemento() != null && !dto.getComplemento().isBlank()){
                sb.append("Complemento: ").append(dto.getComplemento()).append("\n");
            }
        }

        if(dto.getObservacao() != null && !dto.getObservacao().isBlank()){
            sb.append("\n");
            sb.append("OBS PEDIDO: ").append(dto.getObservacao()).append("\n");
        }
        sb.append("\n");
        sb.append("--------------------------------\n");
        sb.append("ITENS\n");
        sb.append("--------------------------------\n");

        for (ItemComandaDTO item : dto.getItens()) {
        sb.append(item.getQuantidade())
                .append("x ")
                .append(item.getNomeItem())
                .append("\n");

        if (item.getTipoItem() == TipoItemPedido.COMBO) {
            sb.append("   Tipo: COMBO\n");
        }

        if (item.getAdicionais() != null && !item.getAdicionais().isEmpty()) {
            for (String adicional : item.getAdicionais()) {
                sb.append("   + ").append(adicional).append("\n");
            }
        }

        if (item.getObservacao() != null && !item.getObservacao().isBlank()) {
            sb.append("   Obs: ").append(item.getObservacao()).append("\n");
        }

        sb.append("   ")
                .append(MOEDA.format(item.getSubtotal()))
                .append("\n\n");
    }

    BigDecimal descontoTotal =
            valorOuZero(dto.getDescontoManualValor())
                    .add(valorOuZero(dto.getDescontoFidelidadeValor()));

        sb.append("--------------------------------\n");
        sb.append("SUBTOTAL: ").append(MOEDA.format(dto.getSubtotal())).append("\n");
        sb.append("ENTREGA:  ").append(MOEDA.format(dto.getTaxaEntrega())).append("\n");
        sb.append("DESCONTO: ").append(MOEDA.format(descontoTotal)).append("\n");
        sb.append("TOTAL:    ").append(MOEDA.format(dto.getTotal())).append("\n");

        sb.append("\n");
        sb.append("PAGAMENTO: ").append(dto.getFormaPagamento()).append("\n");

        sb.append("================================\n");

        return sb.toString();
    }
}
