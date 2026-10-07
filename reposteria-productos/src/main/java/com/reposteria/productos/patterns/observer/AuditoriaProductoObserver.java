package com.reposteria.productos.patterns.observer;

import com.reposteria.productos.model.AuditoriaProducto;
import com.reposteria.productos.repository.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Traza de responsabilidad: registra QUIÉN registró, modificó o desactivó un producto. */
@Component
public class AuditoriaProductoObserver implements ProductoObserver {

    private final AuditoriaRepository repository;

    public AuditoriaProductoObserver(AuditoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void actualizar(ProductoEvento evento) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null && auth.isAuthenticated()) ? auth.getName() : "sistema";
        repository.save(new AuditoriaProducto(usuario, evento.tipo().name(),
                evento.producto().getIdProducto(), evento.producto().getNombre()));
    }
}
