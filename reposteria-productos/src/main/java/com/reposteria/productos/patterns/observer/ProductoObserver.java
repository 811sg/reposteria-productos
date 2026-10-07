package com.reposteria.productos.patterns.observer;

/** PATRÓN OBSERVER (comportamiento): interfaz de los observadores de eventos de producto. */
public interface ProductoObserver {
    void actualizar(ProductoEvento evento);
}
