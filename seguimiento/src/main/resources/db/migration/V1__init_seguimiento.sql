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