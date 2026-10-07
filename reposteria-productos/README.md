# Módulo de Productos – Sistema de gestión de pedidos para repostería

Proyecto de **Arquitectura de Sistemas** (Universidad Católica Luis Amigó, 2026).
Integrantes: Mariana Piedrahita Florez, Sara Melisa Sanchez Vasquez, Sebastian Gonzalez Campiño.
Docente: Javier Dario Cardona Cardenas.

Stack: Java 17 · Spring Boot · Spring Web · Spring Data JPA · Hibernate · MySQL · Jakarta Validation ·
Spring Security · JWT · Lombok · Maven · Swagger/OpenAPI · HTML5 · CSS3 · JavaScript · Bootstrap 5 · Git/GitHub · Postman.

---

## 1. Qué necesita instalado

| Programa | Versión | Para qué |
|---|---|---|
| JDK | **17** (recomendado; también funciona 21) | Ejecutar Java |
| Maven | 3.9+ (o use IntelliJ / VS Code / Eclipse, que ya lo traen) | Descargar dependencias y ejecutar |
| MySQL | 8.x (Community Server) | Base de datos |
| Navegador | Chrome, Edge o Firefox actual | Ver la interfaz (necesita internet para Bootstrap y la fuente) |

## 2. Paso a paso para ver el resultado

### Paso 1. Descomprimir
Descomprima `reposteria-productos.zip` y abra la carpeta `reposteria-productos`.

### Paso 2. Tener MySQL funcionando

**Instalar MySQL (si no lo tiene):**
- Descargue *MySQL Installer* desde https://dev.mysql.com/downloads/installer/ (Windows) y elija *Server only* (o *Developer Default*).
- Durante la instalación defina la contraseña del usuario `root` (recomendado: `root`, así no tiene que cambiar nada).
- Deje la opción de iniciar MySQL como servicio de Windows. Para comprobarlo, abra *Servicios* y verifique que `MySQL80` esté "En ejecución".
- Alternativa: instalar XAMPP, iniciar el módulo *MySQL* y usar usuario `root` con contraseña vacía (en ese caso ponga `spring.datasource.password=` vacío en `application.properties`).

La aplicación crea sola la base de datos `reposteria_db` y las tablas; no tiene que crear nada a mano.
Por defecto usa usuario `root` y contraseña `root`. Si su contraseña es otra, cámbiela en
`src/main/resources/application.properties` (líneas `spring.datasource.username` / `password`)
o defina las variables de entorno `DB_USER` y `DB_PASSWORD`.

### Paso 3. Ejecutar la aplicación
En una terminal, dentro de la carpeta del proyecto:
```
mvn spring-boot:run
```
La primera vez descarga dependencias (1–3 minutos). Cuando aparezca `Started ProductosApplication` ya está lista.

*Con un IDE:* abra la carpeta como proyecto Maven y ejecute la clase `ProductosApplication`.

### Paso 4. Abrir la interfaz web
Entre a **http://localhost:8080** e inicie sesión:

| Usuario | Contraseña | Permisos |
|---|---|---|
| `admin` | `admin123` | Registrar, editar, desactivar/activar, subir imagen, ver historial |
| `consultor` | `consultor123` | Solo consultar, buscar y listar |

Al iniciar por primera vez se cargan 6 productos de ejemplo.

### Paso 5. Probar cada requisito
Con el usuario `admin`:
1. **Listar (RF-06):** la tabla principal muestra nombre, categoría, precio y estado.
2. **Registrar (RF-01, RF-07, RF-09, RF-10, RF-12):** botón *Nuevo producto*. Deje el nombre vacío o ponga precio 0 para ver los mensajes de validación (RNF-06). Complete los datos, elija una imagen y guarde. Verá la fecha de registro en *Ver*.
3. **Buscar (RF-05):** escriba un nombre y/o elija una categoría → *Buscar*.
4. **Solo disponibles (RF-08, RF-11):** marque *Solo disponibles*.
5. **Consultar (RF-02):** botón *Ver*. Allí también puede simular descuento/promoción (patrón Decorator).
6. **Editar (RF-03):** botón *Editar*.
7. **Desactivar (RF-04):** botón *Desactivar*; el producto sigue en el sistema con estado "Desactivado" y no aparece en *Solo disponibles*. *Activar* lo habilita de nuevo.
8. **Traza de responsabilidad:** botón *Historial de cambios* (quién hizo cada acción).
9. **Seguridad (RNF-03):** cierre sesión, entre como `consultor` y compruebe que no aparecen los botones de modificación. Si intenta modificar por la API con ese usuario, responde 403.

