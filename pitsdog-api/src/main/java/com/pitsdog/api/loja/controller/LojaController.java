package com.pitsdog.api.loja.controller;


import com.pitsdog.api.loja.dto.LojaStatusReponseDTO;
import com.pitsdog.api.loja.service.LojaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loja")
public class LojaController {
    private final LojaService service;

    public LojaController(LojaService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public ResponseEntity<LojaStatusReponseDTO> buscarStatus(){
        return ResponseEntity.ok(service.buscarStatus());
    }
}
