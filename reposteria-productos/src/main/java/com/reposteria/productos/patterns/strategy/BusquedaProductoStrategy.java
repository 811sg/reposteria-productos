package com.reposteria.productos.patterns.strategy;

import com.reposteria.productos.model.Producto;

import java.util.List;

/** PATRÓN STRATEGY (comportamiento): familia de algoritmos de búsqueda intercambiables (RF-05). */
public interface BusquedaProductoStrategy {

    /** @return true si esta estrategia corresponde a los criterios recibidos (null = criterio no usado). */
    boolean aplica(String nombre, String categoria);

    List<Producto> buscar(String nombre, String categoria);
}
