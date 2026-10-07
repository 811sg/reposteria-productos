/* Interfaz del módulo de productos.
   Mantiene la API REST y la lógica funcional del proyecto y concentra la mejora en la experiencia visual. */
'use strict';

let token = sessionStorage.getItem('token');
let usuario = sessionStorage.getItem('usuario');
let rol = sessionStorage.getItem('rol');

const $ = (id) => document.getElementById(id);
const esAdmin = () => rol === 'ADMIN';
const moneda = (n) => Number(n).toLocaleString('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0
});
const esc = (t) => String(t ?? '').replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
}[c]));

const modalProducto = new bootstrap.Modal($('modalProducto'));
const modalDetalle = new bootstrap.Modal($('modalDetalle'));
const modalHistorial = new bootstrap.Modal($('modalHistorial'));
const toast = new bootstrap.Toast($('toast'), { delay: 3000 });

function aviso(texto) {
    $('toastTexto').textContent = texto;
    toast.show();
}

/* ---------- API ---------- */
async function api(ruta, opciones = {}) {
    const headers = { ...(opciones.headers || {}) };
    if (token) headers.Authorization = 'Bearer ' + token;
    if (opciones.body && !(opciones.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }

    const res = await fetch(ruta, { ...opciones, headers });
    const datos = res.status === 204 ? null : await res.json().catch(() => null);

    if (res.status === 401 && !ruta.startsWith('/api/auth')) {
        cerrarSesion();
        throw { mensaje: 'Su sesión expiró. Inicie sesión de nuevo.', errores: [] };
    }

    if (!res.ok) {
        throw datos || { mensaje: 'Error ' + res.status, errores: [] };
    }

    return datos;
}

/* ---------- Login ---------- */
$('formLogin').addEventListener('submit', async (e) => {
    e.preventDefault();
    $('loginError').classList.add('d-none');

    if (!e.target.checkValidity()) {
        e.target.classList.add('was-validated');
        $('loginError').textContent = 'Ingresa tu usuario y contraseña para continuar.';
        $('loginError').classList.remove('d-none');
        return;
    }

    const boton = e.target.querySelector('button[type="submit"]');
    const textoOriginal = boton.innerHTML;
    boton.disabled = true;
    boton.innerHTML = '<span>Validando acceso...</span><i class="bi bi-arrow-repeat spin"></i>';

    try {
        const r = await api('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                username: $('loginUsuario').value.trim(),
                password: $('loginClave').value
            })
        });

        token = r.token;
        usuario = r.username;
        rol = r.rol;

        sessionStorage.setItem('token', token);
        sessionStorage.setItem('usuario', usuario);
        sessionStorage.setItem('rol', rol);

        $('loginClave').value = '';
        mostrarApp();
    } catch (err) {
        $('loginError').textContent = err.mensaje || 'No fue posible iniciar sesión.';
        $('loginError').classList.remove('d-none');
    } finally {
        boton.disabled = false;
        boton.innerHTML = textoOriginal;
    }
});

$('btnVerClave').addEventListener('click', () => {
    const input = $('loginClave');
    const icono = $('btnVerClave i');
    const visible = input.type === 'text';

    input.type = visible ? 'password' : 'text';
    icono.className = visible ? 'bi bi-eye' : 'bi bi-eye-slash';
    $('btnVerClave').setAttribute('aria-label', visible ? 'Mostrar contraseña' : 'Ocultar contraseña');
    $('btnVerClave').setAttribute('title', visible ? 'Mostrar contraseña' : 'Ocultar contraseña');
});

function cerrarSesion() {
    sessionStorage.clear();
    token = usuario = rol = null;
    $('vistaApp').classList.add('d-none');
    $('vistaLogin').classList.remove('d-none');
    $('formLogin').reset();
    $('loginError').classList.add('d-none');
}

$('btnSalir').addEventListener('click', cerrarSesion);

