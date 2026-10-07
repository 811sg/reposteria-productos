package com.reposteria.productos.patterns.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/** Almacenamiento propio del sistema (carpeta local "uploads"). Activo con imagenes.storage=local. */
@Component
@ConditionalOnProperty(name = "imagenes.storage", havingValue = "local", matchIfMissing = true)
public class LocalImageStorage implements ImageStorage {

    private final Path directorio;

    public LocalImageStorage(@Value("${imagenes.directorio:uploads}") String directorio) {
        this.directorio = Paths.get(directorio).toAbsolutePath().normalize();
    }

    @Override
    public String guardar(byte[] contenido, String extension) {
        try {
            Files.createDirectories(directorio);
            String nombre = UUID.randomUUID() + extension;
            Files.write(directorio.resolve(nombre), contenido);
            return "/uploads/" + nombre;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen", e);
        }
    }
}
