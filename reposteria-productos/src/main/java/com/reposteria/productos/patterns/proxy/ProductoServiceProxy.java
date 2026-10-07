package com.reposteria.productos.patterns.proxy;

import com.reposteria.productos.dto.ProductoPresentacion;
import com.reposteria.productos.dto.ProductoRequest;
import com.reposteria.productos.model.Producto;
import com.reposteria.productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * PATRÓN PROXY (estructural).
 * Intermediario del ProductoService real: antes de delegar, verifica que el usuario esté autenticado
 * y, en operaciones que modifican información, que tenga el rol ADMIN.
 */
@Service("productoServiceProxy")
@Primary
public class ProductoServiceProxy implements ProductoService {

    private final ProductoService real;

    public ProductoServiceProxy(@Qualifier("productoServiceImpl") ProductoService real) {
        this.real = real;
    }

    // ---- operaciones protegidas (solo ADMIN) ----
    @Override
    public Producto registrar(ProductoRequest datos) {
        requerirAdmin();
        return real.registrar(datos);
    }

    @Override
    public Producto actualizar(Integer id, ProductoRequest datos) {
        requerirAdmin();
        return real.actualizar(id, datos);
    }

    @Override
    public Producto desactivar(Integer id) {
        requerirAdmin();
        return real.desactivar(id);
    }

    @Override
    public Producto activar(Integer id) {
        requerirAdmin();
        return real.activar(id);
    }

    @Override
    public Producto actualizarImagen(Integer id, MultipartFile archivo) {
        requerirAdmin();
        return real.actualizarImagen(id, archivo);
    }

    // ---- consultas (cualquier usuario autenticado) ----
    @Override
    public Producto consultar(Integer id) {
        requerirAutenticado();
        return real.consultar(id);
    }

    @Override
    public List<Producto> buscar(String nombre, String categoria) {
        requerirAutenticado();
        return real.buscar(nombre, categoria);
    }

    @Override
    public List<Producto> listar() {
        requerirAutenticado();
        return real.listar();
    }

    @Override
    public List<Producto> listarDisponibles() {
        requerirAutenticado();
        return real.listarDisponibles();
    }

    @Override
    public List<String> categorias() {
        requerirAutenticado();
        return real.categorias();
    }

    @Override
    public ProductoPresentacion presentacion(Integer id, Integer descuento, String promocion) {
        requerirAutenticado();
        return real.presentacion(id, descuento, promocion);
    }

    // ---- control de acceso ----
    private Authentication autenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        return auth;
    }

    private void requerirAutenticado() {
        autenticacion();
    }

    private void requerirAdmin() {
        boolean esAdmin = autenticacion().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!esAdmin) {
            throw new AccessDeniedException("Se requiere el rol ADMIN");
        }
    }
}
