package com.pitsdog.api.pedido.repository;

import com.pitsdog.api.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}

