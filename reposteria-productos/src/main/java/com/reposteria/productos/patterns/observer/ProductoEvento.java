package com.reposteria.productos.patterns.observer;

import com.reposteria.productos.model.Producto;

public record ProductoEvento(TipoEvento tipo, Producto producto) {
}
