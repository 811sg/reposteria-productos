package com.reposteria.productos.patterns.observer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Sujeto observable: genera los eventos y avisa a todos los observadores registrados. */
@Component
public class ProductoSubject {

    private final List<ProductoObserver> observadores = new CopyOnWriteArrayList<>();

    /** Spring entrega todos los ProductoObserver existentes; agregar uno nuevo no exige tocar este código. */
    public ProductoSubject(List<ProductoObserver> iniciales) {
        this.observadores.addAll(iniciales);
    }

    public void registrar(ProductoObserver observador) {
        observadores.add(observador);
    }

    public void eliminar(ProductoObserver observador) {
        observadores.remove(observador);
    }

    public void notificar(ProductoEvento evento) {
        observadores.forEach(o -> o.actualizar(evento));
    }
}
