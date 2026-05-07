CREATE SEQUENCE NOTIFICACION_SEQ START WITH 1 INCREMENT BY 1;

-- Creación de la tabla
CREATE TABLE notificacion (
    id NUMBER PRIMARY KEY,
    destinatario_id NUMBER NOT NULL,
    destinatario_email VARCHAR2(255) NOT NULL,
    tipo VARCHAR2(100) NOT NULL,
    canal VARCHAR2(50) NOT NULL,
    asunto VARCHAR2(200) NOT NULL,
    cuerpo CLOB NOT NULL,       
    estado VARCHAR2(20) DEFAULT 'PENDIENTE',
    fecha_envio TIMESTAMP,
    intentos NUMBER DEFAULT 0,
    idempotency_key VARCHAR2(255) NOT NULL,
    
    
    CONSTRAINT uk_idempotency UNIQUE (idempotency_key),
    CONSTRAINT chk_canal CHECK (canal IN ('EMAIL', 'SMS', 'PUSH'))
);