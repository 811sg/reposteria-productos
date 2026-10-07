package com.reposteria.productos.dto;

import java.util.List;

/** Respuesta de error uniforme: indica qué información debe corregirse (RNF-06). */
public record ErrorResponse(String mensaje, List<String> errores) {
    public static ErrorResponse de(String mensaje) {
        return new ErrorResponse(mensaje, List.of());
    }
}
