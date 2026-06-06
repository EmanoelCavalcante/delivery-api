package com.pitsdog.api.pedido.repository;

import com.pitsdog.api.pedido.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComboRepository extends JpaRepository<Combo, Long> {

    List<Combo> findByAtivoTrue();
}

