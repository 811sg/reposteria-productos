package com.reposteria.productos.patterns.adapter;

/** PATRÓN ADAPTER (estructural): interfaz única que usa el sistema para guardar imágenes. */
public interface ImageStorage {

    /**
     * @param contenido bytes de la imagen
     * @param extension extensión con punto, por ejemplo ".png"
     * @return referencia (ruta/URL) que se guarda en el campo imagen del producto
     */
    String guardar(byte[] contenido, String extension);
}
