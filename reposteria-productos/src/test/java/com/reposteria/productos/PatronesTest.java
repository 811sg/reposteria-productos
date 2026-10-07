package com.reposteria.productos;

import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.builder.ProductoBuilder;
import com.reposteria.productos.patterns.decorator.DescuentoDecorator;
import com.reposteria.productos.patterns.decorator.ProductoBase;
import com.reposteria.productos.patterns.decorator.ProductoComponent;
import com.reposteria.productos.patterns.factory.ProductoTortaFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias simples (no necesitan base de datos). */
class PatronesTest {

    @Test
    void builderConstruyeProducto() {
        Producto p = new ProductoBuilder().nombre("  Tiramisú ").categoria("Postre")
                .precio(new BigDecimal("14500")).disponibilidad(true).build();
        assertEquals("Tiramisú", p.getNombre());
        assertTrue(p.isDisponibilidad());
    }

    @Test
    void decoratorAplicaDescuento() {
        Producto p = new ProductoBuilder().nombre("Torta").descripcion("d").categoria("Torta")
                .precio(new BigDecimal("50000")).disponibilidad(true).build();
        ProductoComponent c = new DescuentoDecorator(new ProductoBase(p), 10);
        assertEquals(new BigDecimal("45000.00"), c.getPrecio());
    }

    @Test
    void factoryNormalizaCategoria() {
        ProductoTortaFactory fabrica = new ProductoTortaFactory();
        assertTrue(fabrica.soporta("tORTA"));
        Producto p = fabrica.crear(new ProductoRequest("Torta", "desc", "torta", new BigDecimal("1000"), true, null));
        assertEquals("Torta", p.getCategoria());
    }
}
