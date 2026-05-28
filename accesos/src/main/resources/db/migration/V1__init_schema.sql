CREATE TABLE qr_tokens (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           cliente_id BIGINT NOT NULL,
                           token VARCHAR(500) NOT NULL UNIQUE,
                           fecha_expiracion TIMESTAMP NOT NULL,
                           usado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE registro_acceso (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 cliente_id BIGINT NOT NULL,
                                 tipo VARCHAR(20) NOT NULL,
                                 resultado VARCHAR(20) NOT NULL,
                                 motivo_rechazo VARCHAR(255),
                                 fecha_hora TIMESTAMP NOT NULL
);