package com.pitsdog.api.pedido.controller;

import com.pitsdog.api.pedido.dto.AtualizarStatusPedidoDTO;
import com.pitsdog.api.pedido.dto.PedidoRequestDTO;
import com.pitsdog.api.pedido.dto.PedidoResponseDTO;
import com.pitsdog.api.pedido.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminPedidoController {

    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/admin/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listPedidos(){
        List<PedidoResponseDTO> pedidos = pedidoService.listPedidos();

        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/admin/pedidos/{id}")
    public ResponseEntity<PedidoResponseDTO> editPedido(
            @PathVariable Long id,
            @RequestBody PedidoRequestDTO dto
    ){
        PedidoResponseDTO pedidoAtualizado = pedidoService.editarPedido(id, dto);

        return ResponseEntity.ok(pedidoAtualizado);
    }

    @PatchMapping("/admin/pedidos/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @RequestBody AtualizarStatusPedidoDTO dto
    ){

        PedidoResponseDTO pedidoAtualizado = pedidoService.atualizarStatusPedido(id, dto);

        return ResponseEntity.ok(pedidoAtualizado);
    }

    @DeleteMapping("/admin/pedidos/{id}")
    public ResponseEntity<Void> deleteProduto(
            @PathVariable Long id
    ){
        pedidoService.removerPedido(id);

        return ResponseEntity.noContent().build();
    }
}

