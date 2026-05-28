CREATE TABLE configuracion_global (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      clave VARCHAR(100) NOT NULL UNIQUE,
                                      valor VARCHAR(255) NOT NULL,
                                      descripcion VARCHAR(255),
                                      fecha_modificacion TIMESTAMP NOT NULL,
                                      usuario_id BIGINT NOT NULL
);

CREATE TABLE feriados_bloqueos (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   fecha DATE NOT NULL UNIQUE,
                                   motivo VARCHAR(200) NOT NULL,
                                   afecta_reservas BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO configuracion_global (clave, valor, descripcion, fecha_modificacion, usuario_id)
VALUES ('HORARIO_APERTURA', '08:00', 'Horario de apertura de las canchas', NOW(), 1);

INSERT INTO configuracion_global (clave, valor, descripcion, fecha_modificacion, usuario_id)
VALUES ('HORARIO_CIERRE', '23:00', 'Horario de cierre de las canchas', NOW(), 1);

INSERT INTO configuracion_global (clave, valor, descripcion, fecha_modificacion, usuario_id)
VALUES ('PRECIO_BASE_CANCHA', '15000', 'Tarifa base por hora en pesos', NOW(), 1);