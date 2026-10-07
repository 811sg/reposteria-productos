package com.reposteria.productos.patterns.decorator;

import com.reposteria.productos.model.Producto;

import java.math.BigDecimal;

/** Producto sin modificaciones. */
public class ProductoBase implements ProductoComponent {

    private final Producto producto;

    public ProductoBase(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String getNombre() {
        return producto.getNombre();
    }

    @Override
    public String getDescripcion() {
        return producto.getDescripcion();
    }

    @Override
    public BigDecimal getPrecio() {
        return producto.getPrecio();
    }
}
