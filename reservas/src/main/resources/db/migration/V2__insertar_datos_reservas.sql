-- V2__insertar_datos_reservas.sql

-- ── Canchas ───────────────────────────────────────────────────────
INSERT INTO cancha (nombre, deporte, capacidad, tarifa_hora, estado) VALUES
('Cancha Norte',   'FUTBOL',  10, 15000.00, 'DISPONIBLE'),
('Cancha Central', 'TENIS',    2, 12000.00, 'DISPONIBLE'),
('Cancha Sur',     'BASQUET',  8, 10000.00, 'DISPONIBLE');

-- ── Reservas ──────────────────────────────────────────────────────
INSERT INTO reserva (cancha_id, cliente_id, fecha_inicio, fecha_fin, estado, total_cobrado) VALUES
(1, 101, '2026-06-01 09:00:00', '2026-06-01 10:00:00', 'CONFIRMADA', 15000.00),
(2, 102, '2026-06-02 14:00:00', '2026-06-02 15:30:00', 'CONFIRMADA', 18000.00),
(3, 103, '2026-06-03 16:00:00', '2026-06-03 17:00:00', 'CONFIRMADA', 10000.00);

-- ── Bloques horarios ──────────────────────────────────────────────
INSERT INTO bloque_horario (cancha_id, fecha_inicio, fecha_fin, motivo) VALUES
(1, '2026-06-05 08:00:00', '2026-06-05 12:00:00', 'Mantenimiento de cesped'),
(2, '2026-06-06 10:00:00', '2026-06-06 13:00:00', 'Reparacion de red'),
(3, '2026-06-07 07:00:00', '2026-06-07 09:00:00', 'Limpieza general');