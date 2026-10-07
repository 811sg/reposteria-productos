package com.reposteria.productos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Publica la carpeta de imágenes en la ruta /uploads/** para poder mostrarlas en la interfaz. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String directorio;

    public WebConfig(@Value("${imagenes.directorio:uploads}") String directorio) {
        this.directorio = directorio;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path ruta = Paths.get(directorio).toAbsolutePath().normalize();
        try {
            Files.createDirectories(ruta);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de imágenes: " + ruta, e);
        }
        String ubicacion = ruta.toUri().toString();
        if (!ubicacion.endsWith("/")) {
            ubicacion += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(ubicacion);
    }
}
