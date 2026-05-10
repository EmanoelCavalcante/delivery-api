package com.pitsdog.api.produto.repository;

import com.pitsdog.api.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

