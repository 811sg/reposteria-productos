package com.reposteria.productos.patterns.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Genera notificaciones (en esta versión, un mensaje en consola; puede ampliarse a correo, etc.). */
@Component
public class NotificacionProductoObserver implements ProductoObserver {

    private static final Logger log = LoggerFactory.getLogger(NotificacionProductoObserver.class);

    @Override
    public void actualizar(ProductoEvento evento) {
        log.info("[NOTIFICACIÓN] Producto '{}' (id {}) -> {}",
                evento.producto().getNombre(), evento.producto().getIdProducto(), evento.tipo());
    }
}
