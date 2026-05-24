-- Creación de la tabla (Versión MySQL)
CREATE TABLE notificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destinatario_id BIGINT NOT NULL,
    destinatario_email VARCHAR(255) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    canal VARCHAR(50) NOT NULL,
    asunto VARCHAR(200) NOT NULL,
    cuerpo TEXT NOT NULL,       
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    fecha_envio DATETIME,
    intentos INT DEFAULT 0,
    idempotency_key VARCHAR(255) NOT NULL,
    
    CONSTRAINT uk_idempotency UNIQUE (idempotency_key),
    CONSTRAINT chk_canal CHECK (canal IN ('EMAIL', 'SMS', 'PUSH'))
);

-- 1. Un correo de bienvenida que está pendiente de envío
INSERT INTO notificacion (
    destinatario_id, destinatario_email, tipo, canal, asunto, cuerpo, estado, intentos, idempotency_key
) VALUES (
    101, 
    'juan.perez@example.com', 
    'BIENVENIDA', 
    'EMAIL', 
    '¡Bienvenido a Onlyfields!', 
    'Hola Juan, gracias por registrarte. Ya puedes empezar a reservar tus canchas.', 
    'PENDIENTE', 
    0, 
    'evt-welcome-usr-101'
);

-- 2. Una notificación push de recordatorio que ya fue enviada
INSERT INTO notificacion (
    destinatario_id, destinatario_email, tipo, canal, asunto, cuerpo, estado, fecha_envio, intentos, idempotency_key
) VALUES (
    205, 
    'maria.gomez@example.com', 
    'RECORDATORIO_RESERVA', 
    'PUSH', 
    '¡Tu partido está por empezar!', 
    'Recuerda que tienes una reserva en la Cancha 3 a las 18:00.', 
    'ENVIADO', 
    CURRENT_TIMESTAMP, 
    1, 
    'evt-remind-res-8842'
);

-- 3. Un SMS de alerta de pago que falló después de varios intentos
INSERT INTO notificacion (
    destinatario_id, destinatario_email, tipo, canal, asunto, cuerpo, estado, fecha_envio, intentos, idempotency_key
) VALUES (
    310, 
    'carlos.ruiz@example.com', 
    'ALERTA_PAGO', 
    'SMS', 
    'Problema con tu pago', 
    'No pudimos procesar el pago de tu última reserva. Por favor revisa tu tarjeta.', 
    'FALLIDO', 
    '2026-05-07 10:15:00', 
    3, 
    'evt-payfail-res-8845'
);