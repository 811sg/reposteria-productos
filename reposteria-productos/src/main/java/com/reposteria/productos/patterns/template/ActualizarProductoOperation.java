package com.reposteria.productos.patterns.template;

import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.factory.ProductoFactoryProvider;
import com.reposteria.productos.patterns.observer.ProductoSubject;
import com.reposteria.productos.patterns.observer.TipoEvento;
import com.reposteria.productos.repository.ProductoRepository;
import com.reposteria.productos.validation.ProductoValidator;
import org.springframework.stereotype.Component;

@Component
public class ActualizarProductoOperation extends ProductoOperationTemplate {

    private final ProductoValidator validator;
    private final ProductoFactoryProvider fabricas;

    public ActualizarProductoOperation(ProductoRepository repository, ProductoSubject subject,
                                       ProductoValidator validator, ProductoFactoryProvider fabricas) {
        super(repository, subject);
        this.validator = validator;
        this.fabricas = fabricas;
    }

    @Override
    protected void validar(ProductoContexto c) {
        validator.validar(c.datos(), c.id());
    }

    @Override
    protected Producto procesar(ProductoContexto c) {
        Producto existente = buscarExistente(c.id());
        Producto nuevo = fabricas.crear(c.datos());
        existente.setNombre(nuevo.getNombre());
        existente.setDescripcion(nuevo.getDescripcion());
        existente.setCategoria(nuevo.getCategoria());
        existente.setPrecio(nuevo.getPrecio());
        existente.setDisponibilidad(nuevo.isDisponibilidad());
        if (nuevo.getImagen() != null) { // si no se envía imagen, se conserva la actual
            existente.setImagen(nuevo.getImagen());
        }
        return existente;
    }

    @Override
    protected TipoEvento evento() {
        return TipoEvento.ACTUALIZADO;
    }
}
