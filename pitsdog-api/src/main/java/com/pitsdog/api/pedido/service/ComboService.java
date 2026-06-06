package com.pitsdog.api.pedido.service;

import com.pitsdog.api.pedido.dto.ComboRequestDTO;
import com.pitsdog.api.pedido.dto.ComboResponseDTO;
import com.pitsdog.api.pedido.entity.Combo;
import com.pitsdog.api.pedido.repository.ComboRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComboService {

    private final ComboRepository comboRepository;

    public ComboService(ComboRepository comboRepository) {
        this.comboRepository = comboRepository;
    }

    public List<ComboResponseDTO> listarCombosAtivos() {
        List<Combo> combos = comboRepository.findAll();

        List<ComboResponseDTO> response = new ArrayList<>();

        for (Combo combo : combos) {
            response.add(converterParaResponseDTO(combo));
        }

        return response;
    }

    public ComboResponseDTO buscarComboAtivoPorId(Long id) {
        Combo combo = buscarComboEntityById(id);

        if (!Boolean.TRUE.equals(combo.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Combo não encontrado ou indisponível"
            );
        }

        return converterParaResponseDTO(combo);
    }

    public List<ComboResponseDTO> listarTodosAdmin() {
        List<Combo> combos = comboRepository.findAll();

        List<ComboResponseDTO> response = new ArrayList<>();

        for (Combo combo : combos) {
            response.add(converterParaResponseDTO(combo));
        }

        return response;
    }

    public ComboResponseDTO criarCombo(ComboRequestDTO dto) {
        validarComboRequest(dto);

        Combo combo = new Combo();

        combo.setNome(dto.getNome());
        combo.setDescricao(dto.getDescricao());
        combo.setPreco(dto.getPreco());
        combo.setImagemUrl(dto.getImagemUrl());
        combo.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        Combo comboSalvo = comboRepository.save(combo);

        return converterParaResponseDTO(comboSalvo);
    }

    public ComboResponseDTO editarCombo(Long id, ComboRequestDTO dto) {
        validarComboRequest(dto);

        Combo combo = buscarComboEntityById(id);

        combo.setNome(dto.getNome());
        combo.setDescricao(dto.getDescricao());
        combo.setPreco(dto.getPreco());
        combo.setImagemUrl(dto.getImagemUrl());

        if (dto.getAtivo() != null) {
            combo.setAtivo(dto.getAtivo());
        }

        Combo comboAtualizado = comboRepository.save(combo);

        return converterParaResponseDTO(comboAtualizado);
    }

    public ComboResponseDTO atualizarStatus(Long id, Boolean ativo) {
        if (ativo == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status ativo é obrigatório"
            );
        }

        Combo combo = buscarComboEntityById(id);

        combo.setAtivo(ativo);

        Combo comboAtualizado = comboRepository.save(combo);

        return converterParaResponseDTO(comboAtualizado);
    }

    private Combo buscarComboEntityById(Long id) {
        return comboRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Combo não encontrado"
                ));
    }

    private void validarComboRequest(ComboRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome do combo é obrigatório"
            );
        }

        if (dto.getPreco() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Preço do combo é obrigatório"
            );
        }

        if (dto.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Preço do combo não pode ser negativo"
            );
        }
    }

    private ComboResponseDTO converterParaResponseDTO(Combo combo) {
        ComboResponseDTO dto = new ComboResponseDTO();

        dto.setId(combo.getId());
        dto.setNome(combo.getNome());
        dto.setDescricao(combo.getDescricao());
        dto.setPreco(combo.getPreco());
        dto.setImagemUrl(combo.getImagemUrl());
        dto.setAtivo(combo.getAtivo());

        return dto;
    }
}