function mostrarApp() {
    $('vistaLogin').classList.add('d-none');
    $('vistaApp').classList.remove('d-none');

    $('usuarioActual').textContent = usuario;
    $('usuarioSidebar').textContent = usuario;
    $('rolSidebar').textContent = rol ? rol.toLowerCase() : '';

    document.querySelectorAll('.solo-admin').forEach((el) => {
        el.classList.toggle('d-none', !esAdmin());
    });

    cargarCategorias();
    cargarProductos();
}

/* ---------- Navegación móvil ---------- */
$('btnMenuMobile').addEventListener('click', () => {
    $('sidebar').classList.toggle('open');
});

document.querySelectorAll('.side-link').forEach((link) => {
    link.addEventListener('click', () => {
        if (window.innerWidth <= 900) $('sidebar').classList.remove('open');
    });
});

/* ---------- Categorías ---------- */
async function cargarCategorias() {
    try {
        const categorias = await api('/api/productos/categorias');
        const seleccionada = $('buscarCategoria').value;

        $('buscarCategoria').innerHTML =
            '<option value="">Todas las categorías</option>' +
            categorias.map((c) => `<option value="${esc(c)}">${esc(c)}</option>`).join('');

        $('buscarCategoria').value = seleccionada;

        const base = ['Torta', 'Postre', 'Galleta'];
        const completas = [...base, ...categorias.filter((c) => !base.includes(c))];

        $('listaCategorias').innerHTML = completas
            .map((c) => `<option value="${esc(c)}"></option>`)
            .join('');
    } catch (e) {
        /* Las categorías son un apoyo visual. El catálogo continúa funcionando. */
    }
}

/* ---------- Listar y buscar ---------- */
async function cargarProductos() {
    const nombre = $('buscarNombre').value.trim();
    const categoria = $('buscarCategoria').value;
    const solo = $('soloDisponibles').checked;

    $('mensajeLista').classList.add('d-none');
    mostrarSkeleton();

    try {
        let lista;

        if (nombre || categoria) {
            const q = new URLSearchParams();
            if (nombre) q.set('nombre', nombre);
            if (categoria) q.set('categoria', categoria);

            lista = await api('/api/productos/buscar?' + q);
            if (solo) lista = lista.filter((p) => p.disponibilidad);
        } else {
            lista = await api(solo ? '/api/productos/disponibles' : '/api/productos');
        }

        pintarCatalogo(lista);
    } catch (e) {
        $('mensajeLista').textContent = e.mensaje || 'No se pudo cargar el catálogo.';
        $('mensajeLista').classList.remove('d-none');
        pintarCatalogo([]);
    }
}

function mostrarSkeleton() {
    $('cuerpoCatalogo').innerHTML = Array.from({ length: 6 }, () => `
        <div class="skeleton-card">
            <div class="skeleton-shimmer"></div>
        </div>
    `).join('');
}

function actualizarResumen(lista) {
    const total = lista.length;
    const disponibles = lista.filter((p) => p.disponibilidad).length;
    const desactivados = total - disponibles;
    const categorias = new Set(lista.map((p) => p.categoria).filter(Boolean)).size;

    $('statTotal').textContent = total;
    $('statDisponibles').textContent = disponibles;
    $('statCategorias').textContent = categorias;
    $('statDesactivados').textContent = desactivados;

    $('conteo').textContent = `${total} ${total === 1 ? 'producto encontrado' : 'productos encontrados'}`;
}

