package com.reposteria.productos.dto;

import java.math.BigDecimal;

/** Producto "decorado" con descuentos / promociones (patrón Decorator). */
public record ProductoPresentacion(
        Integer idProducto,
        String nombre,
        String descripcion,
        BigDecimal precioOriginal,
        BigDecimal precioFinal) {
}
