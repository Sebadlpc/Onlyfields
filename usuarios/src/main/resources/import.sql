INSERT INTO rol (id, nombre, descripcion) 
VALUES (1, 'ADMIN', 'Administrador total del complejo deportivo');

INSERT INTO rol (id, nombre, descripcion) 
VALUES (2, 'CLIENTE', 'Usuario regular (deportista/arrendatario)');

INSERT INTO rol (id, nombre, descripcion) 
VALUES (2, 'STAFF DP', 'Staff DP');

INSERT INTO permiso (id, modulo, accion) VALUES (1, 'USUARIOS', 'LEER');
INSERT INTO permiso (id, modulo, accion) VALUES (2, 'USUARIOS', 'ESCRIBIR');
INSERT INTO permiso (id, modulo, accion) VALUES (3, 'RESERVAS', 'LEER');
INSERT INTO permiso (id, modulo, accion) VALUES (4, 'RESERVAS', 'ESCRIBIR');

INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 1);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 2);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 3);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 4);

INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 1);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 3);
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 4);


INSERT INTO usuario (id_usuario, nombre, correo_electronico, password_hash, estado, fecha_creacion, rol_id) 
VALUES (1, 'Admin OnlyFields', 'admin@onlyfields.cl', '$2a$10$EjemploDeHashBCryptSimuladoParaAdmin123', 'ACTIVO', CURRENT_TIMESTAMP, 1);

INSERT INTO usuario (id_usuario, nombre, correo_electronico, password_hash, estado, fecha_creacion, rol_id) 
VALUES (2, 'Sebastian Silva', 'cliente@onlyfields.cl', '1273h1t4981vi713b136u84th81giju3tfu7', 'ACTIVO', CURRENT_TIMESTAMP, 3);

INSERT INTO usuario (id_usuario, nombre, correo_electronico, password_hash, estado, fecha_creacion, rol_id) 
VALUES (3, 'El pepe sech', 'ete sech@onlyfields.cl', '1273h1t4981vi713b136u84th81giju3tfu7', 'ACTIVO', CURRENT_TIMESTAMP, 2);
COMMIT;