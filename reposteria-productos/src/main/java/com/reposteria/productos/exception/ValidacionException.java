package com.reposteria.productos.exception;

import java.util.List;

public class ValidacionException extends RuntimeException {
    private final List<String> errores;

    public ValidacionException(List<String> errores) {
        super("Hay información que debe corregirse");
        this.errores = errores;
    }

    public ValidacionException(String error) {
        this(List.of(error));
    }

    public List<String> getErrores() {
        return errores;
    }
}
