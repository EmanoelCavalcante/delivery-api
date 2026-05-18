package com.pitsdog.api.cardapio.controller;

import com.pitsdog.api.cardapio.dto.CardapioResponseDTO;
import com.pitsdog.api.cardapio.service.CardapioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cardapio")
public class CardapioController {

    private final CardapioService cardapioService;

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    @GetMapping
    public ResponseEntity<CardapioResponseDTO> listarCardapio(){
       CardapioResponseDTO dto = cardapioService.listCardapio();
       return ResponseEntity.ok(dto);
    }
}
