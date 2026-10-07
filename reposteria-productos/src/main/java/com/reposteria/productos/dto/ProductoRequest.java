package com.reposteria.productos.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Datos de entrada para registrar o editar un producto. Las reglas son las del RF-07 / RNF-06. */
public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion,

        @NotBlank(message = "La categoría es obligatoria")
        @Size(max = 100, message = "La categoría no puede superar 100 caracteres")
        String categoria,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
        @Digits(integer = 8, fraction = 2, message = "El precio admite máximo 8 enteros y 2 decimales")
        BigDecimal precio,

        @NotNull(message = "La disponibilidad es obligatoria (true o false)")
        Boolean disponibilidad,

        @Size(max = 255, message = "La referencia de la imagen no puede superar 255 caracteres")
        String imagen) {
}
