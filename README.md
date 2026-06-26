# OnlyFields ERP

> Sistema de gestión integral para complejos deportivos, desarrollado bajo arquitectura de microservicios con Spring Boot 3.2 y Spring Cloud 2023.0.0.

---

## Índice

- [Descripción general](#descripción-general)
- [Arquitectura del sistema](#arquitectura-del-sistema)
- [Stack tecnológico](#stack-tecnológico)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Microservicios](#microservicios)
- [API Gateway](#api-gateway)
- [Comunicación entre servicios](#comunicación-entre-servicios)
- [Persistencia de datos](#persistencia-de-datos)
- [Seguridad](#seguridad)
- [Despliegue con Docker](#despliegue-con-docker)
- [Ejecución en entorno local](#ejecución-en-entorno-local)
- [Documentación de APIs](#documentación-de-apis)
- [Pruebas y cobertura](#pruebas-y-cobertura)
- [Equipo](#equipo)

---

## Descripción general

**OnlyFields** es un ERP orientado a la gestión operativa, financiera y de acceso de complejos deportivos. Digitaliza los procesos de reserva de canchas, punto de venta, control de acceso físico mediante tokens QR, suscripciones de socios, seguimiento físico y generación de reportes consolidados.

El sistema está implementado como un **monorepo Maven multi-módulo**, donde cada módulo encapsula un dominio de negocio independiente, siguiendo el patrón *Database per Service* y exponiéndose a través de un único punto de entrada vía **API Gateway**.

---

## Arquitectura del sistema

```
                        ┌─────────────────────────┐
                        │      Cliente externo    │
                        │  (Postman / Frontend)   │
                        └────────────┬────────────┘
                                     │ HTTP :8080
                        ┌────────────▼────────────┐
                        │       API Gateway       │
                        │  spring-cloud-gateway   │
                        │      :8080              │
                        └──────┬──────────┬───────┘
              ┌────────────────┤          ├────────────────┐
              │ onlyfields-network (bridge Docker)         │
   ┌──────────▼──────┐  ┌─────▼───────┐  ┌──────▼──────┐   │
   │  ms-usuarios    │  │ ms-reservas │  │   ms-pos    │   │
   │    :8081        │  │   :8082     │  │   :8083     │   │
   └─────────────────┘  └─────────────┘  └─────────────┘   │
   ┌─────────────────┐  ┌─────────────┐  ┌─────────────┐   │
   │ms-suscripciones │  │ms-inventario│  │  ms-accesos │   │
   │    :8084        │  │   :8085     │  │   :8086     │   │
   └─────────────────┘  └─────────────┘  └─────────────┘   │
   ┌─────────────────┐  ┌─────────────┐  ┌─────────────┐   │
   │ ms-seguimiento  │  │ ms-reportes │  │ms-configur. │   │
   │    :8087        │  │   :8088     │  │   :8089     │   │
   └─────────────────┘  └─────────────┘  └─────────────┘   │
   ┌─────────────────┐  ┌────────────────────────────┐     │
   │ms-notificaciones│  │       db-mysql :3306       │     │
   │    :8090        │  │    (MySQL 8.0 compartido)  │     │
   └─────────────────┘  └────────────────────────────┘     │
                                                           
```

### Decisiones arquitectónicas clave

| Decisión | Justificación |
|---|---|
| **Monorepo Maven multi-módulo** | Facilita la gestión centralizada de versiones y dependencias compartidas (Spring Boot, Spring Cloud) mediante el POM padre `Onlyfields-parent`. |
| **Gateway con routing estático** | Se descartó Eureka/Consul para evitar la complejidad de un servidor de descubrimiento. Las URIs de los microservicios se inyectan como variables de entorno, con fallback a `localhost` para desarrollo local. |
| **Database per Service** | Cada microservicio posee su propia base de datos MySQL, garantizando el desacoplamiento total del esquema de datos entre dominios. |
| **Flyway para migraciones** | Versionado de esquemas SQL (`V{N}__{descripcion}.sql`) con `ddl-auto: validate` en JPA, asegurando que el esquema real en MySQL siempre coincide con las entidades Hibernate. |
| **FeignClient para comunicación síncrona** | Declaración de clientes HTTP tipados entre microservicios, evitando el uso directo de `RestTemplate` o `WebClient` en lógica de negocio. |

---

## Stack tecnológico

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 17 (LTS) | Lenguaje de programación base |
| Spring Boot | 3.2.0 | Framework principal de cada microservicio |
| Spring Cloud | 2023.0.0 | API Gateway + OpenFeign |
| Spring Cloud Gateway | 2023.0.0 | Enrutamiento HTTP hacia los microservicios |
| Spring Cloud OpenFeign | 2023.0.0 | Comunicación síncrona entre servicios |
| Spring Data JPA | 3.2.0 | Capa de persistencia sobre MySQL |
| Spring Security | 6.2.0 | Seguridad en `ms-usuarios` (BCrypt) |
| Flyway | 9.22.3 | Migraciones de esquema versionadas |
| MySQL | 8.0 | Motor de base de datos relacional |
| Lombok | 1.18.x | Reducción de boilerplate en modelos y DTOs |
| Springdoc OpenAPI | 2.x | Generación automática de documentación Swagger UI |
| Jacoco | 0.8.11 | Medición de cobertura de pruebas unitarias |
| Docker | 25.x | Contenerización de cada módulo |
| Docker Compose | 2.x | Orquestación local de los 12 contenedores |
| Maven Wrapper | 3.9.x | Gestión de dependencias del monorepo |

---

## Estructura del repositorio

```
Onlyfields/                          ← Raíz del monorepo
├── pom.xml                          ← POM padre (Onlyfields-parent 1.0.0)
├── mvnw / mvnw.cmd                  ← Maven Wrapper
├── docker-compose.yml               ← Orquestación de los 12 contenedores
│
├── api-gateway/                     ← Módulo: punto de entrada único (:8080)
│   ├── pom.xml
│   ├── Dockerfile.validate
│   └── src/main/resources/
│       └── application.yml          ← 16 rutas de enrutamiento configuradas
│
├── usuarios/                        ← ms-usuarios (:8081)
├── reservas/                        ← ms-reservas (:8082)
├── pos/                             ← ms-pos (:8083)
├── suscripciones/                   ← ms-suscripciones (:8084)
├── inventario/                      ← ms-inventario (:8085)
├── accesos/                         ← ms-accesos (:8086)
├── seguimiento/                     ← ms-seguimiento (:8087)
├── reportes/                        ← ms-reportes (:8088)
├── configuracion/                   ← ms-configuracion (:8089)
└── notificaciones/                  ← ms-notificaciones (:8090)
```

Cada módulo sigue la estructura estándar de capas:

```
<modulo>/
├── pom.xml
├── Dockerfile.validate
└── src/
    ├── main/
    │   ├── java/com/fullstack/<modulo>/
    │   │   ├── controller/          ← Capa de presentación REST (@RestController)
    │   │   ├── service/             ← Lógica de negocio (@Service, @Transactional)
    │   │   ├── repository/          ← Acceso a datos (JpaRepository)
    │   │   ├── model/               ← Entidades JPA (@Entity)
    │   │   ├── dto/                 ← Objetos de transferencia de datos
    │   │   ├── client/              ← Clientes FeignClient hacia otros microservicios
    │   │   └── config/              ← Configuración de beans (@Configuration)
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/        ← Scripts Flyway (V1__init.sql, V2__...)
    └── test/
        └── java/com/fullstack/<modulo>/
            └── controller/          ← Tests unitarios con JUnit 5 + Mockito
```

---

## Microservicios

### Mapa de servicios

| Microservicio | Puerto | Base de datos | Responsabilidad principal |
|---|---|---|---|
| `ms-usuarios` | 8081 | `onlyfields_usuarios` | Gestión de identidad: registro, autenticación y roles |
| `ms-reservas` | 8082 | `onlyfields_reservas` | Reserva de canchas con validación de disponibilidad en tiempo real |
| `ms-pos` | 8083 | `onlyfields_pos` | Punto de venta: caja, transacciones y cuadratura financiera |
| `ms-suscripciones` | 8084 | `onlyfields_suscripciones` | Planes y contratos de suscripción de socios |
| `ms-inventario` | 8085 | `onlyfields_inventario` | Gestión de productos, categorías y movimientos de stock |
| `ms-accesos` | 8086 | `onlyfields_accesos` | Control de acceso físico mediante tokens QR dinámicos |
| `ms-seguimiento` | 8087 | `onlyfields_seguimiento` | Fichas clínicas y mediciones antropométricas de socios |
| `ms-reportes` | 8088 | `onlyfields_reportes` | Generación de reportes consolidados multi-fuente |
| `ms-configuracion` | 8089 | `onlyfields_configuracion` | Parámetros globales y calendario de feriados/bloqueos |
| `ms-notificaciones` | 8090 | `onlyfields_notificaciones` | Despacho de emails con control de reintentos e idempotencia |

### Endpoints por microservicio

<details>
<summary><strong>ms-usuarios</strong> — <code>/api/v1/usuarios</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/usuarios` | Lista todos los usuarios registrados |
| `GET` | `/api/v1/usuarios/{id}` | Obtiene un usuario por su ID |
| `POST` | `/api/v1/usuarios` | Registra un nuevo usuario (password hasheado con BCrypt) |
| `POST` | `/api/v1/usuarios/login` | Autentica credenciales del usuario |
| `DELETE` | `/api/v1/usuarios/{id}` | Elimina un usuario del sistema |

</details>

<details>
<summary><strong>ms-reservas</strong> — <code>/api/canchas</code> + <code>/api/v1/reservas</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/canchas` | Lista todas las canchas |
| `GET` | `/api/canchas/{id}` | Obtiene una cancha por ID |
| `POST` | `/api/canchas` | Registra una nueva cancha |
| `POST` | `/api/canchas/{id}/bloquear` | Crea un bloqueo horario |
| `DELETE` | `/api/canchas/{id}/desbloquear/{bloqueoId}` | Elimina un bloqueo horario |
| `GET` | `/api/v1/reservas` | Lista todas las reservas |
| `GET` | `/api/v1/reservas/{id}` | Obtiene una reserva por ID |
| `GET` | `/api/v1/reservas/cliente/{clienteId}` | Lista reservas por cliente |
| `POST` | `/api/v1/reservas` | Crea una nueva reserva |
| `PUT` | `/api/v1/reservas/{id}/confirmar` | Confirma una reserva pendiente |
| `PUT` | `/api/v1/reservas/{id}/cancelar` | Cancela una reserva |

> ⚠️ Este microservicio expone **dos `@RequestMapping` base distintos**, por lo que el API Gateway le asigna **dos rutas independientes**.

</details>

<details>
<summary><strong>ms-pos</strong> — <code>/api/v1/pos</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/v1/pos/caja/abrir` | Abre una jornada de caja |
| `POST` | `/api/v1/pos/caja/cerrar` | Cierra y cuadra la caja |
| `GET` | `/api/v1/pos/caja/actual` | Obtiene el estado de la caja activa |
| `POST` | `/api/v1/pos/transacciones` | Procesa una venta |
| `GET` | `/api/v1/pos/transacciones` | Historial de transacciones |
| `GET` | `/api/v1/pos/transacciones/{id}` | Obtiene una transacción por ID |

</details>

<details>
<summary><strong>ms-suscripciones</strong> — <code>/api/v1/suscripciones</code> + <code>/api/v1/planes</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/planes` | Lista los planes disponibles |
| `GET` | `/api/v1/planes/{id}` | Obtiene un plan por ID |
| `POST` | `/api/v1/suscripciones` | Crea una suscripción para un cliente |
| `GET` | `/api/v1/suscripciones/cliente/{clienteId}` | Suscripciones de un cliente |
| `PUT` | `/api/v1/suscripciones/{id}/congelar` | Suspende temporalmente una suscripción |
| `PUT` | `/api/v1/suscripciones/{id}/reactivar` | Reactiva una suscripción congelada |
| `PUT` | `/api/v1/suscripciones/{id}/cancelar` | Cancela una suscripción |
| `GET` | `/api/v1/suscripciones/{id}/historial` | Historial de cambios de estado |

</details>

<details>
<summary><strong>ms-inventario</strong> — <code>/api/v1/productos</code> + <code>/api/v1/categorias</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/categorias` | Lista todas las categorías |
| `POST` | `/api/v1/productos` | Registra un producto |
| `GET` | `/api/v1/productos` | Lista todos los productos |
| `GET` | `/api/v1/productos/{id}` | Obtiene un producto por ID |
| `PUT` | `/api/v1/productos/{id}` | Actualiza un producto |
| `DELETE` | `/api/v1/productos/{id}` | Elimina un producto |
| `PUT` | `/api/v1/productos/{id}/stock` | Registra movimiento de stock |
| `GET` | `/api/v1/productos/alertas` | Productos bajo stock mínimo |
| `GET` | `/api/v1/productos/{id}/movimientos` | Historial de movimientos |

</details>

<details>
<summary><strong>ms-accesos</strong> — <code>/api/v1/qr</code> + <code>/api/v1/accesos</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/v1/qr/generar` | Genera un token QR de acceso |
| `POST` | `/api/v1/accesos/validar` | Valida un QR y registra el ingreso |
| `GET` | `/api/v1/accesos/historial` | Historial global de movimientos |
| `GET` | `/api/v1/accesos/cliente/{clienteId}` | Historial de accesos por cliente |
| `GET` | `/api/v1/accesos/activos` | Clientes dentro del recinto en tiempo real |
| `POST` | `/api/v1/accesos/salida` | Registra salida manual de un cliente |

</details>

<details>
<summary><strong>ms-seguimiento</strong> — <code>/api/v1/seguimiento/fichas</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/v1/seguimiento/fichas` | Crea una ficha clínica |
| `GET` | `/api/v1/seguimiento/fichas/cliente/{clienteId}` | Ficha activa de un socio |
| `PUT` | `/api/v1/seguimiento/fichas/{id}` | Actualiza la ficha |
| `DELETE` | `/api/v1/seguimiento/fichas/{id}` | Elimina una ficha |
| `POST` | `/api/v1/seguimiento/fichas/{id}/mediciones` | Registra una medición |
| `GET` | `/api/v1/seguimiento/fichas/{id}/mediciones` | Historial de mediciones |

</details>

<details>
<summary><strong>ms-reportes</strong> — <code>/api/v1/reportes</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/reportes` | Lista todos los reportes generados |
| `GET` | `/api/v1/reportes/{id}` | Obtiene un reporte por ID |
| `POST` | `/api/v1/reportes` | Genera un nuevo reporte consolidado |
| `DELETE` | `/api/v1/reportes/{id}` | Elimina un reporte |

</details>

<details>
<summary><strong>ms-configuracion</strong> — <code>/api/v1/config</code> + <code>/api/v1/feriados</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/config` | Lista parámetros globales |
| `GET` | `/api/v1/config/{clave}` | Obtiene un parámetro por clave |
| `PUT` | `/api/v1/config/{clave}` | Actualiza un parámetro global |
| `POST` | `/api/v1/feriados` | Registra un feriado o bloqueo |
| `GET` | `/api/v1/feriados` | Lista feriados y bloqueos |
| `DELETE` | `/api/v1/feriados/{id}` | Elimina un feriado o bloqueo |

</details>

<details>
<summary><strong>ms-notificaciones</strong> — <code>/api/notificaciones</code></summary>

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/notificaciones` | Lista todas las notificaciones |
| `POST` | `/api/notificaciones/email` | Envía una notificación por email |
| `GET` | `/api/notificaciones/{id}` | Obtiene una notificación por ID |
| `GET` | `/api/notificaciones/cliente/{clienteId}` | Notificaciones de un cliente |
| `GET` | `/api/notificaciones/pendientes` | Notificaciones pendientes de envío |
| `POST` | `/api/notificaciones/reenviar/{id}` | Reenvía una notificación fallida |
| `POST` | `/api/notificaciones/enviar-comprobante` | Envía comprobante de reserva |

</details>

---

## API Gateway

El módulo `api-gateway` implementa **Spring Cloud Gateway** sobre WebFlux, actuando como reverse proxy y punto de entrada único al sistema.

### Configuración de routing (`application.yml`)

El gateway expone el puerto `8080` y define **16 rutas estáticas**, resolviendo URIs de destino desde variables de entorno con fallback a `localhost` para desarrollo local:

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: usuarios-route
          uri: ${MS_USUARIOS_URI:http://localhost:8081}
          predicates:
            - Path=/api/v1/usuarios/**

        - id: reservas-canchas-route
          uri: ${MS_RESERVAS_URI:http://localhost:8082}
          predicates:
            - Path=/api/canchas/**

        - id: reservas-route
          uri: ${MS_RESERVAS_URI:http://localhost:8082}
          predicates:
            - Path=/api/v1/reservas/**
        # ... (13 rutas adicionales)
```

> El número de rutas supera al número de microservicios porque `ms-reservas`, `ms-suscripciones`, `ms-inventario`, `ms-accesos` y `ms-configuracion` exponen múltiples `@RequestMapping` base que requieren predicates independientes.

### Tabla completa de rutas

| ID de ruta | Path predicate | Microservicio destino |
|---|---|---|
| `usuarios-route` | `/api/v1/usuarios/**` | ms-usuarios:8081 |
| `reservas-canchas-route` | `/api/canchas/**` | ms-reservas:8082 |
| `reservas-route` | `/api/v1/reservas/**` | ms-reservas:8082 |
| `pos-route` | `/api/v1/pos/**` | ms-pos:8083 |
| `suscripciones-route` | `/api/v1/suscripciones/**` | ms-suscripciones:8084 |
| `planes-route` | `/api/v1/planes/**` | ms-suscripciones:8084 |
| `inventario-productos-route` | `/api/v1/productos/**` | ms-inventario:8085 |
| `inventario-categorias-route` | `/api/v1/categorias/**` | ms-inventario:8085 |
| `accesos-qr-route` | `/api/v1/qr/**` | ms-accesos:8086 |
| `accesos-route` | `/api/v1/accesos/**` | ms-accesos:8086 |
| `seguimiento-route` | `/api/v1/seguimiento/**` | ms-seguimiento:8087 |
| `reportes-route` | `/api/v1/reportes/**` | ms-reportes:8088 |
| `configuracion-config-route` | `/api/v1/config/**` | ms-configuracion:8089 |
| `configuracion-feriados-route` | `/api/v1/feriados/**` | ms-configuracion:8089 |
| `notificaciones-route` | `/api/notificaciones/**` | ms-notificaciones:8090 |

---

## Comunicación entre servicios

La comunicación inter-servicio se implementa de forma **síncrona** mediante **Spring Cloud OpenFeign**. Cada microservicio declara interfaces `@FeignClient` para consumir los contratos REST de otros servicios, con la URL resuelta desde variables de entorno.

```java
@FeignClient(
    name = "ms-usuarios",
    url = "${ms.usuarios.url:http://onlyfields-ms-usuarios:8081}"
)
public interface UsuarioClient {
    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioDTO obtenerUsuario(@PathVariable Long id);
}
```

### Grafo de dependencias

```
ms-reservas     →  ms-usuarios, ms-accesos, ms-notificaciones, ms-pos
ms-pos          →  ms-usuarios, ms-inventario, ms-suscripciones, ms-notificaciones
ms-suscripciones → ms-usuarios, ms-notificaciones
ms-accesos      →  ms-usuarios, ms-suscripciones, ms-reservas
ms-seguimiento  →  ms-usuarios
ms-reportes     →  ms-reservas, ms-pos, ms-inventario, ms-suscripciones
ms-configuracion → ms-reservas, ms-pos
ms-notificaciones → (consumidor terminal, no llama a otros servicios)
ms-usuarios     → (servicio raíz, no llama a otros servicios)
```

---

## Persistencia de datos

### Estrategia Database per Service

Cada microservicio posee su propia base de datos MySQL, aislada del resto. La conexión se configura en `application.yml` de cada módulo:

```yaml
spring:
  datasource:
    url: jdbc:mysql://db-mysql:3306/onlyfields_<modulo>?createDatabaseIfNotExist=true&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate       # Hibernate valida contra el esquema real; Flyway lo crea
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Bases de datos

| Base de datos | Tablas principales |
|---|---|
| `onlyfields_usuarios` | `usuario`, `rol`, `usuario_roles` |
| `onlyfields_reservas` | `cancha`, `reserva`, `bloque_horario` |
| `onlyfields_pos` | `caja`, `transaccion`, `item_transaccion` |
| `onlyfields_suscripciones` | `plan`, `suscripcion`, `historial_estado` |
| `onlyfields_inventario` | `categorias`, `producto`, `movimiento_stock` |
| `onlyfields_accesos` | `qr_tokens`, `registro_acceso` |
| `onlyfields_seguimiento` | `ficha_cliente`, `medicion_corporal` |
| `onlyfields_reportes` | `reporte_generado` |
| `onlyfields_configuracion` | `configuracion_global`, `feriado_bloqueo` |
| `onlyfields_notificaciones` | `notificacion` |

### Migraciones Flyway

Los scripts de migración siguen el naming convention de Flyway:

```
db/migration/
├── V1__init_schema.sql       ← Creación del esquema inicial
└── V2__seed_data.sql         ← Datos iniciales (si aplica)
```

---

## Seguridad

### ms-usuarios — Spring Security + BCrypt

El microservicio de usuarios es el único con Spring Security activo funcionalmente:

- **`BCryptPasswordEncoder`**: las contraseñas nunca se persisten en texto plano. Se aplica hashing con BCrypt al registrar o actualizar credenciales.
- **`SecurityFilterChain`** con `permitAll()`: configuración abierta para la etapa de desarrollo. En producción se implementaría JWT u OAuth2.
- **CSRF deshabilitado**: apropiado para APIs REST stateless sin sesiones de navegador.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### ms-accesos — Control de acceso físico por QR

Implementa un segundo nivel de seguridad a nivel operativo:

- **Tokens QR dinámicos** con campo `fechaExpiracion` y flag `usado` (single-use).
- **Validación multi-condición** al escanear: suscripción vigente + reserva activa + token no usado previamente.
- **Auditoría completa**: cada evento de acceso registra `resultado` (PERMITIDO/DENEGADO) y `motivoRechazo`.

### Estado de seguridad por módulo

| Módulo | Spring Security | Estado en desarrollo |
|---|---|---|
| `ms-usuarios` | ✅ Activo | BCrypt + permitAll |
| `ms-accesos` | Removido | Seguridad a nivel de negocio (QR) |
| Resto de módulos (8) | Removido | Sin seguridad activa — pendiente JWT |

---

## Despliegue con Docker

El proyecto implementa un **build multi-stage** en cada `Dockerfile.validate`. El contexto de build es siempre la **raíz del repositorio** (no la carpeta del módulo), ya que Maven necesita resolver las dependencias del monorepo completo.

### `Dockerfile.validate` (patrón por módulo)

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

# Se copia el repositorio completo para que Maven resuelva el multi-módulo
COPY pom.xml .
COPY .mvn ./.mvn
COPY mvnw .
COPY usuarios ./usuarios
COPY reservas ./reservas
COPY pos ./pos
COPY suscripciones ./suscripciones
COPY inventario ./inventario
COPY accesos ./accesos
COPY seguimiento ./seguimiento
COPY reportes ./reportes
COPY configuracion ./configuracion
COPY notificaciones ./notificaciones
COPY api-gateway ./api-gateway

RUN chmod +x mvnw && sed -i 's/\r$//' mvnw
RUN ./mvnw clean package -DskipTests -pl <modulo> -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/<modulo>/target/<modulo>-1.0.0.jar /app/app.jar
EXPOSE <puerto>
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

> La instrucción `sed -i 's/\r$//'` resuelve incompatibilidades de saltos de línea CRLF/LF al construir en Linux desde un repositorio editado en Windows.

### `docker-compose.yml`

Orquesta **12 contenedores** en la red bridge `onlyfields-network`:

```yaml
services:
  db-mysql:
    image: mysql:8.0
    container_name: onlyfields-db-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: onlyfields_usuarios
    ports:
      - "3307:3306"     # Puerto 3307 en el host para no colisionar con MySQL local
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - onlyfields-network

  ms-usuarios:
    build:
      context: .
      dockerfile: usuarios/Dockerfile.validate
    container_name: onlyfields-ms-usuarios
    ports:
      - "8081:8081"
    depends_on:
      - db-mysql
    networks:
      - onlyfields-network

  # ... (ms-reservas:8082 a ms-notificaciones:8090)

  api-gateway:
    build:
      context: .
      dockerfile: api-gateway/Dockerfile.validate
    container_name: onlyfields-api-gateway
    ports:
      - "8080:8080"
    environment:
      MS_USUARIOS_URI:      http://onlyfields-ms-usuarios:8081
      MS_RESERVAS_URI:      http://onlyfields-ms-reservas:8082
      MS_POS_URI:           http://onlyfields-ms-pos:8083
      MS_SUSCRIPCIONES_URI: http://onlyfields-ms-suscripciones:8084
      MS_INVENTARIO_URI:    http://onlyfields-ms-inventario:8085
      MS_ACCESOS_URI:       http://onlyfields-ms-accesos:8086
      MS_SEGUIMIENTO_URI:   http://onlyfields-ms-seguimiento:8087
      MS_REPORTES_URI:      http://onlyfields-ms-reportes:8088
      MS_CONFIGURACION_URI: http://onlyfields-ms-configuracion:8089
      MS_NOTIFICACIONES_URI:http://onlyfields-ms-notificaciones:8090
    depends_on:
      - ms-usuarios
      # ... resto de microservicios
    networks:
      - onlyfields-network

networks:
  onlyfields-network:
    driver: bridge

volumes:
  mysql-data:
```

---

## Ejecución en entorno local

### Prerrequisitos

- Java 17
- Docker Desktop 4.x o superior
- Maven 3.9.x (o usar el wrapper `./mvnw` incluido)
- Git

### Clonar el repositorio

```bash
git clone https://github.com/<org>/Onlyfields.git
cd Onlyfields
```

### Compilar el monorepo localmente

```bash
# Compilar todos los módulos (incluye fase de test)
./mvnw clean package

# Compilar omitiendo tests (más rápido, para validar compilación)
./mvnw clean package -DskipTests
```

### Levantar con Docker Compose

```bash
# Primera vez — construye todas las imágenes Docker y levanta los contenedores
docker compose up --build

# Ejecución posterior (sin reconstruir imágenes si no hubo cambios)
docker compose up

# Detener todos los contenedores
docker compose down

# Detener y eliminar volúmenes (reinicia las bases de datos)
docker compose down -v
```

### Verificar que el sistema está operativo

```bash
# Ver estado de los 12 contenedores
docker ps

# Logs de un microservicio específico
docker compose logs ms-usuarios -f

# Logs del API Gateway
docker compose logs api-gateway -f
```

### Verificar el API Gateway

```bash
# A través del gateway (puerto 8080)
curl http://localhost:8080/api/v1/usuarios

# Directo al microservicio (puerto 8081)
curl http://localhost:8081/api/v1/usuarios
```

Ambas peticiones deben retornar el mismo resultado.

---

## Documentación de APIs

Todos los microservicios exponen documentación interactiva generada automáticamente por **Springdoc OpenAPI (Swagger UI)**:

| Microservicio | Swagger UI |
|---|---|
| ms-usuarios | http://localhost:8081/doc/swagger-ui.html |
| ms-reservas | http://localhost:8082/doc/swagger-ui.html |
| ms-pos | http://localhost:8083/doc/swagger-ui.html |
| ms-suscripciones | http://localhost:8084/doc/swagger-ui.html |
| ms-inventario | http://localhost:8085/doc/swagger-ui.html |
| ms-accesos | http://localhost:8086/doc/swagger-ui.html |
| ms-seguimiento | http://localhost:8087/doc/swagger-ui.html |
| ms-reportes | http://localhost:8088/doc/swagger-ui.html |
| ms-configuracion | http://localhost:8089/doc/swagger-ui.html |
| ms-notificaciones | http://localhost:8090/doc/swagger-ui.html |

> La Swagger UI está disponible con el sistema corriendo localmente (con o sin Docker). La ruta `/doc/swagger-ui.html` es una personalización respecto a la ruta por defecto de Springdoc.

---

## Pruebas y cobertura

### Pruebas unitarias

Cada microservicio implementa pruebas con **JUnit 5** y **Mockito** sobre la capa de Service y Controller. La cobertura se mide con el plugin **Jacoco 0.8.11**.

**Clases excluidas de la medición** (por convención del proyecto):

```
com/fullstack/<modulo>/model/**
com/fullstack/<modulo>/dto/**
com/fullstack/<modulo>/config/**
com/fullstack/<modulo>/client/**
com/fullstack/<modulo>/*Application.class
```

### Generar reporte de cobertura

```bash
# Todos los módulos
./mvnw test jacoco:report

# Módulo específico
./mvnw test jacoco:report -pl usuarios -am
```

El reporte HTML se genera en:

```
<modulo>/target/site/jacoco/index.html
```

### Compilar módulo específico

```bash
# Ejemplo: recompilar solo el gateway sin afectar el resto
./mvnw clean package -DskipTests -pl api-gateway -am
```

---

## Equipo

| Nombre | Rol |
|---|---|
| Sebastian De la Paz | Desarrollador FullStack |
| Sergio Sepúlveda | Desarrollador FullStack |
| Gabriel Zurita | Desarrollador FullStack |

**Asignatura:** Desarrollo FullStack I  
**Docente:** Marcelo Eduardo Crisostomo Carrasco  
**Institución:** DUOC UC — Sede Puerto Montt  
**Año:** 2026

---

<p align="center">
  <sub>OnlyFields ERP · Spring Boot 3.2.0 · Spring Cloud 2023.0.0 · Java 17 · MySQL 8.0 · Docker</sub>
</p>
