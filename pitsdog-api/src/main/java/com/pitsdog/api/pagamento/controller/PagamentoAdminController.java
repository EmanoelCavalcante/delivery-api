package com.pitsdog.api.pagamento.controller;

import com.pitsdog.api.pagamento.dto.PagamentoResponseDTO;
import com.pitsdog.api.pagamento.service.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/pagamentos")
public class PagamentoAdminController {

    private final PagamentoService pagamentoService;

    public PagamentoAdminController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagamentoResponseDTO> criarPagamentoManual(
            @PathVariable Long pedidoId
    ) {
        PagamentoResponseDTO pagamento = pagamentoService.criarPagamentoManual(pedidoId);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }

    @PatchMapping("/pedido/{pedidoId}/confirmar")
    public ResponseEntity<PagamentoResponseDTO> confirmarPagamentoManual(
            @PathVariable Long pedidoId
    ) {
        PagamentoResponseDTO pagamento = pagamentoService.confirmarPagamentoManual(pedidoId);

        return ResponseEntity.ok(pagamento);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorPedido(
            @PathVariable Long pedidoId
    ) {
        PagamentoResponseDTO pagamento = pagamentoService.buscarPorPedido(pedidoId);

        return ResponseEntity.ok(pagamento);
    }

    @GetMapping("/{pagamentoId}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorId(
            @PathVariable Long pagamentoId
    ) {
        PagamentoResponseDTO pagamento = pagamentoService.buscarPorId(pagamentoId);

        return ResponseEntity.ok(pagamento);
    }
}
