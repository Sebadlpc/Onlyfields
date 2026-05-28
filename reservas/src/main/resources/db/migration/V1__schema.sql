-- V1__schema_reservas.sql

CREATE TABLE cancha (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    deporte VARCHAR(50) NOT NULL,
    capacidad INT NOT NULL,
    tarifa_hora DECIMAL(12,2) NOT NULL,
    estado VARCHAR(20),
    PRIMARY KEY (id)
);

CREATE TABLE reserva (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cancha_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL,
    total_cobrado DECIMAL(12,2),
    PRIMARY KEY (id),
    CONSTRAINT fk_reserva_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);

CREATE TABLE bloque_horario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cancha_id BIGINT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    motivo VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_bloque_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);