package com.reposteria.productos.service;

import com.reposteria.productos.dto.ProductoPresentacion;
import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.model.Producto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Operaciones de gestión de productos. Implementada por ProductoServiceImpl y protegida por ProductoServiceProxy. */
public interface ProductoService {
    Producto registrar(ProductoRequest datos);                 // RF-01
    Producto consultar(Integer id);                            // RF-02
    Producto actualizar(Integer id, ProductoRequest datos);    // RF-03
    Producto desactivar(Integer id);                           // RF-04
    Producto activar(Integer id);
    List<Producto> buscar(String nombre, String categoria);    // RF-05
    List<Producto> listar();                                   // RF-06
    List<Producto> listarDisponibles();                        // RF-08 / RF-11
    List<String> categorias();                                 // RF-09
    Producto actualizarImagen(Integer id, MultipartFile archivo); // RF-10
    ProductoPresentacion presentacion(Integer id, Integer descuento, String promocion); // Decorator
}
