
CREATE TABLE cajas (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       usuario_id BIGINT NOT NULL,
                       fecha_apertura TIMESTAMP,
                       fecha_cierre TIMESTAMP,
                       monto_inicial DOUBLE NOT NULL,
                       total_efectivo FLOAT DEFAULT 0.0,
                       total_tarjeta FLOAT DEFAULT 0.0,
                       estado VARCHAR(20) NOT NULL
);

CREATE TABLE transacciones (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               caja_id BIGINT NOT NULL,
                               cliente_id BIGINT NOT NULL,
                               tipo VARCHAR(30) NOT NULL,
                               total DOUBLE NOT NULL,
                               metodo_pago VARCHAR(255) NOT NULL,
                               estado VARCHAR(30) NOT NULL,
                               fecha_hora TIMESTAMP
);

CREATE TABLE items_transaccion (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   transaccion_id BIGINT NOT NULL,
                                   producto_id BIGINT,
                                   descripcion VARCHAR(255) NOT NULL,
                                   cantidad INT NOT NULL,
                                   precio_unitario DOUBLE NOT NULL,
                                   CONSTRAINT fk_items_transaccion FOREIGN KEY (transaccion_id) REFERENCES transacciones (id) ON DELETE CASCADE
);