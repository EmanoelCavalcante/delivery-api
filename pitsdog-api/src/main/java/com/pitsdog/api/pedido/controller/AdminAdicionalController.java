package com.pitsdog.api.pedido.controller;

import com.pitsdog.api.pedido.dto.AdicionalRequestDTO;
import com.pitsdog.api.pedido.dto.AdicionalResponseDTO;
import com.pitsdog.api.pedido.dto.AtualizarStatutsAdicionalDTO;
import com.pitsdog.api.pedido.service.AdicionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/adicionais")
public class AdminAdicionalController {

    private final AdicionalService adicionalService;

    public AdminAdicionalController(AdicionalService adicionalService) {
        this.adicionalService = adicionalService;
    }

    @PostMapping
    public ResponseEntity<AdicionalResponseDTO> createAdicional(
            @RequestBody AdicionalRequestDTO dto
            ){
        AdicionalResponseDTO adicional = adicionalService.createAdicional(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adicional);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdicionalResponseDTO> updateAdicional(
            @PathVariable Long id,
            @RequestBody AdicionalRequestDTO dto
    ){
        AdicionalResponseDTO adicionalAtualizado = adicionalService.updateAdicional(id, dto);

        return ResponseEntity.ok(adicionalAtualizado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdicionalResponseDTO> updateStatusAdicional(
            @PathVariable Long id,
            @RequestBody AtualizarStatutsAdicionalDTO status
    ){
        AdicionalResponseDTO statusAtualizado = adicionalService.updateStatus(id, status.ativo());

        return ResponseEntity.ok(statusAtualizado);
    }

    public ResponseEntity<Void> deleteAdicional(
            @PathVariable Long id
    ){
        adicionalService.deleteAdicional(id);

        return ResponseEntity.noContent().build();
    }
}
