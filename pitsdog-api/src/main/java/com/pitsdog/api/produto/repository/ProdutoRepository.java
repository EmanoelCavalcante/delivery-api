package com.pitsdog.api.produto.repository;

import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByAtivoTrueAndCategoriaAtivoTrue();

    Optional<Produto> findByIdAndAtivoTrueAndCategoriaAtivoTrue(Long id);

    List<Produto> findByCategoriaId(Long categoriaId);

    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);

    List<Produto> findByCategoriaIdAndAtivoTrueOrderByNomeAsc(Long categoriaId);

    @Query("""
            select count(item) > 0
            from ItemPedido item
            where item.produto.id = :produtoId
              and (
                    item.pedido.status is null
                    or item.pedido.status not in :statusFinais
                  )
            """)
    boolean existsVinculadoAPedidosEmAndamento(
            @Param("produtoId") Long produtoId,
            @Param("statusFinais") Collection<StatusPedido> statusFinais
    );
}
