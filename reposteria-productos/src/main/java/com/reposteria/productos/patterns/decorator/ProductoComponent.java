package com.reposteria.productos.patterns.decorator;

import java.math.BigDecimal;

/** PATRÓN DECORATOR (estructural): interfaz común del producto y de sus decoradores. */
public interface ProductoComponent {
    String getNombre();

    String getDescripcion();

    BigDecimal getPrecio();
}
