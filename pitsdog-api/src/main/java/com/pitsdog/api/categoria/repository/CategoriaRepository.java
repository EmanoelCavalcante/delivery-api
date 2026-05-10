package com.pitsdog.api.categoria.repository;

import com.pitsdog.api.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByAtivoTrueOrderByOrdemAsc();
}

