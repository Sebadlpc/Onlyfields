CREATE TABLE permiso (
    id NUMBER(19,0) NOT NULL,
    modulo VARCHAR2(50) NOT NULL,
    accion VARCHAR2(20) NOT NULL,
    CONSTRAINT pk_permiso PRIMARY KEY (id)
);

CREATE TABLE rol (
    id NUMBER(19,0) NOT NULL,
    nombre VARCHAR2(50) NOT NULL,
    descripcion VARCHAR2(255),
    CONSTRAINT pk_rol PRIMARY KEY (id),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

CREATE TABLE rol_permiso (
    rol_id NUMBER(19,0) NOT NULL,
    permiso_id NUMBER(19,0) NOT NULL,
    CONSTRAINT fk_rol_permiso_rol FOREIGN KEY (rol_id) REFERENCES rol(id),
    CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (permiso_id) REFERENCES permiso(id)
);

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

INSERT INTO rol (id, nombre, descripcion) 
VALUES (1, 'ADMIN', 'Administrador total del complejo deportivo');

INSERT INTO rol (id, nombre, descripcion) 
VALUES (2, 'CLIENTE', 'Usuario regular (deportista/arrendatario)');

INSERT INTO rol (id, nombre, descripcion) 
VALUES (2, 'STAFF DP', 'Staff DP');

INSERT INTO permiso (id, modulo, accion) VALUES (1, 'USUARIOS', 'LEER');
INSERT INTO permiso (id, modulo, accion) VALUES (2, 'USUARIOS', 'ESCRIBIR');
INSERT INTO permiso (id, modulo, accion) VALUES (3, 'RESERVAS', 'LEER');
INSERT INTO permiso (id, modulo, accion) VALUES (4, 'RESERVAS', 'ESCRIBIR');

INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 1);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 2);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 3);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 4);

INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 1);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 3);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 4);


INSERT INTO usuario (id_usuario, nombre, correo_electronico, password_hash, estado, fecha_creacion, rol_id) 
VALUES (1, 'Admin OnlyFields', 'admin@onlyfields.cl', '$2a$10$EjemploDeHashBCryptSimuladoParaAdmin123', 'ACTIVO', CURRENT_TIMESTAMP, 1);

INSERT INTO usuario (id_usuario, nombre, correo_electronico, password_hash, estado, fecha_creacion, rol_id) 
VALUES (2, 'Sebastian Silva', 'cliente@onlyfields.cl', '1273h1t4981vi713b136u84th81giju3tfu7', 'ACTIVO', CURRENT_TIMESTAMP, 3);

INSERT INTO usuario (id_usuario, nombre, correo_electronico, password_hash, estado, fecha_creacion, rol_id) 
VALUES (3, 'El pepe sech', 'ete sech@onlyfields.cl', '1273h1t4981vi713b136u84th81giju3tfu7', 'ACTIVO', CURRENT_TIMESTAMP, 2);
COMMIT;