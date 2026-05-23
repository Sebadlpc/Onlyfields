CREATE TABLE cancha (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    deporte VARCHAR(50) NOT NULL,
    capacidad INT NOT NULL,
    tarifa_hora DECIMAL(12,2) NOT NULL,
    estado VARCHAR(20)
);

CREATE TABLE reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cancha_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL,
    total_cobrado DECIMAL(12,2),
    CONSTRAINT fk_reserva_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);

CREATE TABLE bloque_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cancha_id BIGINT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    motivo VARCHAR(255),
    CONSTRAINT fk_bloque_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);