package com.reposteria.productos.patterns.template;

import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.observer.ProductoSubject;
import com.reposteria.productos.patterns.observer.TipoEvento;
import com.reposteria.productos.repository.ProductoRepository;
import org.springframework.stereotype.Component;

/** Permite volver a habilitar un producto desactivado (control de disponibilidad). */
@Component
public class ActivarProductoOperation extends ProductoOperationTemplate {

    public ActivarProductoOperation(ProductoRepository repository, ProductoSubject subject) {
        super(repository, subject);
    }

    @Override
    protected Producto procesar(ProductoContexto c) {
        Producto p = buscarExistente(c.id());
        p.setDisponibilidad(true);
        return p;
    }

    @Override
    protected TipoEvento evento() {
        return TipoEvento.ACTIVADO;
    }
}