function pintarCatalogo(lista) {
    actualizarResumen(lista);

    if (!lista.length) {
        $('cuerpoCatalogo').innerHTML = `
            <div class="catalog-empty">
                <div class="catalog-empty-icon"><i class="bi bi-search"></i></div>
                <h3>No encontramos productos</h3>
                <p>Prueba con otro nombre, categoría o limpia los filtros para volver a mostrar el catálogo.</p>
            </div>
        `;
        return;
    }

    $('cuerpoCatalogo').innerHTML = lista.map((p) => {
        const imagen = p.imagen
            ? `<img src="${esc(p.imagen)}" alt="Imagen de ${esc(p.nombre)}" loading="lazy">`
            : `<div class="product-image-placeholder" aria-hidden="true"><i class="bi bi-cake2"></i></div>`;

        return `
            <article class="product-card">
                <div class="product-image">
                    ${imagen}
                    <span class="product-status ${p.disponibilidad ? 'available' : ''}">
                        ${p.disponibilidad ? 'Disponible' : 'No disponible'}
                    </span>
                </div>

                <div class="product-body">
                    <div class="product-meta">
                        <span class="category-pill">${esc(p.categoria)}</span>
                        <span class="product-id">#${esc(p.idProducto)}</span>
                    </div>

                    <h3>${esc(p.nombre)}</h3>
                    <p class="product-description">${esc(p.descripcion)}</p>

                    <div class="product-footer">
                        <span class="product-price">${moneda(p.precio)}</span>
                        <div class="product-actions">
                            <button class="icon-btn" data-accion="ver" data-id="${p.idProducto}"
                                    type="button" aria-label="Ver producto" title="Ver producto">
                                <i class="bi bi-eye"></i>
                            </button>
                            ${esAdmin() ? `
                                <button class="icon-btn" data-accion="editar" data-id="${p.idProducto}"
                                        type="button" aria-label="Editar producto" title="Editar producto">
                                    <i class="bi bi-pencil"></i>
                                </button>
                                ${p.disponibilidad
                                    ? `<button class="icon-btn danger" data-accion="desactivar" data-id="${p.idProducto}"
                                             type="button" aria-label="Desactivar producto" title="Desactivar producto">
                                           <i class="bi bi-slash-circle"></i>
                                       </button>`
                                    : `<button class="icon-btn success" data-accion="activar" data-id="${p.idProducto}"
                                             type="button" aria-label="Activar producto" title="Activar producto">
                                           <i class="bi bi-check2-circle"></i>
                                       </button>`
                                }
                            ` : ''}
                        </div>
                    </div>
                </div>
            </article>
        `;
    }).join('');
}

$('formBuscar').addEventListener('submit', (e) => {
    e.preventDefault();
    cargarProductos();
});

$('soloDisponibles').addEventListener('change', cargarProductos);

$('btnLimpiar').addEventListener('click', () => {
    $('buscarNombre').value = '';
    $('buscarCategoria').value = '';
    $('soloDisponibles').checked = false;
    cargarProductos();
});

$('cuerpoCatalogo').addEventListener('click', async (e) => {
    const boton = e.target.closest('button[data-accion]');
    if (!boton) return;

    const id = boton.dataset.id;

    try {
        switch (boton.dataset.accion) {
            case 'ver':
                return await verDetalle(id);

            case 'editar':
                return await abrirEditar(id);

            case 'desactivar':
                if (!confirm('¿Desactivar este producto? Se conservará su información, pero no podrá seleccionarse en nuevos pedidos.')) {
                    return;
                }
                await api(`/api/productos/${id}/desactivar`, { method: 'PATCH' });
                aviso('Producto desactivado correctamente.');
                break;

            case 'activar':
                await api(`/api/productos/${id}/activar`, { method: 'PATCH' });
                aviso('Producto activado correctamente.');
                break;
        }

        cargarProductos();
    } catch (err) {
        aviso(err.mensaje || 'No se pudo completar la acción.');
    }
});

