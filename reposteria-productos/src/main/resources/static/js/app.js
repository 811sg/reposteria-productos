/* Interfaz del módulo de productos: HTML5 + Bootstrap 5 + JavaScript (fetch contra la API REST). */
'use strict';

let token = sessionStorage.getItem('token');
let usuario = sessionStorage.getItem('usuario');
let rol = sessionStorage.getItem('rol');

const $ = (id) => document.getElementById(id);
const esAdmin = () => rol === 'ADMIN';
const moneda = (n) => Number(n).toLocaleString('es-CO', { style: 'currency', currency: 'COP' });
const esc = (t) => String(t ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

const modalProducto = new bootstrap.Modal($('modalProducto'));
const modalDetalle = new bootstrap.Modal($('modalDetalle'));
const modalHistorial = new bootstrap.Modal($('modalHistorial'));
const toast = new bootstrap.Toast($('toast'), { delay: 3000 });

function aviso(texto) {
    $('toastTexto').textContent = texto;
    toast.show();
}

/* ---------- Llamadas a la API ---------- */
async function api(ruta, opciones = {}) {
    const headers = { ...(opciones.headers || {}) };
    if (token) headers['Authorization'] = 'Bearer ' + token;
    if (opciones.body && !(opciones.body instanceof FormData)) headers['Content-Type'] = 'application/json';
    const res = await fetch(ruta, { ...opciones, headers });
    const datos = res.status === 204 ? null : await res.json().catch(() => null);
    if (res.status === 401 && !ruta.startsWith('/api/auth')) {
        cerrarSesion();
        throw { mensaje: 'Su sesión expiró. Inicie sesión de nuevo.', errores: [] };
    }
    if (!res.ok) throw datos || { mensaje: 'Error ' + res.status, errores: [] };
    return datos;
}

/* ---------- Sesión ---------- */
$('formLogin').addEventListener('submit', async (e) => {
    e.preventDefault();
    $('loginError').classList.add('d-none');
    try {
        const r = await api('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username: $('loginUsuario').value.trim(), password: $('loginClave').value })
        });
        token = r.token; usuario = r.username; rol = r.rol;
        sessionStorage.setItem('token', token);
        sessionStorage.setItem('usuario', usuario);
        sessionStorage.setItem('rol', rol);
        $('loginClave').value = '';
        mostrarApp();
    } catch (err) {
        $('loginError').textContent = err.mensaje || 'No fue posible iniciar sesión';
        $('loginError').classList.remove('d-none');
    }
});

function cerrarSesion() {
    sessionStorage.clear();
    token = usuario = rol = null;
    $('vistaApp').classList.add('d-none');
    $('vistaLogin').classList.remove('d-none');
}
$('btnSalir').addEventListener('click', cerrarSesion);

function mostrarApp() {
    $('vistaLogin').classList.add('d-none');
    $('vistaApp').classList.remove('d-none');
    $('usuarioActual').textContent = usuario + ' (' + rol.toLowerCase() + ')';
    document.querySelectorAll('.solo-admin').forEach((el) => el.classList.toggle('d-none', !esAdmin()));
    cargarCategorias();
    cargarProductos();
}

/* ---------- Categorías (RF-09) ---------- */
async function cargarCategorias() {
    try {
        const categorias = await api('/api/productos/categorias');
        const seleccionada = $('buscarCategoria').value;
        $('buscarCategoria').innerHTML = '<option value="">Todas</option>' +
            categorias.map((c) => `<option>${esc(c)}</option>`).join('');
        $('buscarCategoria').value = seleccionada;
        $('listaCategorias').innerHTML = ['Torta', 'Postre', 'Galleta', ...categorias.filter((c) => !['Torta', 'Postre', 'Galleta'].includes(c))]
            .map((c) => `<option value="${esc(c)}">`).join('');
    } catch (e) { /* las categorías son un apoyo: si fallan, la lista principal muestra el error */ }
}

