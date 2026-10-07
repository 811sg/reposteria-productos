package com.reposteria.productos.patterns.facade;

import com.reposteria.productos.dto.ProductoPresentacion;
import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.dto.ProductoResponse;
import com.reposteria.productos.service.ProductoService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * PATRÓN FACADE (estructural).
 * Punto de entrada único para el controlador: coordina el servicio (que a su vez usa el validador,
 * el repositorio, las fábricas, estrategias, observadores, etc.) y convierte las entidades en respuestas.
 */
@Component
public class ProductoFacade {

    private final ProductoService servicio; // inyecta el Proxy (@Primary)

    public ProductoFacade(ProductoService servicio) {
        this.servicio = servicio;
    }

    public ProductoResponse registrarProducto(ProductoRequest datos) {
        return ProductoResponse.desde(servicio.registrar(datos));
    }

    public ProductoResponse consultarProducto(Integer id) {
        return ProductoResponse.desde(servicio.consultar(id));
    }

    public ProductoResponse editarProducto(Integer id, ProductoRequest datos) {
        return ProductoResponse.desde(servicio.actualizar(id, datos));
    }

    public ProductoResponse desactivarProducto(Integer id) {
        return ProductoResponse.desde(servicio.desactivar(id));
    }

    public ProductoResponse activarProducto(Integer id) {
        return ProductoResponse.desde(servicio.activar(id));
    }

    public List<ProductoResponse> listarProductos() {
        return servicio.listar().stream().map(ProductoResponse::desde).toList();
    }

    public List<ProductoResponse> listarDisponibles() {
        return servicio.listarDisponibles().stream().map(ProductoResponse::desde).toList();
    }

    public List<ProductoResponse> buscarProductos(String nombre, String categoria) {
        return servicio.buscar(nombre, categoria).stream().map(ProductoResponse::desde).toList();
    }

    public List<String> categorias() {
        return servicio.categorias();
    }

    public ProductoResponse subirImagen(Integer id, MultipartFile archivo) {
        return ProductoResponse.desde(servicio.actualizarImagen(id, archivo));
    }

    public ProductoPresentacion presentacion(Integer id, Integer descuento, String promocion) {
        return servicio.presentacion(id, descuento, promocion);
    }
}
