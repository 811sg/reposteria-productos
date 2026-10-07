package com.reposteria.productos.patterns.decorator;

import java.math.BigDecimal;

/** Clase base de los decoradores: delega todo en el componente envuelto. */
public abstract class ProductoDecorator implements ProductoComponent {

    protected final ProductoComponent componente;

    protected ProductoDecorator(ProductoComponent componente) {
        this.componente = componente;
    }

    @Override
    public String getNombre() {
        return componente.getNombre();
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion();
    }

    @Override
    public BigDecimal getPrecio() {
        return componente.getPrecio();
    }
}