/* ---------- Listar y buscar (RF-05, RF-06, RF-08, RF-11) ---------- */
async function cargarProductos() {
    const nombre = $('buscarNombre').value.trim();
    const categoria = $('buscarCategoria').value;
    const solo = $('soloDisponibles').checked;
    $('mensajeLista').classList.add('d-none');
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
        pintarTabla(lista);
    } catch (e) {
        $('mensajeLista').textContent = e.mensaje || 'No se pudo cargar el listado';
        $('mensajeLista').classList.remove('d-none');
    }
}

function pintarTabla(lista) {
    $('cuerpoTabla').innerHTML = lista.length ? lista.map((p) => `
        <tr>
            <td>${p.imagen
                ? `<img class="miniatura" src="${esc(p.imagen)}" alt="Imagen de ${esc(p.nombre)}">`
                : '<div class="miniatura" aria-hidden="true">🍰</div>'}</td>
            <td><strong>${esc(p.nombre)}</strong><div class="small text-muted">${esc(p.descripcion)}</div></td>
            <td>${esc(p.categoria)}</td>
            <td class="text-end">${moneda(p.precio)}</td>
            <td><span class="badge ${p.disponibilidad ? 'estado-ok' : 'estado-off'}">${p.disponibilidad ? 'Disponible' : 'Desactivado'}</span></td>
            <td class="text-end text-nowrap">
                <button class="btn btn-sm btn-outline-secondary" data-accion="ver" data-id="${p.idProducto}">Ver</button>
                ${esAdmin() ? `
                <button class="btn btn-sm btn-outline-secondary" data-accion="editar" data-id="${p.idProducto}">Editar</button>
                ${p.disponibilidad
                    ? `<button class="btn btn-sm btn-outline-danger" data-accion="desactivar" data-id="${p.idProducto}">Desactivar</button>`
                    : `<button class="btn btn-sm btn-outline-success" data-accion="activar" data-id="${p.idProducto}">Activar</button>`}` : ''}
            </td>
        </tr>`).join('')
        : '<tr><td colspan="6" class="text-center text-muted py-4">No hay productos que coincidan con la búsqueda. Pruebe con otro nombre o categoría.</td></tr>';
    $('conteo').textContent = lista.length + (lista.length === 1 ? ' producto' : ' productos');
}

$('formBuscar').addEventListener('submit', (e) => { e.preventDefault(); cargarProductos(); });
$('soloDisponibles').addEventListener('change', cargarProductos);
$('btnLimpiar').addEventListener('click', () => {
    $('buscarNombre').value = ''; $('buscarCategoria').value = ''; $('soloDisponibles').checked = false;
    cargarProductos();
});

$('cuerpoTabla').addEventListener('click', async (e) => {
    const boton = e.target.closest('button[data-accion]');
    if (!boton) return;
    const id = boton.dataset.id;
    try {
        switch (boton.dataset.accion) {
            case 'ver': return await verDetalle(id);
            case 'editar': return await abrirEditar(id);
            case 'desactivar':
                if (!confirm('¿Desactivar este producto? Se conservará su información, pero no podrá seleccionarse en nuevos pedidos.')) return;
                await api(`/api/productos/${id}/desactivar`, { method: 'PATCH' });
                aviso('Producto desactivado');
                break;
            case 'activar':
                await api(`/api/productos/${id}/activar`, { method: 'PATCH' });
                aviso('Producto activado');
                break;
        }
        cargarProductos();
    } catch (err) {
        aviso(err.mensaje || 'No se pudo completar la acción');
    }
});

