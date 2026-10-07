package com.reposteria.productos.patterns.template;

import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.factory.ProductoFactoryProvider;
import com.reposteria.productos.patterns.observer.ProductoSubject;
import com.reposteria.productos.patterns.observer.TipoEvento;
import com.reposteria.productos.repository.ProductoRepository;
import com.reposteria.productos.validation.ProductoValidator;
import org.springframework.stereotype.Component;

@Component
public class RegistrarProductoOperation extends ProductoOperationTemplate {

    private final ProductoValidator validator;
    private final ProductoFactoryProvider fabricas;

    public RegistrarProductoOperation(ProductoRepository repository, ProductoSubject subject,
                                      ProductoValidator validator, ProductoFactoryProvider fabricas) {
        super(repository, subject);
        this.validator = validator;
        this.fabricas = fabricas;
    }

    @Override
    protected void validar(ProductoContexto c) {
        validator.validar(c.datos(), null);
    }

    @Override
    protected Producto procesar(ProductoContexto c) {
        return fabricas.crear(c.datos()); // Factory Method + Builder
    }

    @Override
    protected TipoEvento evento() {
        return TipoEvento.CREADO;
    }
}
