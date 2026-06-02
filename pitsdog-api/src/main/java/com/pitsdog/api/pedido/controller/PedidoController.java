package com.pitsdog.api.pedido.controller;


import com.pitsdog.api.pedido.dto.CriarPedidoRequestDTO;
import com.pitsdog.api.pedido.dto.ItemPedidoAdicionalRequestDTO;
import com.pitsdog.api.pedido.dto.ItemPedidoRequestDTO;
import com.pitsdog.api.pedido.dto.PedidoResponseDTO;
import com.pitsdog.api.pedido.service.PedidoService;
import jakarta.validation.Valid;
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
            @Valid @RequestBody CriarPedidoRequestDTO dto
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

}
