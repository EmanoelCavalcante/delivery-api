package com.pitsdog.api.pedido.repository;

import com.pitsdog.api.pedido.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
