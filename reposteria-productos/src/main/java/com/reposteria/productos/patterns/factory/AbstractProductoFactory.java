package com.reposteria.productos.patterns.factory;

import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.builder.ProductoBuilder;

/** Base común de las fábricas de un tipo concreto de producto: normaliza la categoría. */
public abstract class AbstractProductoFactory implements ProductoFactory {

    private final String tipo;

    protected AbstractProductoFactory(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean soporta(String categoria) {
        return categoria != null && categoria.trim().equalsIgnoreCase(tipo);
    }

    @Override
    public Producto crear(ProductoRequest d) {
        return new ProductoBuilder()
                .nombre(d.nombre())
                .descripcion(d.descripcion())
                .categoria(tipo) // categoría normalizada: "torta", "TORTA" -> "Torta"
                .precio(d.precio())
                .disponibilidad(d.disponibilidad())
                .imagen(d.imagen())
                .build();
    }
}
