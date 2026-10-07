package com.reposteria.productos.patterns.builder;

import com.reposteria.productos.model.Producto;

import java.math.BigDecimal;

/**
 * PATRÓN BUILDER (creacional).
 * Construye el objeto Producto paso a paso, controlando la asignación de cada propiedad.
 */
public class ProductoBuilder {

    private final Producto producto = new Producto();

    public ProductoBuilder nombre(String nombre) {
        producto.setNombre(nombre == null ? null : nombre.trim());
        return this;
    }

    public ProductoBuilder descripcion(String descripcion) {
        producto.setDescripcion(descripcion == null ? null : descripcion.trim());
        return this;
    }

    public ProductoBuilder categoria(String categoria) {
        producto.setCategoria(categoria == null ? null : categoria.trim());
        return this;
    }

    public ProductoBuilder precio(BigDecimal precio) {
        producto.setPrecio(precio);
        return this;
    }

    public ProductoBuilder disponibilidad(Boolean disponibilidad) {
        producto.setDisponibilidad(disponibilidad == null || disponibilidad);
        return this;
    }

    public ProductoBuilder imagen(String imagen) {
        producto.setImagen(imagen == null || imagen.isBlank() ? null : imagen.trim());
        return this;
    }

    /** id_producto y fecha_registro NO se asignan aquí: los genera el sistema. */
    public Producto build() {
        return producto;
    }
}
