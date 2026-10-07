package com.reposteria.productos.patterns.template;

import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.observer.ProductoSubject;
import com.reposteria.productos.patterns.observer.TipoEvento;
import com.reposteria.productos.repository.ProductoRepository;
import org.springframework.stereotype.Component;

/** RF-04: desactiva el producto conservando su información. */
@Component
public class DesactivarProductoOperation extends ProductoOperationTemplate {

    public DesactivarProductoOperation(ProductoRepository repository, ProductoSubject subject) {
        super(repository, subject);
    }

    @Override
    protected Producto procesar(ProductoContexto c) {
        Producto p = buscarExistente(c.id());
        p.setDisponibilidad(false);
        return p;
    }

    @Override
    protected TipoEvento evento() {
        return TipoEvento.DESACTIVADO;
    }
}
