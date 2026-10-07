package com.reposteria.productos.patterns.factory;

import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.model.Producto;
import org.springframework.stereotype.Component;

import java.util.List;

/** Selecciona la fábrica correspondiente, de modo que el resto del sistema no sepa cómo se construye cada tipo. */
@Component
public class ProductoFactoryProvider {

    private final List<ProductoFactory> fabricas; // Spring las entrega ordenadas por @Order

    public ProductoFactoryProvider(List<ProductoFactory> fabricas) {
        this.fabricas = fabricas;
    }

    public Producto crear(ProductoRequest datos) {
        return fabricas.stream()
                .filter(f -> f.soporta(datos.categoria()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No existe una fábrica para la categoría"))
                .crear(datos);
    }
}
