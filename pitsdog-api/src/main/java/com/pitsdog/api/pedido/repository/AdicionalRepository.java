package com.pitsdog.api.pedido.repository;

import com.pitsdog.api.pedido.entity.Adicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdicionalRepository extends JpaRepository<Adicional, Long>{

    List<Adicional> findByAtivoTrue();
}
