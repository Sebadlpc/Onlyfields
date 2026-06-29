CREATE TABLE ficha_cliente (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               cliente_id BIGINT NOT NULL UNIQUE,
                               antecedentes_medicos TEXT,
                               lesiones_previas TEXT,
                               observaciones TEXT,
                               fecha_creacion TIMESTAMP NOT NULL
);

CREATE TABLE medicion_corporal (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   ficha_cliente_id BIGINT NOT NULL,
                                   fecha_medicion TIMESTAMP NOT NULL,
                                   peso DOUBLE,
                                   altura DOUBLE,
                                   porcentaje_grasa DOUBLE,
                                   masa_muscular DOUBLE,
                                   perimetro_cintura DOUBLE,
                                   perimetro_cadera DOUBLE,
                                   objetivo_actual VARCHAR(30),
                                   CONSTRAINT fk_medicion_ficha FOREIGN KEY (ficha_cliente_id) REFERENCES ficha_cliente(id) ON DELETE CASCADE
);

-- Insertar Fichas de Clientes (Datos de prueba)
INSERT INTO ficha_cliente (cliente_id, antecedentes_medicos, lesiones_previas, observaciones, fecha_creacion)
VALUES
    (101, 'Ninguno', 'Esguince tobillo derecho (2025)', 'Socio con buena movilidad, precaución en saltos', CURRENT_TIMESTAMP),
    (102, 'Hipertensión leve controlada', 'Ninguna', 'Requiere monitoreo de frecuencia cardíaca', CURRENT_TIMESTAMP),
    (103, 'Asma estacional', 'Operación de meniscos (2023)', 'Evitar impacto alto en tren inferior', CURRENT_TIMESTAMP);

-- Insertar Mediciones Corporales (Asociadas a las fichas creadas arriba, IDs 1, 2 y 3)
INSERT INTO medicion_corporal (ficha_cliente_id, fecha_medicion, peso, altura, porcentaje_grasa, masa_muscular, perimetro_cintura, perimetro_cadera, objetivo_actual)
VALUES
    (1, CURRENT_TIMESTAMP, 78.5, 1.75, 14.2, 36.8, 82.0, 94.5, 'GANANCIA_MASA_MUSCULAR'),
    (1, CURRENT_TIMESTAMP, 79.1, 1.75, 13.8, 37.5, 81.5, 95.0, 'MEJORA_RENDIMIENTO'),
    (2, CURRENT_TIMESTAMP, 92.0, 1.68, 28.5, 31.0, 105.0, 112.0, 'PERDIDA_PESO'),
    (3, CURRENT_TIMESTAMP, 65.5, 1.60, 18.0, 26.5, 72.0, 90.0, 'MANTENIMIENTO');