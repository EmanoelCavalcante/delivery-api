package com.pitsdog.api.pedido.controller;

import com.pitsdog.api.pagamento.dto.ConfirmarPagamentoPedidoDTO;
import com.pitsdog.api.pagamento.service.PagamentoService;
import com.pitsdog.api.pedido.dto.*;
import com.pitsdog.api.pedido.entity.Pedido;
import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.pedido.enums.TipoPedido;
import com.pitsdog.api.pedido.service.ComandaService;
import com.pitsdog.api.pedido.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/pedidos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPedidoController {
    private final PedidoService pedidoService;
    private final ComandaService comandaService;
    private final PagamentoService pagamentoService;

    public AdminPedidoController(
            PedidoService pedidoService,
            ComandaService comandaService,
            PagamentoService pagamentoService
    ) {
        this.pedidoService = pedidoService;
        this.comandaService = comandaService;
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public ResponseEntity<Page<PedidoResumoResponseDTO>> listPedido(
            @PageableDefault(size = 20, sort = "momentoPedido", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) TipoPedido tipoPedido,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim
    ){
        Page<PedidoResumoResponseDTO> pedidos =
                pedidoService.listPedidosResumo(pageable, status, tipoPedido, dataInicio, dataFim);

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoById(@PathVariable Long id){
        PedidoResponseDTO pedido = pedidoService.buscarPedidoById(id);

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/mesa/{numeroMesa}")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidoByMesa(
            @PathVariable Integer numeroMesa
    ){
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidoByMesa(numeroMesa);

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}/comanda")
    public ResponseEntity<ComandaPedidoResponseDTO> gerarComanda(
            @PathVariable Long id
    ){
        PedidoDTO pedido = pedidoService.buscarPedidoDTOCompletoPorId(id);

        ComandaPedidoResponseDTO comanda = comandaService.gerarComanda(pedido);

        return ResponseEntity.ok(comanda);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> createPedido(
            @Valid @RequestBody CriarPedidoRequestDTO dto
    ){
        PedidoResponseDTO pedido = pedidoService.createPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> editPedido(
            @PathVariable Long id,
            @Valid @RequestBody CriarPedidoRequestDTO dto
            ){
        PedidoResponseDTO pedidoAtualizado = pedidoService.editarPedido(id, dto);

        return ResponseEntity.ok(pedidoAtualizado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusPedidoDTO status
    ){
        PedidoResponseDTO statusAtualizado = pedidoService.atualizarStatusPedido(id, status);

        return ResponseEntity.ok(statusAtualizado);
    }

    @PatchMapping("/{id}/pagamento/confirmar")
    public ResponseEntity<PedidoResponseDTO> confirmarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmarPagamentoPedidoDTO dto
    ){
        PedidoResponseDTO pedido = pagamentoService.confirmarPagamento(id, dto);

        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/pagamento/cancelar-confirmacao")
    public ResponseEntity<PedidoResponseDTO> cancelarConfirmacaoPagamento(
            @PathVariable Long id
    ){
        PedidoResponseDTO pedido = pagamentoService.cancelarConfirmacaoPagamento(id);

        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/restaurar")
    public ResponseEntity<PedidoResponseDTO> restaurarPedidoCancelado(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(pedidoService.restaurarPedidoCancelado(id));
    }

    @PatchMapping("/{id}/pagamento")
    public ResponseEntity<PedidoResponseDTO> atualizarFormaPagamento(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarPagamentoPedidoDTO pagamentoDTO
    ){
        PedidoResponseDTO pagamentoAtualizado = pedidoService.atualizarFormaDePagamento(id, pagamentoDTO);

        return ResponseEntity.ok(pagamentoAtualizado);
    }

    @PatchMapping("/{id}/desconto")
    public ResponseEntity<PedidoResponseDTO> aplicarDescontoManual(
            @PathVariable Long id,
            @Valid @RequestBody AplicarDescontoPedidoDTO dto
            ){
        PedidoResponseDTO descontoAplicado = pedidoService.aplicarDescontoManual(id, dto);

        return ResponseEntity.ok(descontoAplicado);
    }

    @PostMapping("/{pedidoId}/itens")
    public ResponseEntity<PedidoResponseDTO> adicionarItemAoPedido(
            @PathVariable Long pedidoId,
            @Valid @RequestBody ItemPedidoRequestDTO dto
    ){
        PedidoResponseDTO pedidoAtualizado = pedidoService.adicionarItemAoPedido(pedidoId, dto);

        return ResponseEntity.ok(pedidoAtualizado);
    }

    @PatchMapping("/{pedidoId}/itens/{itemId}/quantidade")
    public ResponseEntity<PedidoResponseDTO> atualizarQuantidadeItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @Valid @RequestBody AtualizarQuantidadeItemPedidoDTO dto
    ){
        PedidoResponseDTO pedidoAtualizado =
                pedidoService.atualizarQuantidadeItem(pedidoId, itemId, dto.getQuantidade());

        return ResponseEntity.ok(pedidoAtualizado);
    }

    @PutMapping("/{pedidoId}/itens/{itemId}/adicionais")
    public ResponseEntity<PedidoResponseDTO> atualizarAdicionaisDoItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @Valid @RequestBody List<ItemPedidoAdicionalRequestDTO> adicionalDTO
    ){
        PedidoResponseDTO adicionalAtualizado =
                pedidoService.atualizarAdicionaisDoItem(pedidoId, itemId, adicionalDTO);

        return ResponseEntity.ok(adicionalAtualizado);
    }

    @PatchMapping("/{pedidoId}/itens/{itemId}/observacao")
    public ResponseEntity<PedidoResponseDTO> atualizarObservacaoItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestBody String observacao
    ){
        PedidoResponseDTO observacaoAtualizada = pedidoService.atualizarObservacaoItem(pedidoId, itemId, observacao);

        return ResponseEntity.ok(observacaoAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
            @PathVariable Long id
    ){
        AtualizarStatusPedidoDTO dto = new AtualizarStatusPedidoDTO();

        dto.setStatus(StatusPedido.CANCELADO);
        PedidoResponseDTO pedidoCancelado = pedidoService.atualizarStatusPedido(id, dto);

        return ResponseEntity.ok(pedidoCancelado);
    }

    @DeleteMapping("/{pedidoId}/itens/{itemId}")
    public ResponseEntity<PedidoResponseDTO> deleteItemPedido(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId
    ){
        PedidoResponseDTO pedidoAtualizado = pedidoService.removerItemDoPedido(pedidoId, itemId);

        return ResponseEntity.ok(pedidoAtualizado);
    }
}
