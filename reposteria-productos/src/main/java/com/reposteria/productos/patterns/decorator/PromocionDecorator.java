package com.reposteria.productos.patterns.decorator;

/** Agrega un texto promocional (por ejemplo "2x1 los viernes") a la descripción. */
public class PromocionDecorator extends ProductoDecorator {

    private final String promocion;

    public PromocionDecorator(ProductoComponent componente, String promocion) {
        super(componente);
        this.promocion = promocion;
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " | Promoción: " + promocion;
    }
}
