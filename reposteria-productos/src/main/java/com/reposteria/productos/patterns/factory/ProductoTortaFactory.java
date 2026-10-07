package com.reposteria.productos.patterns.factory;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ProductoTortaFactory extends AbstractProductoFactory {
    public ProductoTortaFactory() {
        super("Torta");
    }
}
