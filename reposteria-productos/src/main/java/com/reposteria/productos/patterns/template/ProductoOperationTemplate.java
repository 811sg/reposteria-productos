package com.reposteria.productos.patterns.template;

import com.reposteria.productos.exception.RecursoNoEncontradoException;
import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.observer.ProductoEvento;
import com.reposteria.productos.patterns.observer.ProductoSubject;
import com.reposteria.productos.patterns.observer.TipoEvento;
import com.reposteria.productos.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PATRÓN TEMPLATE METHOD (comportamiento).
 * Define el flujo común: recibir -> validar -> procesar -> guardar -> notificar -> resultado.
 * Las subclases implementan solo los pasos que cambian.
 */
public abstract class ProductoOperationTemplate {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final ProductoRepository repository;
    protected final ProductoSubject subject;

    protected ProductoOperationTemplate(ProductoRepository repository, ProductoSubject subject) {
        this.repository = repository;
        this.subject = subject;
    }

    /** Método plantilla: el orden de los pasos no puede ser alterado por las subclases. */
    public final Producto ejecutar(ProductoContexto contexto) {
        recibir(contexto);
        validar(contexto);
        Producto procesado = procesar(contexto);
        Producto guardado = guardar(procesado);
        notificar(guardado);
        return guardado;
    }

    // 1. Recibir información
    protected void recibir(ProductoContexto contexto) {
        log.debug("Operación {} recibida (id={})", getClass().getSimpleName(), contexto.id());
    }

    // 2. Validar información (por defecto no valida; Registrar y Actualizar lo sobrescriben)
    protected void validar(ProductoContexto contexto) {
    }

    // 3. Procesar el producto (paso propio de cada operación)
    protected abstract Producto procesar(ProductoContexto contexto);

    // 4. Guardar los cambios
    protected Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    // 5. Notificar a los observadores
    protected void notificar(Producto producto) {
        subject.notificar(new ProductoEvento(evento(), producto));
    }

    protected abstract TipoEvento evento();

    protected Producto buscarExistente(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un producto con id " + id));
    }
}