### Paso 6. Swagger (documentación y pruebas de la API)
Abra **http://localhost:8080/swagger-ui.html** → `POST /api/auth/login` → *Try it out* con
`{"username":"admin","password":"admin123"}` → copie el valor de `token` → botón **Authorize** → pegue el token → ya puede probar todos los endpoints.

### Paso 7. Postman (opcional)
1. `POST http://localhost:8080/api/auth/login` con body JSON `{"username":"admin","password":"admin123"}`.
2. En las demás peticiones agregue el encabezado `Authorization: Bearer <token>`.
3. Ejemplo `POST http://localhost:8080/api/productos`:
```json
{
  "nombre": "Cupcake de vainilla",
  "descripcion": "Cupcake con crema de mantequilla",
  "categoria": "Cupcake",
  "precio": 6500,
  "disponibilidad": true
}
```

### Ejecutar pruebas unitarias
```
mvn test
```

---

## 3. Endpoints

| Método y ruta | Requisito | Rol |
|---|---|---|
| `POST /api/auth/login` | Autenticación JWT | público |
| `POST /api/productos` | RF-01 Registrar | ADMIN |
| `GET /api/productos/{id}` | RF-02 Consultar | autenticado |
| `PUT /api/productos/{id}` | RF-03 Editar | ADMIN |
| `PATCH /api/productos/{id}/desactivar` | RF-04 Desactivar | ADMIN |
| `PATCH /api/productos/{id}/activar` | Reactivar | ADMIN |
| `GET /api/productos/buscar?nombre=&categoria=` | RF-05 Buscar | autenticado |
| `GET /api/productos` | RF-06 Listar | autenticado |
| `GET /api/productos/disponibles` | RF-08 / RF-11 | autenticado |
| `GET /api/productos/categorias` | RF-09 Categorías | autenticado |
| `POST /api/productos/{id}/imagen` (multipart `archivo`) | RF-10 Imágenes | ADMIN |
| `GET /api/productos/{id}/presentacion?descuento=&promocion=` | Decorator | autenticado |
| `GET /api/auditoria` | Traza de responsabilidad | ADMIN |

## 4. Requisitos funcionales → dónde están

| ID | Implementación |
|---|---|
| RF-01 | `ProductoController.registrar` → `RegistrarProductoOperation` |
| RF-02 | `ProductoController.consultar` · botón *Ver* |
| RF-03 | `ProductoController.editar` → `ActualizarProductoOperation` |
| RF-04 | `ProductoController.desactivar` → `DesactivarProductoOperation` (conserva datos, `disponibilidad=false`) |
| RF-05 | `ProductoServiceImpl.buscar` + estrategias `Busqueda*Strategy` (nombre, categoría o ambas) |
| RF-06 | `ProductoController.listar` · tabla principal |
| RF-07 | `ProductoValidator` + anotaciones Jakarta en `ProductoRequest` |
| RF-08 | Campo `disponibilidad` y badge de estado (Disponible / Desactivado) |
| RF-09 | Campo `categoria`, `GET /categorias`, factories por tipo (Torta, Postre, Galleta) y filtro por categoría |
| RF-10 | `POST /{id}/imagen` + patrón Adapter (`ImageStorage`) |
| RF-11 | `GET /disponibles` · casilla *Solo disponibles* |
| RF-12 | `Producto.alCrear()` (`@PrePersist`) genera `fecha_registro` |