/* ---------- Detalle ---------- */
async function verDetalle(id) {
    const p = await api('/api/productos/' + id);

    $('tituloDetalle').textContent = p.nombre;

    $('cuerpoDetalle').innerHTML = `
        <div class="detail-layout">
            <div class="detail-image ${p.imagen ? '' : 'placeholder'}">
                ${p.imagen
                    ? `<img src="${esc(p.imagen)}" alt="Imagen de ${esc(p.nombre)}">`
                    : '<i class="bi bi-cake2"></i>'
                }
            </div>

            <div class="detail-content">
                <span class="category-pill">${esc(p.categoria)}</span>
                <h3>${esc(p.nombre)}</h3>
                <p class="detail-description">${esc(p.descripcion)}</p>
                <div class="detail-price">${moneda(p.precio)}</div>

                <dl class="detail-facts">
                    <div class="detail-fact">
                        <dt>Estado</dt>
                        <dd>${p.disponibilidad ? 'Disponible' : 'Desactivado'}</dd>
                    </div>
                    <div class="detail-fact">
                        <dt>Identificador</dt>
                        <dd>#${esc(p.idProducto)}</dd>
                    </div>
                    <div class="detail-fact">
                        <dt>Registrado</dt>
                        <dd>${new Date(p.fechaRegistro).toLocaleDateString('es-CO')}</dd>
                    </div>
                    <div class="detail-fact">
                        <dt>Hora de registro</dt>
                        <dd>${new Date(p.fechaRegistro).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' })}</dd>
                    </div>
                </dl>

                <div class="decorator-box">
                    <div class="decorator-title">
                        <i class="bi bi-stars"></i>
                        Simular descuento o promoción
                    </div>

                    <div class="row g-2">
                        <div class="col-4">
                            <input id="simDescuento" type="number" min="0" max="100"
                                   class="form-control" placeholder="% descuento"
                                   aria-label="Porcentaje de descuento">
                        </div>
                        <div class="col-8">
                            <input id="simPromo" class="form-control"
                                   placeholder="Ej. 2x1 los viernes"
                                   aria-label="Texto de promoción">
                        </div>
                    </div>

                    <button id="btnSimular" class="btn btn-ghost btn-sm mt-2" type="button">
                        <i class="bi bi-calculator"></i> Ver precio final
                    </button>

                    <div id="resultadoSim" class="decorator-result d-none"></div>
                </div>
            </div>
        </div>
    `;

    $('btnSimular').addEventListener('click', async () => {
        const q = new URLSearchParams();

        if ($('simDescuento').value) q.set('descuento', $('simDescuento').value);
        if ($('simPromo').value.trim()) q.set('promocion', $('simPromo').value.trim());

        try {
            const r = await api(`/api/productos/${id}/presentacion?` + q);
            $('resultadoSim').innerHTML =
                `<strong>Precio final: ${moneda(r.precioFinal)}</strong>
                 <span class="d-block mt-1">Precio original: ${moneda(r.precioOriginal)}</span>
                 ${r.descripcion ? `<span class="d-block mt-1">${esc(r.descripcion)}</span>` : ''}`;
            $('resultadoSim').classList.remove('d-none');
        } catch (err) {
            $('resultadoSim').textContent =
                (err.errores && err.errores[0]) || err.mensaje || 'No se pudo calcular la presentación.';
            $('resultadoSim').classList.remove('d-none');
        }
    });

    modalDetalle.show();
}

/* ---------- Crear y editar ---------- */
function placeholderImagen() {
    return `
        <div class="image-placeholder">
            <i class="bi bi-image"></i>
            <strong>Agrega una imagen</strong>
            <span>Se mostrará aquí antes de guardar</span>
        </div>
    `;
}

function mostrarPreviewImagen(src) {
    $('imagenPreview').innerHTML = src
        ? `<img src="${esc(src)}" alt="Vista previa del producto">`
        : placeholderImagen();
}

function limpiarForm() {
    $('formProducto').reset();
    $('pId').value = '';
    $('erroresForm').classList.add('d-none');
    $('formProducto').classList.remove('was-validated');
    mostrarPreviewImagen('');
}

$('pImagen').addEventListener('change', () => {
    const archivo = $('pImagen').files[0];

    if (!archivo) {
        mostrarPreviewImagen('');
        return;
    }

    if (!archivo.type.startsWith('image/')) {
        aviso('Selecciona un archivo de imagen válido.');
        $('pImagen').value = '';
        mostrarPreviewImagen('');
        return;
    }

    const url = URL.createObjectURL(archivo);
    mostrarPreviewImagen(url);
});

