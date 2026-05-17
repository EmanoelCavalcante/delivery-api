package com.pitsdog.api.pedido.service;

import com.pitsdog.api.pedido.dto.AdicionalRequestDTO;
import com.pitsdog.api.pedido.dto.AdicionalResponseDTO;
import com.pitsdog.api.pedido.dto.ItemPedidoAdicionalRequestDTO;
import com.pitsdog.api.pedido.entity.Adicional;
import com.pitsdog.api.pedido.entity.ItemPedido;
import com.pitsdog.api.pedido.entity.ItemPedidoAdicional;
import com.pitsdog.api.pedido.repository.AdicionalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdicionalService {

    private final AdicionalRepository adicionalRepository;

    public AdicionalService(AdicionalRepository adicionalRepository) {
        this.adicionalRepository = adicionalRepository;
    }

    private Adicional buscarAdicionalEntityById(Long id) {
        return adicionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adicional não encontrado"));
    }

    private Integer quantidadeOuUm(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            return 1;
        }

        return quantidade;
    }

    private void validarAdicionalDTO(AdicionalRequestDTO dto) {
        if (dto.getNomeAdicional() == null || dto.getNomeAdicional().isBlank()) {
            throw new RuntimeException("Nome do adicional é obrigatório");
        }

        if (dto.getPreco() == null || dto.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Preço do adicional não pode ser negativo");
        }
    }

    private AdicionalResponseDTO toResponseDTO(Adicional adicional) {
        return new AdicionalResponseDTO(
                adicional.getId(),
                adicional.getNomeAdicional(),
                adicional.getPreco(),
                adicional.getAtivo()
        );
    }

    public List<ItemPedidoAdicional> converterAdicionaisParaItemPedido(
            List<ItemPedidoAdicionalRequestDTO> adicionaisDTO,
            ItemPedido itemPedido
    ) {
        List<ItemPedidoAdicional> adicionais = new ArrayList<>();

        if (adicionaisDTO == null || adicionaisDTO.isEmpty()) {
            return adicionais;
        }

        for (ItemPedidoAdicionalRequestDTO adicionalDTO : adicionaisDTO) {
            Adicional adicional = buscarAdicionalEntityById(adicionalDTO.getAdicionalId());

            Integer quantidade = quantidadeOuUm(adicionalDTO.getQuantidade());

            ItemPedidoAdicional itemPedidoAdicional = new ItemPedidoAdicional();

            itemPedidoAdicional.setItemPedido(itemPedido);
            itemPedidoAdicional.setAdicional(adicional);

            itemPedidoAdicional.setNomeAdicional(adicional.getNomeAdicional());
            itemPedidoAdicional.setPrecoUnitario(adicional.getPreco());
            itemPedidoAdicional.setQuantidade(quantidade);

            BigDecimal subtotalAdicional = adicional.getPreco()
                    .multiply(BigDecimal.valueOf(quantidade));

            itemPedidoAdicional.setSubtotal(subtotalAdicional);

            adicionais.add(itemPedidoAdicional);
        }

        return adicionais;
    }

    public AdicionalResponseDTO createAdicional(AdicionalRequestDTO dto) {
        validarAdicionalDTO(dto);

        Adicional adicional = new Adicional();

        adicional.setNomeAdicional(dto.getNomeAdicional());
        adicional.setPreco(dto.getPreco());

        if (dto.getAtivo() != null) {
            adicional.setAtivo(dto.getAtivo());
        } else {
            adicional.setAtivo(true);
        }

        Adicional adicionalSalvo = adicionalRepository.save(adicional);

        return toResponseDTO(adicionalSalvo);
    }

    public AdicionalResponseDTO getAdicionalById(Long id) {
        Adicional adicional = buscarAdicionalEntityById(id);

        return toResponseDTO(adicional);
    }

    public List<AdicionalResponseDTO> listAdicionaisAtivos() {
        return adicionalRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AdicionalResponseDTO> listAllAdicionais() {
        return adicionalRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AdicionalResponseDTO updateAdicional(Long id, AdicionalRequestDTO dto) {
        validarAdicionalDTO(dto);

        Adicional adicional = buscarAdicionalEntityById(id);

        adicional.setNomeAdicional(dto.getNomeAdicional());
        adicional.setPreco(dto.getPreco());

        if (dto.getAtivo() != null) {
            adicional.setAtivo(dto.getAtivo());
        }

        Adicional adicionalAtualizado = adicionalRepository.save(adicional);

        return toResponseDTO(adicionalAtualizado);
    }

    public AdicionalResponseDTO updateStatus(Long id, Boolean ativo) {
        if (ativo == null) {
            throw new RuntimeException("Status não pode ser null");
        }

        Adicional adicional = buscarAdicionalEntityById(id);

        adicional.setAtivo(ativo);

        Adicional adicionalAtualizado = adicionalRepository.save(adicional);

        return toResponseDTO(adicionalAtualizado);
    }

    public void deleteAdicional(Long id) {
        Adicional adicional = buscarAdicionalEntityById(id);

        adicionalRepository.delete(adicional);
    }
}