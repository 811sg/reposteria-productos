package com.reposteria.productos.patterns.template;

import com.reposteria.productos.dto.ProductoRequest;

/** Datos de entrada de una operación: id (si aplica) y datos del producto (si aplica). */
public record ProductoContexto(Integer id, ProductoRequest datos) {
}