$('btnNuevo').addEventListener('click', () => {
    limpiarForm();
    $('tituloModalProducto').textContent = 'Nuevo producto';
    modalProducto.show();
});

async function abrirEditar(id) {
    const p = await api('/api/productos/' + id);

    limpiarForm();
    $('tituloModalProducto').textContent = 'Editar producto';
    $('pId').value = p.idProducto;
    $('pNombre').value = p.nombre;
    $('pDescripcion').value = p.descripcion;
    $('pCategoria').value = p.categoria;
    $('pPrecio').value = p.precio;
    $('pDisponibilidad').value = String(p.disponibilidad);

    if (p.imagen) mostrarPreviewImagen(p.imagen);

    modalProducto.show();
}

function mostrarErrores(err) {
    const lista = (err.errores && err.errores.length)
        ? err.errores
        : [err.mensaje || 'No se pudo guardar el producto.'];

    $('erroresForm').innerHTML =
        '<strong>Revisa la información:</strong><ul class="mb-0 mt-1">' +
        lista.map((m) => `<li>${esc(m)}</li>`).join('') +
        '</ul>';

    $('erroresForm').classList.remove('d-none');
}

$('formProducto').addEventListener('submit', async (e) => {
    e.preventDefault();
    $('erroresForm').classList.add('d-none');

    if (!e.target.checkValidity()) {
        e.target.classList.add('was-validated');
        mostrarErrores({
            errores: ['Completa los campos obligatorios y verifica que el precio sea mayor que cero.']
        });
        return;
    }

    const id = $('pId').value;
    const datos = {
        nombre: $('pNombre').value.trim(),
        descripcion: $('pDescripcion').value.trim(),
        categoria: $('pCategoria').value.trim(),
        precio: $('pPrecio').value === '' ? null : Number($('pPrecio').value),
        disponibilidad: $('pDisponibilidad').value === 'true'
    };

    $('btnGuardar').disabled = true;
    $('btnGuardar').innerHTML = '<i class="bi bi-arrow-repeat spin"></i><span>Guardando...</span>';

    try {
        const guardado = await api(id ? '/api/productos/' + id : '/api/productos', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(datos)
        });

        const archivo = $('pImagen').files[0];

        if (archivo) {
            const fd = new FormData();
            fd.append('archivo', archivo);
            await api(`/api/productos/${guardado.idProducto}/imagen`, {
                method: 'POST',
                body: fd
            });
        }

        modalProducto.hide();
        aviso(id ? 'Producto actualizado correctamente.' : 'Producto registrado correctamente.');
        cargarCategorias();
        cargarProductos();
    } catch (err) {
        mostrarErrores(err);
    } finally {
        $('btnGuardar').disabled = false;
        $('btnGuardar').innerHTML = '<i class="bi bi-check2"></i><span>Guardar producto</span>';
    }
});

/* ---------- Historial ---------- */
$('btnHistorial').addEventListener('click', async () => {
    try {
        const filas = await api('/api/auditoria');

        $('cuerpoHistorial').innerHTML = filas.length
            ? filas.map((f) => `
                <tr>
                    <td>${new Date(f.fecha).toLocaleString('es-CO')}</td>
                    <td>${esc(f.usuario)}</td>
                    <td><span class="category-pill">${esc(f.accion)}</span></td>
                    <td>${esc(f.nombreProducto)} <span class="text-muted">#${esc(f.idProducto)}</span></td>
                </tr>
            `).join('')
            : '<tr><td colspan="4" class="text-muted py-4">Aún no hay cambios registrados.</td></tr>';

        modalHistorial.show();
    } catch (err) {
        aviso(err.mensaje || 'No se pudo cargar el historial.');
    }
});

/* ---------- Arranque ---------- */
if (token && usuario && rol) {
    mostrarApp();
}

/* Animación pequeña para iconos de carga */
const style = document.createElement('style');
style.textContent = '.spin{animation:appSpin .8s linear infinite}@keyframes appSpin{to{transform:rotate(360deg)}}';
document.head.appendChild(style);
