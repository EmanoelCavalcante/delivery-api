package com.pitsdog.api.pedido.controller;


import com.pitsdog.api.pedido.dto.CriarPedidoRequestDTO;
import com.pitsdog.api.pedido.dto.ItemPedidoAdicionalRequestDTO;
import com.pitsdog.api.pedido.dto.ItemPedidoRequestDTO;
import com.pitsdog.api.pedido.dto.PedidoResponseDTO;
import com.pitsdog.api.pedido.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

   @PostMapping
    public ResponseEntity<PedidoResponseDTO> createPedido(
            @RequestBody CriarPedidoRequestDTO dto
   ){
        PedidoResponseDTO pedidoCriado = pedidoService.createPedido(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoCriado);
   }

   @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoById(
            @PathVariable Long id
   ){
        PedidoResponseDTO pedido = pedidoService.buscarPedidoById(id);

        return ResponseEntity.ok(pedido);
   }

   @PostMapping("/{pedidoId}/itens")
    public ResponseEntity<PedidoResponseDTO> adicionarItemAoPedido(
           @PathVariable Long pedidoId,
           @RequestBody ItemPedidoRequestDTO dto
           ){
        PedidoResponseDTO pedidoAtualizado =
                pedidoService.adicionarItemAoPedido(pedidoId, dto);

        return ResponseEntity.ok(pedidoAtualizado);
   }
   @PatchMapping("/{pedidoId}/itens/{itemId}/quantidade")
   public ResponseEntity<PedidoResponseDTO> atualizarQuantidadeItem(
           @PathVariable Long pedidoId,
           @PathVariable Long itemId,
           @RequestBody Integer quantidade
   ){
        PedidoResponseDTO pedidoAtualizado = pedidoService.atualizarQuantidadeItem(pedidoId, itemId, quantidade);

        return ResponseEntity.ok(pedidoAtualizado);
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

   @PutMapping("/{pedidoId}/itens/{itemId}/adicionais")
   public ResponseEntity<PedidoResponseDTO> atualizarAdicionaisDoItem(
           @PathVariable Long pedidoId,
           @PathVariable Long itemId,
           @RequestBody List<ItemPedidoAdicionalRequestDTO> adicionaisDTO
   ){
        PedidoResponseDTO adicionalAtualizado = pedidoService.atualizarAdicionaisDoItem(pedidoId, itemId, adicionaisDTO);

        return ResponseEntity.ok(adicionalAtualizado);
   }

   @DeleteMapping("/{pedidoId}/itens/{itemId}")
   public ResponseEntity<PedidoResponseDTO> deleteItemPedido(
           @PathVariable Long pedidoId,
           @PathVariable Long itemId
   ){
        PedidoResponseDTO pedidoAtualizado =
                pedidoService.removerItemDoPedido(pedidoId, itemId);

        return ResponseEntity.ok(pedidoAtualizado);
   }

}

