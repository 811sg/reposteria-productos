package com.reposteria.productos.patterns.adapter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Adapta ExternalImageService a la interfaz ImageStorage. Activo con imagenes.storage=external. */
@Component
@ConditionalOnProperty(name = "imagenes.storage", havingValue = "external")
public class ImageStorageAdapter implements ImageStorage {

    private final ExternalImageService servicioExterno;

    public ImageStorageAdapter(ExternalImageService servicioExterno) {
        this.servicioExterno = servicioExterno;
    }

    @Override
    public String guardar(byte[] contenido, String extension) {
        return servicioExterno.subirArchivo("ext-" + UUID.randomUUID() + extension, contenido);
    }
}
