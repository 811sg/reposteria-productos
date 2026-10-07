package com.reposteria.productos.repository;

import com.reposteria.productos.model.Producto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/** Todas las consultas se resuelven directamente en la base de datos (RNF-02). */
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre, Sort sort);

    List<Producto> findByCategoriaContainingIgnoreCase(String categoria, Sort sort);

    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaContainingIgnoreCase(
            String nombre, String categoria, Sort sort);

    List<Producto> findByDisponibilidadTrue(Sort sort);

    boolean existsByNombreIgnoreCaseAndIdProductoNot(String nombre, Integer idProducto);

    @Query("select distinct p.categoria from Producto p order by p.categoria")
    List<String> findCategorias();
}
