package com.reposteria.productos.validation;

import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.exception.ValidacionException;
import com.reposteria.productos.repository.ProductoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validación de la información del producto (RF-07, RNF-04, RNF-06).
 * Usa Jakarta Validation para campos obligatorios, formato y precio, y agrega la regla de nombre único.
 */
@Component
public class ProductoValidator {

    private final Validator validator;
    private final ProductoRepository repository;

    public ProductoValidator(Validator validator, ProductoRepository repository) {
        this.validator = validator;
        this.repository = repository;
    }

    /** @param idExcluir id del producto que se está editando (null si es un registro nuevo). */
    public void validar(ProductoRequest datos, Integer idExcluir) {
        if (datos == null) {
            throw new ValidacionException("Debe enviar los datos del producto");
        }
        List<String> errores = new ArrayList<>();
        for (ConstraintViolation<ProductoRequest> v : validator.validate(datos)) {
            errores.add(v.getMessage());
        }
        if (errores.isEmpty()) {
            Integer excluir = idExcluir == null ? -1 : idExcluir;
            if (repository.existsByNombreIgnoreCaseAndIdProductoNot(datos.nombre().trim(), excluir)) {
                errores.add("Ya existe un producto con ese nombre");
            }
        }
        if (!errores.isEmpty()) {
            errores.sort(String::compareTo);
            throw new ValidacionException(errores);
        }
    }
}
