package com.reposteria.productos.patterns.strategy;

import com.reposteria.productos.model.Producto;
import com.reposteria.productos.repository.ProductoRepository;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class BusquedaPorCategoriaStrategy implements BusquedaProductoStrategy {

    private final ProductoRepository repository;

    public BusquedaPorCategoriaStrategy(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean aplica(String nombre, String categoria) {
        return nombre == null && categoria != null;
    }

    @Override
    public List<Producto> buscar(String nombre, String categoria) {
        return repository.findByCategoriaContainingIgnoreCase(categoria, Sort.by("nombre"));
    }
}
