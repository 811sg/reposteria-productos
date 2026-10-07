package com.reposteria.productos.patterns.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Servicio EXTERNO de almacenamiento (simulado): su interfaz es distinta a ImageStorage,
 * por eso necesita el adaptador. En un caso real aquí iría un cliente de S3, Cloudinary, etc.
 */
@Component
public class ExternalImageService {

    private final Path directorio;

    public ExternalImageService(@Value("${imagenes.directorio:uploads}") String directorio) {
        this.directorio = Paths.get(directorio).toAbsolutePath().normalize();
    }

    /** Interfaz propia del servicio externo: sube un archivo y devuelve su URL pública. */
    public String subirArchivo(String clave, byte[] datos) {
        try {
            Files.createDirectories(directorio);
            Files.write(directorio.resolve(clave), datos);
            return "/uploads/" + clave;
        } catch (IOException e) {
            throw new UncheckedIOException("Falla en el servicio externo de imágenes", e);
        }
    }
}
