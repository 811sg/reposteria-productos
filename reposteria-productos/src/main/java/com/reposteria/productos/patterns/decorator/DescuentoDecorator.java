package com.reposteria.productos.patterns.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Agrega un descuento porcentual (0-100) al precio. */
public class DescuentoDecorator extends ProductoDecorator {

    private final int porcentaje;

    public DescuentoDecorator(ProductoComponent componente, int porcentaje) {
        super(componente);
        this.porcentaje = porcentaje;
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal factor = BigDecimal.valueOf(100 - porcentaje).divide(BigDecimal.valueOf(100));
        return componente.getPrecio().multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " | Descuento del " + porcentaje + "%";
    }
}