## 5. Requisitos no funcionales → dónde están

| ID | Implementación |
|---|---|
| RNF-01 Usabilidad | Interfaz con formularios, botones rotulados y tabla organizada (`static/`) |
| RNF-02 Rendimiento | Búsquedas y listados resueltos con consultas derivadas de Spring Data directamente en MySQL (`ProductoRepository`) |
| RNF-03 Seguridad | Spring Security + JWT; registro/edición/desactivación solo ADMIN (`SecurityConfig` y `ProductoServiceProxy`) |
| RNF-04 Integridad | Columnas `NOT NULL`, tipos adecuados (`DECIMAL(10,2)`, etc.), id único autogenerado, nombre único |
| RNF-05 Disponibilidad | Módulo integrado a la aplicación principal; sin procesos manuales |
| RNF-06 Validación | `ProductoValidator` + `GlobalExceptionHandler` devuelven qué campos corregir |

## 6. Patrones de diseño (9)

| Patrón | Tipo | Clases |
|---|---|---|
| Factory Method | Creacional | `ProductoFactory`, `ProductoTortaFactory`, `ProductoPostreFactory`, `ProductoGalletaFactory` (+ `ProductoGenericoFactory`, `ProductoFactoryProvider`) |
| Builder | Creacional | `ProductoBuilder` |
| Strategy | Comportamiento | `BusquedaProductoStrategy`, `BusquedaPorNombreStrategy`, `BusquedaPorCategoriaStrategy`, `BusquedaPorNombreYCategoriaStrategy` |
| Facade | Estructural | `ProductoFacade` (usada por `ProductoController`) |
| Observer | Comportamiento | `ProductoSubject`, `ProductoObserver`, `AuditoriaProductoObserver`, `NotificacionProductoObserver` |
| Adapter | Estructural | `ImageStorage`, `LocalImageStorage`, `ExternalImageService`, `ImageStorageAdapter` (cambie `imagenes.storage=external` en `application.properties` para usar el servicio externo simulado) |
| Decorator | Estructural | `ProductoComponent`, `ProductoBase`, `ProductoDecorator`, `DescuentoDecorator`, `PromocionDecorator` |
| Template Method | Comportamiento | `ProductoOperationTemplate`, `RegistrarProductoOperation`, `ActualizarProductoOperation`, `DesactivarProductoOperation` (+ `ActivarProductoOperation`) |
| Proxy | Estructural | `ProductoService` (interfaz), `ProductoServiceImpl`, `ProductoServiceProxy` |

Flujo de una petición protegida: `ProductoController → ProductoFacade → ProductoServiceProxy (autenticación/rol) → ProductoServiceImpl → Operation (Template) → Validator → Factory/Builder → Repository → Subject (Observer)`.

## 7. Atributos de calidad

- **Seguridad:** autenticidad (login + JWT), confidencialidad (toda consulta exige sesión), integridad (validaciones + permisos), traza de responsabilidad (tabla `auditoria_producto` / *Historial*), comprobación de hechos (validación previa de datos).
- **Portabilidad:** aplicación web estándar (HTML/CSS/JS + REST); Bootstrap 5 para pantallas adaptables; sin instalación en el cliente.
- **Usabilidad:** opciones claramente rotuladas, formularios simples, mensajes de error que indican qué corregir, diseño legible.

## 8. Problemas frecuentes

| Síntoma | Solución |
|---|---|
| `Access denied for user 'root'` | La contraseña de MySQL no es `root`: cámbiela en `application.properties` o use `DB_PASSWORD`. |
| `Communications link failure` | MySQL no está iniciado (revise el servicio en *Servicios* de Windows). |
| `Port 8080 was already in use` | Cierre lo que use el puerto o agregue `server.port=8081` en `application.properties`. |
| La interfaz se ve sin estilos | Se necesita internet para cargar Bootstrap y la fuente desde CDN. |
| `release version 17 not supported` | Está usando un JDK anterior a 17. |
