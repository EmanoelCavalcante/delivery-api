package com.pitsdog.api.pedido.controller;

import com.pitsdog.api.pedido.dto.AdicionalResponseDTO;
import com.pitsdog.api.pedido.service.AdicionalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adicionais")
public class AdicionalController {

    private final AdicionalService adicionalService;

    public AdicionalController(AdicionalService adicionalService) {
        this.adicionalService = adicionalService;
    }

    @GetMapping
    public ResponseEntity<List<AdicionalResponseDTO>> listAdicionaisAtivos(){
        return ResponseEntity.ok(adicionalService.listAdicionaisAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdicionalResponseDTO> getAdicionalById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(adicionalService.getAdicionalById(id));
    }
}
