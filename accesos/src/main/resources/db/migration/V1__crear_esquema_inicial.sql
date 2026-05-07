-- 1. Tabla de Tokens QR
CREATE TABLE qr_tokens (
                           id NUMBER(19,0) GENERATED AS IDENTITY,
                           cliente_id NUMBER(19,0) NOT NULL,
                           token VARCHAR2(500) NOT NULL,
                           fecha_expiracion TIMESTAMP NOT NULL,
                           usado NUMBER(1,0) DEFAULT 0 NOT NULL,
                           CONSTRAINT pk_qr_tokens PRIMARY KEY (id),
                           CONSTRAINT uk_qr_tokens_token UNIQUE (token)
);
-- Token 1: Un acceso activo para hoy
INSERT INTO qr_tokens (cliente_id, token, fecha_expiracion, usado)
VALUES (101, 'TOKEN-ACTIVO-VERANO-2026', CURRENT_TIMESTAMP + 1, 0);

-- Token 2: Un token que ya expiró y fue usado
INSERT INTO qr_tokens (cliente_id, token, fecha_expiracion, usado)
VALUES (102, 'TOKEN-EXPIRADO-MARZO', CURRENT_TIMESTAMP - 30, 1);

-- Token 3: Token temporal para un pase diario
INSERT INTO qr_tokens (cliente_id, token, fecha_expiracion, usado)
VALUES (103, 'PASE-DIARIO-ONLYFIELDS-XYZ', CURRENT_TIMESTAMP + 0.5, 0);


-- 2. Tabla de Registro de Accesos
CREATE TABLE registro_acceso (
                                 id NUMBER(19,0) GENERATED AS IDENTITY,
                                 cliente_id NUMBER(19,0) NOT NULL,
                                 tipo VARCHAR2(20) NOT NULL,
                                 resultado VARCHAR2(20) NOT NULL,
                                 fecha_hora TIMESTAMP NOT NULL,
                                 motivo_rechazo VARCHAR2(255),
                                 CONSTRAINT pk_registro_acceso PRIMARY KEY (id),
                                 CONSTRAINT chk_tipo_acceso CHECK (tipo IN ('ENTRADA', 'SALIDA')),
                                 CONSTRAINT chk_resultado_acceso CHECK (resultado IN ('PERMITIDO', 'DENEGADO'))
);

-- Registro 1: Entrada permitida normal
INSERT INTO registro_acceso (cliente_id, tipo, resultado, fecha_hora, motivo_rechazo)
VALUES (101, 'ENTRADA', 'PERMITIDO', CURRENT_TIMESTAMP - 2/24, NULL);

-- Registro 2: Entrada denegada (ejemplo: no pagó la mensualidad)
INSERT INTO registro_acceso (cliente_id, tipo, resultado, fecha_hora, motivo_rechazo)
VALUES (205, 'ENTRADA', 'DENEGADO', CURRENT_TIMESTAMP - 1/24, 'Membresía inactiva o deuda en MS-POS');

-- Registro 3: Salida registrada exitosamente
INSERT INTO registro_acceso (cliente_id, tipo, resultado, fecha_hora, motivo_rechazo)
VALUES (101, 'SALIDA', 'PERMITIDO', CURRENT_TIMESTAMP, NULL);
