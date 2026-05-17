package com.pitsdog.api.produto.repository;

import com.pitsdog.api.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaId(Long categoriaId);

    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
}

