package com.pitsdog.api.pagamento.service;

import com.pitsdog.api.pagamento.dto.ConfirmarPagamentoPedidoDTO;
import com.pitsdog.api.pagamento.enums.StatusPagamento;
import com.pitsdog.api.pedido.dto.PedidoResponseDTO;
import com.pitsdog.api.pedido.entity.Pedido;
import com.pitsdog.api.pedido.enums.FormaPagamento;
import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.pedido.mapper.PedidoMapper;
import com.pitsdog.api.pedido.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    public PagamentoService(
            PedidoRepository pedidoRepository,
            PedidoMapper pedidoMapper
    ) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    private Pedido buscarPedidoById(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido não encontrado"
                ));
    }

    private void validarPedidoNaoCancelado(Pedido pedido) {
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pedido cancelado não pode ser editado. Restaure o pedido antes de editar."
            );
        }
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private BigDecimal calcularValorTroco(Pedido pedido, ConfirmarPagamentoPedidoDTO dto) {
        if (dto.getFormaPagamento() != FormaPagamento.DINHEIRO) {
            if (dto.getTrocoPara() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "trocoPara só pode ser informado quando formaPagamento for DINHEIRO"
                );
            }

            return null;
        }

        if (dto.getTrocoPara() == null) {
            return null;
        }

        if (dto.getTrocoPara().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "trocoPara deve ser maior que zero"
            );
        }

        BigDecimal total = valorOuZero(pedido.getTotal());

        if (dto.getTrocoPara().compareTo(total) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "trocoPara não pode ser menor que o total do pedido"
            );
        }

        return dto.getTrocoPara().subtract(total);
    }

    public void marcarPagamentoComoCancelado(Pedido pedido) {
        pedido.setStatusPagamento(StatusPagamento.CANCELADO);
        pedido.setPagamentoConfirmado(false);
        pedido.setMomentoPagamentoConfirmado(null);
        pedido.setTrocoPara(null);
        pedido.setValorTroco(null);
    }

    public void resetarPagamentoParaPendente(Pedido pedido) {
        pedido.setStatusPagamento(StatusPagamento.PENDENTE);
        pedido.setPagamentoConfirmado(false);
        pedido.setMomentoPagamentoConfirmado(null);
        pedido.setTrocoPara(null);
        pedido.setValorTroco(null);
    }

    @Transactional
    public PedidoResponseDTO confirmarPagamento(Long pedidoId, ConfirmarPagamentoPedidoDTO dto) {
        if (dto == null || dto.getFormaPagamento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Forma de pagamento é obrigatória para confirmar pagamento"
            );
        }

        Pedido pedido = buscarPedidoById(pedidoId);

        validarPedidoNaoCancelado(pedido);

        BigDecimal valorTroco = calcularValorTroco(pedido, dto);

        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setStatusPagamento(StatusPagamento.CONFIRMADO);
        pedido.setPagamentoConfirmado(true);
        pedido.setMomentoPagamentoConfirmado(LocalDateTime.now());
        pedido.setTrocoPara(dto.getTrocoPara());
        pedido.setValorTroco(valorTroco);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return pedidoMapper.toPedidoResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO cancelarConfirmacaoPagamento(Long pedidoId) {
        Pedido pedido = buscarPedidoById(pedidoId);

        validarPedidoNaoCancelado(pedido);

        resetarPagamentoParaPendente(pedido);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return pedidoMapper.toPedidoResponseDTO(pedidoAtualizado);
    }
}
