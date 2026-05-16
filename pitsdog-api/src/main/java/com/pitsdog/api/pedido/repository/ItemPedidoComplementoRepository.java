package com.pitsdog.api.pedido.repository;

import com.pitsdog.api.pedido.entity.ItemPedidoAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoComplementoRepository extends JpaRepository<ItemPedidoAdicional, Long> {
}
