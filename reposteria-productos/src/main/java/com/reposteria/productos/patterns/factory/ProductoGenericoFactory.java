package com.reposteria.productos.patterns.factory;

import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.builder.ProductoBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Fábrica por defecto: cualquier otra categoría de repostería (cupcake, brownie, etc.). */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ProductoGenericoFactory implements ProductoFactory {

    @Override
    public boolean soporta(String categoria) {
        return true;
    }

    @Override
    public Producto crear(ProductoRequest d) {
        return new ProductoBuilder()
                .nombre(d.nombre())
                .descripcion(d.descripcion())
                .categoria(d.categoria())
                .precio(d.precio())
                .disponibilidad(d.disponibilidad())
                .imagen(d.imagen())
                .build();
    }
}
