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

-- 3. Tabla de Items de Transacción
CREATE TABLE items_transaccion (
                                   id NUMBER(19,0) GENERATED AS IDENTITY,
                                   transaccion_id NUMBER(19,0) NOT NULL, -- <--- Corregido con la 'r'
                                   producto_id NUMBER(19,0),
                                   descripcion VARCHAR2(255) NOT NULL,
                                   cantidad NUMBER(10,0) NOT NULL,
                                   precio_unitario FLOAT(53) NOT NULL,
                                   CONSTRAINT pk_items_trans PRIMARY KEY (id),
                                   CONSTRAINT fk_item_transaccion FOREIGN KEY (transaccion_id)
                                       REFERENCES transacciones(id) ON DELETE CASCADE
);