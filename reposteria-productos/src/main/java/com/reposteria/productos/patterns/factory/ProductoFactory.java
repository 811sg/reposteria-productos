package com.reposteria.productos.patterns.factory;

import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.model.Producto;

/** PATRÓN FACTORY METHOD (creacional): define la operación de creación de productos. */
public interface ProductoFactory {

    /** Indica si esta fábrica se encarga de la categoría recibida. */
    boolean soporta(String categoria);

    Producto crear(ProductoRequest datos);
}
