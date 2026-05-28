CREATE TABLE categorias (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE productos (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(150) NOT NULL,
                           categoria_id BIGINT NOT NULL,
                           precio_venta DECIMAL(10,2) NOT NULL,
                           stock_actual INT NOT NULL,
                           stock_minimo INT NOT NULL,
                           codigo_barras VARCHAR(50) UNIQUE,
                           CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE movimientos_stock (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   producto_id BIGINT NOT NULL,
                                   tipo VARCHAR(20) NOT NULL, -- 'ENTRADA' o 'SALIDA'
                                   cantidad INT NOT NULL,
                                   fecha_hora TIMESTAMP NOT NULL,
                                   referencia VARCHAR(255) NOT NULL,
                                   CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

INSERT INTO categorias (nombre) VALUES ('Bebidas Deportivas');
INSERT INTO categorias (nombre) VALUES ('Snacks Saludables');
INSERT INTO categorias (nombre) VALUES ('Equipamiento (Arriendo)');
INSERT INTO categorias (nombre) VALUES ('Ropa y Accesorios');