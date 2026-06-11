package com.pitsdog.api.pedido.repository;

import com.pitsdog.api.pedido.entity.Pedido;
import com.pitsdog.api.pedido.enums.StatusPedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    List<Pedido> findByNumeroMesa(Integer mesa);

    List<Pedido> findByTelefoneCliente(String telefoneCliente);

    Long countByTelefoneClienteAndStatus(String telefoneCliente, StatusPedido status);

    Long countByStatus(StatusPedido status);

    Optional<Pedido> findByNumeroMesaAndStatus(Integer numeroMesa, StatusPedido status);

    @EntityGraph(attributePaths = {
            "itens",
            "itens.produto",
            "itens.combo"
    })
    Optional<Pedido> findPedidoCompletoById(Long id);
}