/* ---------- Consultar detalle (RF-02) + Decorator ---------- */
async function verDetalle(id) {
    const p = await api('/api/productos/' + id);
    $('cuerpoDetalle').innerHTML = `
        ${p.imagen ? `<img class="detalle-img mb-3" src="${esc(p.imagen)}" alt="Imagen de ${esc(p.nombre)}">` : ''}
        <h3 class="h5 titulo">${esc(p.nombre)}</h3>
        <p>${esc(p.descripcion)}</p>
        <dl class="row mb-3">
            <dt class="col-5">Categoría</dt><dd class="col-7">${esc(p.categoria)}</dd>
            <dt class="col-5">Precio</dt><dd class="col-7">${moneda(p.precio)}</dd>
            <dt class="col-5">Estado</dt><dd class="col-7">${p.disponibilidad ? 'Disponible' : 'Desactivado'}</dd>
            <dt class="col-5">Registrado</dt><dd class="col-7">${new Date(p.fechaRegistro).toLocaleString('es-CO')}</dd>
        </dl>
        <hr>
        <h4 class="h6">Simular descuento o promoción</h4>
        <div class="row g-2 mb-2">
            <div class="col-4"><input id="simDescuento" type="number" min="0" max="100" class="form-control" placeholder="% desc." aria-label="Porcentaje de descuento"></div>
            <div class="col-8"><input id="simPromo" class="form-control" placeholder="Ej: 2x1 los viernes" aria-label="Texto de promoción"></div>
        </div>
        <button id="btnSimular" class="btn btn-sm btn-outline-secondary" type="button">Ver precio final</button>
        <div id="resultadoSim" class="mt-2 small"></div>`;
    $('btnSimular').addEventListener('click', async () => {
        const q = new URLSearchParams();
        if ($('simDescuento').value) q.set('descuento', $('simDescuento').value);
        if ($('simPromo').value.trim()) q.set('promocion', $('simPromo').value.trim());
        try {
            const r = await api(`/api/productos/${id}/presentacion?` + q);
            $('resultadoSim').innerHTML = `Precio final: <strong>${moneda(r.precioFinal)}</strong> (antes ${moneda(r.precioOriginal)})<br>${esc(r.descripcion)}`;
        } catch (err) {
            $('resultadoSim').textContent = (err.errores && err.errores[0]) || err.mensaje;
        }
    });
    modalDetalle.show();
}

/* ---------- Registrar y editar (RF-01, RF-03, RF-07, RF-10) ---------- */
function limpiarForm() {
    $('formProducto').reset();
    $('pId').value = '';
    $('erroresForm').classList.add('d-none');
    $('formProducto').classList.remove('was-validated');
}

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
    modalProducto.show();
}

function mostrarErrores(err) {
    const lista = (err.errores && err.errores.length) ? err.errores : [err.mensaje || 'No se pudo guardar el producto'];
    $('erroresForm').innerHTML = '<strong>Corrija lo siguiente:</strong><ul class="mb-0">' + lista.map((m) => `<li>${esc(m)}</li>`).join('') + '</ul>';
    $('erroresForm').classList.remove('d-none');
}

$('formProducto').addEventListener('submit', async (e) => {
    e.preventDefault();
    $('erroresForm').classList.add('d-none');
    if (!e.target.checkValidity()) {
        e.target.classList.add('was-validated');
        mostrarErrores({ errores: ['Complete los campos obligatorios (nombre, descripción, categoría y un precio mayor que cero)'] });
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
    try {
        const guardado = await api(id ? '/api/productos/' + id : '/api/productos', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(datos)
        });
        const archivo = $('pImagen').files[0];
        if (archivo) {
            const fd = new FormData();
            fd.append('archivo', archivo);
            await api(`/api/productos/${guardado.idProducto}/imagen`, { method: 'POST', body: fd });
        }
        modalProducto.hide();
        aviso(id ? 'Producto actualizado' : 'Producto registrado');
        cargarCategorias();
        cargarProductos();
    } catch (err) {
        mostrarErrores(err);
    } finally {
        $('btnGuardar').disabled = false;
    }
});

/* ---------- Historial (traza de responsabilidad) ---------- */
$('btnHistorial').addEventListener('click', async () => {
    try {
        const filas = await api('/api/auditoria');
        $('cuerpoHistorial').innerHTML = filas.length ? filas.map((f) => `
            <tr><td>${new Date(f.fecha).toLocaleString('es-CO')}</td><td>${esc(f.usuario)}</td>
            <td>${esc(f.accion)}</td><td>${esc(f.nombreProducto)} (#${esc(f.idProducto)})</td></tr>`).join('')
            : '<tr><td colspan="4" class="text-muted">Aún no hay cambios registrados.</td></tr>';
        modalHistorial.show();
    } catch (err) {
        aviso(err.mensaje || 'No se pudo cargar el historial');
    }
});

/* ---------- Arranque ---------- */
if (token && usuario && rol) mostrarApp();
