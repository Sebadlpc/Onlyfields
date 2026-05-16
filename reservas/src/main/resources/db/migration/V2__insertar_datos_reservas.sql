INSERT INTO cancha (id, nombre, deporte, capacidad, tarifa_hora, estado) 
VALUES (1, 'Cancha Central', 'Tenis', 100, 25000.00, 'Disponible');

INSERT INTO cancha (id, nombre, deporte, capacidad, tarifa_hora, estado) 
VALUES (2, 'Cancha Norte', 'Futbolito', 14, 30000.50, 'Mantenimiento');

INSERT INTO cancha (id, nombre, deporte, capacidad, tarifa_hora, estado) 
VALUES (3, 'Pista Cristal', 'Padel', 4, 15000.00, 'Disponible');

INSERT INTO reserva (id, cancha_id, cliente_id, fecha_inicio, fecha_fin, estado, total_cobrado) 
VALUES (1, 1, 101, TO_TIMESTAMP('2026-05-15 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-05-15 11:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'Confirmada', 37500.00);

INSERT INTO reserva (id, cancha_id, cliente_id, fecha_inicio, fecha_fin, estado, total_cobrado) 
VALUES (2, 3, 205, TO_TIMESTAMP('2026-05-16 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-05-16 19:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Pendiente', 15000.00);

INSERT INTO reserva (id, cancha_id, cliente_id, fecha_inicio, fecha_fin, estado, total_cobrado) 
VALUES (3, 1, 310, TO_TIMESTAMP('2026-05-20 20:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-05-20 22:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Cancelada', 0.00);

INSERT INTO bloque_horario (id, cancha_id, fecha_inicio, fecha_fin, motivo) 
VALUES (1, 2, TO_TIMESTAMP('2026-05-10 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-05-12 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Cambio de césped sintético');

INSERT INTO bloque_horario (id, cancha_id, fecha_inicio, fecha_fin, motivo) 
VALUES (2, 1, TO_TIMESTAMP('2026-06-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-06-05 21:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Torneo Regional de Tenis');

INSERT INTO bloque_horario (id, cancha_id, fecha_inicio, fecha_fin, motivo) 
VALUES (3, 3, TO_TIMESTAMP('2026-05-22 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-05-22 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Reparación de red e iluminación');
COMMIT;