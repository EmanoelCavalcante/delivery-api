package com.pitsdog.api.pedido.controller;

import com.pitsdog.api.pedido.dto.AtualizarStatusComboDTO;
import com.pitsdog.api.pedido.dto.ComboRequestDTO;
import com.pitsdog.api.pedido.dto.ComboResponseDTO;
import com.pitsdog.api.pedido.service.ComboService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/combos")
public class ComboAdminController {

    private final ComboService comboService;

    public ComboAdminController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    public ResponseEntity<List<ComboResponseDTO>> listarTodosAdmin() {
        List<ComboResponseDTO> combos = comboService.listarTodosAdmin();

        return ResponseEntity.ok(combos);
    }

    @PostMapping
    public ResponseEntity<ComboResponseDTO> criarCombo(
            @Valid @RequestBody ComboRequestDTO dto
    ) {
        ComboResponseDTO comboCriado = comboService.criarCombo(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(comboCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComboResponseDTO> editarCombo(
            @PathVariable Long id,
            @Valid @RequestBody ComboRequestDTO dto
    ) {
        ComboResponseDTO comboAtualizado = comboService.editarCombo(id, dto);

        return ResponseEntity.ok(comboAtualizado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ComboResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusComboDTO dto
    ) {
        ComboResponseDTO comboAtualizado =
                comboService.atualizarStatus(id, dto.getAtivo());

        return ResponseEntity.ok(comboAtualizado);
    }
}