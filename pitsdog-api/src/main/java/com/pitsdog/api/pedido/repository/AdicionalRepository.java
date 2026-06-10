package com.pitsdog.api.pedido.repository;

import com.pitsdog.api.pedido.entity.Adicional;
import com.pitsdog.api.pedido.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AdicionalRepository extends JpaRepository<Adicional, Long>{

    List<Adicional> findByAtivoTrue();

    @Query("""
            select count(itemAdicional) > 0
            from ItemPedidoAdicional itemAdicional
            where itemAdicional.adicional.id = :adicionalId
              and (
                    itemAdicional.itemPedido.pedido.status is null
                    or itemAdicional.itemPedido.pedido.status not in :statusFinais
                  )
            """)
    boolean existsVinculadoAPedidosEmAndamento(
            @Param("adicionalId") Long adicionalId,
            @Param("statusFinais") Collection<StatusPedido> statusFinais
    );
}
