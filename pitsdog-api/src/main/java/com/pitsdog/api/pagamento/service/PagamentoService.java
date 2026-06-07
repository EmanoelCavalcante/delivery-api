package com.pitsdog.api.pagamento.service;

import com.pitsdog.api.pagamento.dto.PagamentoResponseDTO;
import com.pitsdog.api.pagamento.entity.Pagamento;
import com.pitsdog.api.pagamento.enums.ProvedorPagamento;
import com.pitsdog.api.pagamento.enums.StatusPagamento;
import com.pitsdog.api.pagamento.repository.PagamentoRepository;
import com.pitsdog.api.pedido.entity.Pedido;
import com.pitsdog.api.pedido.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    private Pedido buscarPedidoById(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido não encontrado"
                ));
    }

    private Pagamento buscarPagamentoById(Long pagamentoId) {
        return pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pagamento não encontrado"
                ));
    }

    private Pagamento buscarPagamentoByPedidoId(Long pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pagamento não encontrado para este pedido"
                ));
    }

    private Pagamento criarPagamentoManualEntity(Pedido pedido) {
        Pagamento pagamento = new Pagamento();

        pagamento.setPedido(pedido);
        pagamento.setProvedor(ProvedorPagamento.MANUAL);
        pagamento.setFormaPagamento(pedido.getFormaPagamento());
        pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
        pagamento.setValor(pedido.getTotal());

        return pagamento;
    }

    private PagamentoResponseDTO toResponseDTO(Pagamento pagamento) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();

        dto.setId(pagamento.getId());

        if (pagamento.getPedido() != null) {
            dto.setPedidoId(pagamento.getPedido().getId());
            dto.setNumeroPedido(pagamento.getPedido().getNumeroPedido());
        }

        dto.setProvedor(pagamento.getProvedor());
        dto.setFormaPagamento(pagamento.getFormaPagamento());
        dto.setStatusPagamento(pagamento.getStatusPagamento());
        dto.setValor(pagamento.getValor());
        dto.setPixQrCode(pagamento.getPixQrCode());
        dto.setPixCopiaECola(pagamento.getPixCopiaECola());
        dto.setCriadoEm(pagamento.getCriadoEm());
        dto.setAtualizadoEm(pagamento.getAtualizadoEm());
        dto.setPagoEm(pagamento.getPagoEm());
        dto.setExpiradoEm(pagamento.getExpiradoEm());
        dto.setCanceladoEm(pagamento.getCanceladoEm());

        return dto;
    }

    @Transactional
    public PagamentoResponseDTO criarPagamentoManual(Long pedidoId) {
        if (pagamentoRepository.existsByPedidoId(pedidoId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pagamento já existe para este pedido"
            );
        }

        Pedido pedido = buscarPedidoById(pedidoId);
        Pagamento pagamento = criarPagamentoManualEntity(pedido);
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        return toResponseDTO(pagamentoSalvo);
    }

    @Transactional
    public PagamentoResponseDTO confirmarPagamentoManual(Long pedidoId) {
        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedidoId)
                .orElseGet(() -> criarPagamentoManualEntity(buscarPedidoById(pedidoId)));

        if (pagamento.getStatusPagamento() == StatusPagamento.PAGO) {
            return toResponseDTO(pagamento);
        }

        pagamento.setStatusPagamento(StatusPagamento.PAGO);
        pagamento.setPagoEm(LocalDateTime.now());

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        return toResponseDTO(pagamentoSalvo);
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorPedido(Long pedidoId) {
        Pagamento pagamento = buscarPagamentoByPedidoId(pedidoId);

        return toResponseDTO(pagamento);
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorId(Long pagamentoId) {
        Pagamento pagamento = buscarPagamentoById(pagamentoId);

        return toResponseDTO(pagamento);
    }
}
