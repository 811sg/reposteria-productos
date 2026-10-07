package com.reposteria.productos.repository;

import com.reposteria.productos.model.AuditoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<AuditoriaProducto, Long> {
    List<AuditoriaProducto> findTop50ByOrderByFechaDesc();
}
