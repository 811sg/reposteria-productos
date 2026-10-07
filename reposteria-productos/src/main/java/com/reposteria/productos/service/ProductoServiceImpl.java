package com.reposteria.productos.service;

import com.reposteria.productos.dto.ProductoPresentacion;
import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.exception.RecursoNoEncontradoException;
import com.reposteria.productos.exception.ValidacionException;
import com.reposteria.productos.model.Producto;
import com.reposteria.productos.patterns.adapter.ImageStorage;
import com.reposteria.productos.patterns.decorator.DescuentoDecorator;
import com.reposteria.productos.patterns.decorator.ProductoBase;
import com.reposteria.productos.patterns.decorator.ProductoComponent;
import com.reposteria.productos.patterns.decorator.PromocionDecorator;
import com.reposteria.productos.patterns.observer.ProductoEvento;
import com.reposteria.productos.patterns.observer.ProductoSubject;
import com.reposteria.productos.patterns.observer.TipoEvento;
import com.reposteria.productos.patterns.strategy.BusquedaProductoStrategy;
import com.reposteria.productos.patterns.template.*;
import com.reposteria.productos.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** Implementación real del servicio (la que ejecuta las operaciones una vez el Proxy autoriza el acceso). */
@Service("productoServiceImpl")
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private static final Sort POR_NOMBRE = Sort.by("nombre");
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp", "image/gif", ".gif");

    private final ProductoRepository repository;
    private final RegistrarProductoOperation registrarOp;
    private final ActualizarProductoOperation actualizarOp;
    private final DesactivarProductoOperation desactivarOp;
    private final ActivarProductoOperation activarOp;
    private final List<BusquedaProductoStrategy> estrategias; // ordenadas por @Order
    private final ImageStorage imageStorage;
    private final ProductoSubject subject;

    public ProductoServiceImpl(ProductoRepository repository,
                               RegistrarProductoOperation registrarOp,
                               ActualizarProductoOperation actualizarOp,
                               DesactivarProductoOperation desactivarOp,
                               ActivarProductoOperation activarOp,
                               List<BusquedaProductoStrategy> estrategias,
                               ImageStorage imageStorage,
                               ProductoSubject subject) {
        this.repository = repository;
        this.registrarOp = registrarOp;
        this.actualizarOp = actualizarOp;
        this.desactivarOp = desactivarOp;
        this.activarOp = activarOp;
        this.estrategias = estrategias;
        this.imageStorage = imageStorage;
        this.subject = subject;
    }

    @Override
    public Producto registrar(ProductoRequest datos) {
        return registrarOp.ejecutar(new ProductoContexto(null, datos));
    }

    @Override
    @Transactional(readOnly = true)
    public Producto consultar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un producto con id " + id));
    }

    @Override
    public Producto actualizar(Integer id, ProductoRequest datos) {
        return actualizarOp.ejecutar(new ProductoContexto(id, datos));
    }

    @Override
    public Producto desactivar(Integer id) {
        return desactivarOp.ejecutar(new ProductoContexto(id, null));
    }

    @Override
    public Producto activar(Integer id) {
        return activarOp.ejecutar(new ProductoContexto(id, null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscar(String nombre, String categoria) {
        String n = normalizar(nombre);
        String c = normalizar(categoria);
        // Strategy: se elige el algoritmo de búsqueda según los criterios recibidos
        return estrategias.stream()
                .filter(e -> e.aplica(n, c))
                .findFirst()
                .map(e -> e.buscar(n, c))
                .orElseGet(this::listar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return repository.findAll(POR_NOMBRE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarDisponibles() {
        return repository.findByDisponibilidadTrue(POR_NOMBRE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> categorias() {
        return repository.findCategorias();
    }

    @Override
    public Producto actualizarImagen(Integer id, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ValidacionException("Debe seleccionar una imagen");
        }
        String extension = EXTENSIONES.get(archivo.getContentType());
        if (extension == null) {
            throw new ValidacionException("La imagen debe ser JPG, PNG, WEBP o GIF");
        }
        Producto producto = consultar(id);
        try {
            producto.setImagen(imageStorage.guardar(archivo.getBytes(), extension)); // Adapter
        } catch (IOException e) {
            throw new ValidacionException("No se pudo leer el archivo de la imagen");
        }
        Producto guardado = repository.save(producto);
        subject.notificar(new ProductoEvento(TipoEvento.IMAGEN_ACTUALIZADA, guardado));
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoPresentacion presentacion(Integer id, Integer descuento, String promocion) {
        if (descuento != null && (descuento < 0 || descuento > 100)) {
            throw new ValidacionException("El descuento debe estar entre 0 y 100");
        }
        Producto p = consultar(id);
        // Decorator: se agregan comportamientos sin modificar la clase Producto
        ProductoComponent componente = new ProductoBase(p);
        if (descuento != null && descuento > 0) {
            componente = new DescuentoDecorator(componente, descuento);
        }
        String promo = normalizar(promocion);
        if (promo != null) {
            componente = new PromocionDecorator(componente, promo);
        }
        return new ProductoPresentacion(p.getIdProducto(), componente.getNombre(), componente.getDescripcion(),
                p.getPrecio(), componente.getPrecio());
    }

    private String normalizar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
