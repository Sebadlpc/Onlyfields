-- 1. CREACIÓN DE SECUENCIAS
CREATE SEQUENCE PERMISO_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ROL_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE USUARIO_SEQ START WITH 1 INCREMENT BY 1;

-- 2. TABLA: PERMISO
CREATE TABLE permiso (
    id NUMBER(19,0) NOT NULL,
    modulo VARCHAR2(50) NOT NULL,
    accion VARCHAR2(20) NOT NULL,
    CONSTRAINT pk_permiso PRIMARY KEY (id)
);

-- 3. TABLA: ROL
CREATE TABLE rol (
    id NUMBER(19,0) NOT NULL,
    nombre VARCHAR2(50) NOT NULL,
    descripcion VARCHAR2(255),
    CONSTRAINT pk_rol PRIMARY KEY (id),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

-- 4. TABLA INTERMEDIA: ROL_PERMISO (Relación Many-to-Many)
CREATE TABLE rol_permiso (
    rol_id NUMBER(19,0) NOT NULL,
    permiso_id NUMBER(19,0) NOT NULL,
    CONSTRAINT fk_rol_permiso_rol FOREIGN KEY (rol_id) REFERENCES rol(id),
    CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (permiso_id) REFERENCES permiso(id)
);

-- 5. TABLA: USUARIO
CREATE TABLE usuario (
    id_usuario NUMBER(19,0) NOT NULL,
    nombre VARCHAR2(255) NOT NULL,
    correo_electronico VARCHAR2(255) NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    estado VARCHAR2(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    rol_id NUMBER(19,0) NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuario_email UNIQUE (correo_electronico),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
);

-- 6. DATA INICIAL (Seed) - Para evitar el error de integridad en Postman
INSERT INTO rol (id, nombre, descripcion) VALUES (ROL_SEQ.NEXTVAL, 'ADMIN', 'Administrador General');
INSERT INTO rol (id, nombre, descripcion) VALUES (ROL_SEQ.NEXTVAL, 'CLIENTE', 'Cliente del Complejo');
COMMIT;