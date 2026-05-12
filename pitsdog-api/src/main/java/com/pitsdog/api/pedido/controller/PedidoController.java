package com.pitsdog.api.pedido.controller;


import com.pitsdog.api.pedido.dto.CriarPedidoRequestDTO;
import com.pitsdog.api.pedido.dto.PedidoResponseDTO;
import com.pitsdog.api.pedido.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/pedidos")
    public ResponseEntity<PedidoResponseDTO> createPedido(
            @RequestBody CriarPedidoRequestDTO dto
            ){
        PedidoResponseDTO pedidoCriado = pedidoService.createPedido(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoCriado);
    }
}

