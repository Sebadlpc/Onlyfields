CREATE TABLE qr_tokens (
                           id NUMBER(19,0) GENERATED AS IDENTITY,
                           cliente_id NUMBER(19,0) NOT NULL,
                           token VARCHAR2(500) NOT NULL,
                           fecha_expiracion TIMESTAMP NOT NULL,
                           usado NUMBER(1,0) DEFAULT 0 NOT NULL,
                           CONSTRAINT pk_qr_tokens PRIMARY KEY (id),
                           CONSTRAINT uk_qr_tokens_token UNIQUE (token)
);


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
