package com.pitsdog.api.pedido.controller;

import com.pitsdog.api.pedido.dto.ComboResponseDTO;
import com.pitsdog.api.pedido.service.ComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/combos")
public class ComboController {

    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    public ResponseEntity<List<ComboResponseDTO>> listarCombosAtivos() {
        List<ComboResponseDTO> combos = comboService.listarCombosAtivos();

        return ResponseEntity.ok(combos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComboResponseDTO> buscarComboAtivoPorId(
            @PathVariable Long id
    ) {
        ComboResponseDTO combo = comboService.buscarComboAtivoPorId(id);

        return ResponseEntity.ok(combo);
    }
}