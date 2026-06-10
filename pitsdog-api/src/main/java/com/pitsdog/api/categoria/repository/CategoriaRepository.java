package com.pitsdog.api.categoria.repository;

import com.pitsdog.api.categoria.entity.Categoria;
import com.pitsdog.api.pedido.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByAtivoTrueOrderByOrdemAsc();

    @Query("""
            select count(item) > 0
            from ItemPedido item
            where item.produto.categoria.id = :categoriaId
              and (
                    item.pedido.status is null
                    or item.pedido.status not in :statusFinais
                  )
            """)
    boolean existsVinculadaAPedidosEmAndamento(
            @Param("categoriaId") Long categoriaId,
            @Param("statusFinais") Collection<StatusPedido> statusFinais
    );
}
