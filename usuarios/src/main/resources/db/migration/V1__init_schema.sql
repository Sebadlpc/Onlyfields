CREATE TABLE rol (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     nombre VARCHAR(50) NOT NULL UNIQUE,
                     descripcion VARCHAR(255)
);

CREATE TABLE usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         email VARCHAR(150) NOT NULL UNIQUE,
                         password_hash VARCHAR(255) NOT NULL,
                         estado VARCHAR(20) DEFAULT 'ACTIVO' NOT NULL,
                         fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE usuario_rol (
                             usuario_id BIGINT NOT NULL,
                             rol_id BIGINT NOT NULL,
                             PRIMARY KEY (usuario_id, rol_id),
                             CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
                             CONSTRAINT fk_ur_rol FOREIGN KEY (rol_id) REFERENCES rol (id)
);

INSERT INTO rol (nombre, descripcion) VALUES ('ADMIN', 'Administrador del sistema');
INSERT INTO rol (nombre, descripcion) VALUES ('CREADOR', 'Creador de contenido');
INSERT INTO rol (nombre, descripcion) VALUES ('CLIENTE', 'Usuario consumidor');