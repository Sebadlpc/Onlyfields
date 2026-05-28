CREATE TABLE caja (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      usuario_id BIGINT NOT NULL,
                      fecha_apertura TIMESTAMP NOT NULL,
                      fecha_cierre TIMESTAMP,
                      monto_inicial DECIMAL(19,2) NOT NULL,
                      total_efectivo DECIMAL(19,2) DEFAULT 0.00,
                      total_tarjeta DECIMAL(19,2) DEFAULT 0.00,
                      estado VARCHAR(20) NOT NULL
);

CREATE TABLE transaccion (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             caja_id BIGINT NOT NULL,
                             cliente_id BIGINT,
                             tipo VARCHAR(30) NOT NULL,
                             total DECIMAL(19,2) NOT NULL,
                             metodo_pago VARCHAR(20) NOT NULL,
                             estado VARCHAR(20) NOT NULL,
                             fecha_hora TIMESTAMP NOT NULL,
                             CONSTRAINT fk_transaccion_caja FOREIGN KEY (caja_id) REFERENCES caja(id)
);

CREATE TABLE item_transaccion (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  transaccion_id BIGINT NOT NULL,
                                  producto_id BIGINT,
                                  descripcion VARCHAR(255),
                                  cantidad INT NOT NULL,
                                  precio_unitario DECIMAL(19,2) NOT NULL,
                                  CONSTRAINT fk_item_transaccion FOREIGN KEY (transaccion_id) REFERENCES transaccion(id) ON DELETE CASCADE
);