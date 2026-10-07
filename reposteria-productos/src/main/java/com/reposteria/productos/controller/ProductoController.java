package com.reposteria.productos.controller;

import com.reposteria.productos.dto.ProductoPresentacion;
import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.dto.ProductoResponse;
import com.reposteria.productos.patterns.facade.ProductoFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Controlador REST: solo recibe las solicitudes HTTP y las delega en la fachada. */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos")
public class ProductoController {

    private final ProductoFacade fachada;

    public ProductoController(ProductoFacade fachada) {
        this.fachada = fachada;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "RF-01 Registrar producto (ADMIN)")
    public ProductoResponse registrar(@RequestBody ProductoRequest datos) {
        return fachada.registrarProducto(datos);
    }

    @GetMapping
    @Operation(summary = "RF-06 Listar productos")
    public List<ProductoResponse> listar() {
        return fachada.listarProductos();
    }

    @GetMapping("/disponibles")
    @Operation(summary = "RF-08 / RF-11 Listar solo productos activos y disponibles")
    public List<ProductoResponse> disponibles() {
        return fachada.listarDisponibles();
    }

    @GetMapping("/buscar")
    @Operation(summary = "RF-05 Buscar por nombre y/o categoría")
    public List<ProductoResponse> buscar(@RequestParam(required = false) String nombre,
                                         @RequestParam(required = false) String categoria) {
        return fachada.buscarProductos(nombre, categoria);
    }

    @GetMapping("/categorias")
    @Operation(summary = "RF-09 Categorías de repostería registradas")
    public List<String> categorias() {
        return fachada.categorias();
    }

    @GetMapping("/{id}")
    @Operation(summary = "RF-02 Consultar un producto")
    public ProductoResponse consultar(@PathVariable Integer id) {
        return fachada.consultarProducto(id);
    }

    @GetMapping("/{id}/presentacion")
    @Operation(summary = "Producto con descuento/promoción aplicados (patrón Decorator)")
    public ProductoPresentacion presentacion(@PathVariable Integer id,
                                             @RequestParam(required = false) Integer descuento,
                                             @RequestParam(required = false) String promocion) {
        return fachada.presentacion(id, descuento, promocion);
    }

    @PutMapping("/{id}")
    @Operation(summary = "RF-03 Editar producto (ADMIN)")
    public ProductoResponse editar(@PathVariable Integer id, @RequestBody ProductoRequest datos) {
        return fachada.editarProducto(id, datos);
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "RF-04 Desactivar producto conservando su información (ADMIN)")
    public ProductoResponse desactivar(@PathVariable Integer id) {
        return fachada.desactivarProducto(id);
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Volver a activar un producto desactivado (ADMIN)")
    public ProductoResponse activar(@PathVariable Integer id) {
        return fachada.activarProducto(id);
    }

    @PostMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "RF-10 Asociar o reemplazar la imagen del producto (ADMIN)")
    public ProductoResponse imagen(@PathVariable Integer id, @RequestParam("archivo") MultipartFile archivo) {
        return fachada.subirImagen(id, archivo);
    }
}
