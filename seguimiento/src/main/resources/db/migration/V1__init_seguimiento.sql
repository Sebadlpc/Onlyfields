

CREATE TABLE fichas_clientes (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 cliente_id BIGINT NOT NULL UNIQUE,
                                 lesiones TEXT,
                                 observaciones TEXT,
                                 fecha_ingreso DATE NOT NULL
);

CREATE TABLE mediciones_corporales (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       ficha_id BIGINT NOT NULL,
                                       fecha DATE NOT NULL,
                                       peso DOUBLE NOT NULL,
                                       altura DOUBLE NOT NULL,
                                       imc DOUBLE,
                                       cintura DOUBLE,
                                       cadera DOUBLE,
                                       objetivo VARCHAR(30),
                                       CONSTRAINT fk_mediciones_ficha FOREIGN KEY (ficha_id) REFERENCES fichas_clientes (id) ON DELETE CASCADE
);