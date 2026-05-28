CREATE TABLE plan (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      nombre VARCHAR(100) NOT NULL,
                      duracion_dias INT NOT NULL,
                      precio DECIMAL(10,2) NOT NULL,
                      beneficios TEXT NOT NULL
);

CREATE TABLE suscripcion (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             cliente_id BIGINT NOT NULL,
                             plan_id BIGINT NOT NULL,
                             fecha_inicio DATE NOT NULL,
                             fecha_fin DATE NOT NULL,
                             estado VARCHAR(30) NOT NULL,
                             dias_congelados INT DEFAULT 0 NOT NULL,
                             CONSTRAINT fk_suscripcion_plan FOREIGN KEY (plan_id) REFERENCES plan (id)
);

CREATE TABLE historial_estado (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  suscripcion_id BIGINT NOT NULL,
                                  estado_anterior VARCHAR(30),
                                  estado_nuevo VARCHAR(30) NOT NULL,
                                  fecha_cambio DATETIME NOT NULL,
                                  motivo VARCHAR(255),
                                  CONSTRAINT fk_historial_suscripcion FOREIGN KEY (suscripcion_id) REFERENCES suscripcion (id)
);

INSERT INTO plan (nombre, duracion_dias, precio, beneficios) VALUES
                                                                 ('Plan Mensual Bronce', 30, 24990.00, 'Acceso ilimitado a canchas de futbolito de Lunes a Viernes de 09:00 a 14:00 hrs.'),
                                                                 ('Plan Trimestral Plata', 90, 64990.00, 'Acceso ilimitado a canchas + 10% de descuento en el POS del complejo.'),
                                                                 ('Plan Anual Oro', 365, 199990.00, 'Acceso total 24/7, reserva prioritaria y 20% de descuento general en servicios.');