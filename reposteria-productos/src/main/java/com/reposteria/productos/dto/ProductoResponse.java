package com.reposteria.productos.dto;

import com.reposteria.productos.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(
        Integer idProducto,
        String nombre,
        String descripcion,
        String categoria,
        BigDecimal precio,
        boolean disponibilidad,
        String imagen,
        LocalDateTime fechaRegistro) {

    public static ProductoResponse desde(Producto p) {
        return new ProductoResponse(p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getCategoria(),
                p.getPrecio(), p.isDisponibilidad(), p.getImagen(), p.getFechaRegistro());
    }
}
