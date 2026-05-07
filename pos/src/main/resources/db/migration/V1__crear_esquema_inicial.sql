-- 1. Tabla de Cajas
CREATE TABLE cajas (
                       id NUMBER(19,0) GENERATED AS IDENTITY,
                       usuario_id NUMBER(19,0) NOT NULL,
                       fecha_apertura TIMESTAMP,
                       fecha_cierre TIMESTAMP,
                       monto_inicial FLOAT(53) NOT NULL,
                       total_efectivo FLOAT(53) DEFAULT 0,
                       total_tarjeta FLOAT(53) DEFAULT 0,
                       estado VARCHAR2(20) NOT NULL,
                       CONSTRAINT pk_cajas PRIMARY KEY (id),
                       CONSTRAINT chk_estado_caja CHECK (estado IN ('ABIERTA', 'CERRADA'))
);
-- Caja 1: Cerrada (Turno mañana)
INSERT INTO cajas (usuario_id, fecha_apertura, fecha_cierre, monto_inicial, total_efectivo, total_tarjeta, estado)
VALUES (1, CURRENT_TIMESTAMP - 5/24, CURRENT_TIMESTAMP - 1/24, 50000, 25000, 45000, 'CERRADA');

-- Caja 2: Abierta (Turno tarde - Seba)
INSERT INTO cajas (usuario_id, fecha_apertura, fecha_cierre, monto_inicial, total_efectivo, total_tarjeta, estado)
VALUES (2, CURRENT_TIMESTAMP, NULL, 30000, 15000, 0, 'ABIERTA');

-- Caja 3: Abierta (Nueva sucursal)
INSERT INTO cajas (usuario_id, fecha_apertura, fecha_cierre, monto_inicial, total_efectivo, total_tarjeta, estado)
VALUES (3, CURRENT_TIMESTAMP, NULL, 100000, 0, 0, 'ABIERTA');
-- 2. Tabla de Transacciones
CREATE TABLE transacciones (
                               id NUMBER(19,0) GENERATED AS IDENTITY,
                               caja_id NUMBER(19,0) NOT NULL,
                               cliente_id NUMBER(19,0) NOT NULL,
                               tipo VARCHAR2(30) NOT NULL,
                               total FLOAT(53) NOT NULL,
                               metodo_pago VARCHAR2(20) NOT NULL,
                               estado VARCHAR2(20) NOT NULL,
                               fecha_hora TIMESTAMP,
                               CONSTRAINT pk_transacciones PRIMARY KEY (id),
                               CONSTRAINT chk_tipo_trans CHECK (tipo IN ('VENTA_PRODUCTO', 'PAGO_RESERVA', 'PAGO_SUSCRIPCION')),
                               CONSTRAINT chk_metodo_pago CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA')),
                               CONSTRAINT chk_estado_trans CHECK (estado IN ('COMPLETADA', 'ANULADA', 'PENDIENTE'))
);
-- Transacción 1: Venta de productos (Referencia a Caja 1)
INSERT INTO transacciones (caja_id, cliente_id, tipo, total, metodo_pago, estado, fecha_hora)
VALUES (1, 101, 'VENTA_PRODUCTO', 15000, 'EFECTIVO', 'COMPLETADA', CURRENT_TIMESTAMP - 4/24);

-- Transacción 2: Pago de suscripción mensual (Referencia a Caja 1)
INSERT INTO transacciones (caja_id, cliente_id, tipo, total, metodo_pago, estado, fecha_hora)
VALUES (1, 102, 'PAGO_SUSCRIPCION', 45000, 'TARJETA', 'COMPLETADA', CURRENT_TIMESTAMP - 3/24);

-- Transacción 3: Reserva de cancha de pádel (Referencia a Caja 2)
INSERT INTO transacciones (caja_id, cliente_id, tipo, total, metodo_pago, estado, fecha_hora)
VALUES (2, 103, 'PAGO_RESERVA', 15000, 'EFECTIVO', 'COMPLETADA', CURRENT_TIMESTAMP - 10/1440);
-- 3. Tabla de Items de Transacción
CREATE TABLE items_transaccion (
                                   id NUMBER(19,0) GENERATED AS IDENTITY,
                                   transaccion_id NUMBER(19,0) NOT NULL,
                                   producto_id NUMBER(19,0),
                                   descripcion VARCHAR2(255) NOT NULL,
                                   cantidad NUMBER(10,0) NOT NULL,
                                   precio_unitario FLOAT(53) NOT NULL,
                                   CONSTRAINT pk_items_trans PRIMARY KEY (id),
                                   CONSTRAINT fk_item_transaccion FOREIGN KEY (transaccion_id)
                                       REFERENCES transacciones(id) ON DELETE CASCADE
);
-- Ítems para la Transacción 1 (Venta de productos)
INSERT INTO items_transaccion (transaccion_id, producto_id, descripcion, cantidad, precio_unitario)
VALUES (1, 50, 'Proteína Whey Isolate 1kg', 1, 12000);

INSERT INTO items_transaccion (transaccion_id, producto_id, descripcion, cantidad, precio_unitario)
VALUES (1, 51, 'Shaker Onlyfields Black', 1, 3000);

-- Ítem para la Transacción 3 (Reserva)
INSERT INTO items_transaccion (transaccion_id, producto_id, descripcion, cantidad, precio_unitario)
VALUES (3, NULL, 'Reserva Cancha 1 - 18:00 hrs', 1, 15000);