
SET @password_hash = '$2a$10$3z0.G.VI.3.C9.pU3/yS..TfA8bA.B.eY4G5u/x5iZU1kL.a.a.a';

INSERT INTO rol (nombre) VALUES ('ADMIN'), ('STAFF'), ('CLIENTE')
ON DUPLICATE KEY UPDATE nombre=nombre;

SET @admin_id = (SELECT id FROM rol WHERE nombre = 'ADMIN');
SET @staff_id = (SELECT id FROM rol WHERE nombre = 'STAFF');
SET @cliente_id = (SELECT id FROM rol WHERE nombre = 'CLIENTE');

INSERT INTO usuario (nombre, email, password_hash, estado, fecha_creacion) VALUES
('Admin User', 'admin@onlyfields.com', @password_hash, 'ACTIVO', NOW()),
('Staff User', 'staff@onlyfields.com', @password_hash, 'ACTIVO', NOW()),
('Ana García', 'ana.garcia@email.com', @password_hash, 'ACTIVO', NOW()),
('Carlos Rodriguez', 'carlos.r@email.com', @password_hash, 'ACTIVO', NOW()),
('Elena Fernández', 'elena.f@email.com', @password_hash, 'INACTIVO', NOW());

SET @admin_user_id = (SELECT id FROM usuario WHERE email = 'admin@onlyfields.com');
SET @staff_user_id = (SELECT id FROM usuario WHERE email = 'staff@onlyfields.com');
SET @ana_user_id = (SELECT id FROM usuario WHERE email = 'ana.garcia@email.com');
SET @carlos_user_id = (SELECT id FROM usuario WHERE email = 'carlos.r@email.com');
SET @elena_user_id = (SELECT id FROM usuario WHERE email = 'elena.f@email.com');

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(@admin_user_id, @admin_id),
(@staff_user_id, @staff_id),
(@ana_user_id, @cliente_id),
(@carlos_user_id, @cliente_id),
(@elena_user_id, @cliente_id);
