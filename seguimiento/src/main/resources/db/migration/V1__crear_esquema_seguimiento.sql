-- 1. Tabla de Fichas de Clientes (Maestro)
CREATE TABLE fichas_clientes (
                                 id NUMBER(19,0) GENERATED AS IDENTITY,
                                 cliente_id NUMBER(19,0) NOT NULL,
                                 lesiones CLOB,
                                 observaciones CLOB,
                                 fecha_ingreso DATE,
                                 CONSTRAINT pk_fichas_clientes PRIMARY KEY (id),
                                 CONSTRAINT uk_fichas_cliente_id UNIQUE (cliente_id)
);
-- Ficha 1: Usuario con historial de lesiones
INSERT INTO fichas_clientes (cliente_id, lesiones, observaciones, fecha_ingreso)
VALUES (101, 'Esguince de tobillo grado 2 en 2024.', 'Cliente busca fortalecer tren inferior.', CURRENT_DATE);

-- Ficha 2: Usuario en rehabilitación
INSERT INTO fichas_clientes (cliente_id, lesiones, observaciones, fecha_ingreso)
VALUES (102, 'Hernia discal L5-S1.', 'Entrenamiento enfocado en movilidad y core.', CURRENT_DATE - 30);

-- Ficha 3: Usuario deportista de alto rendimiento
INSERT INTO fichas_clientes (cliente_id, lesiones, observaciones, fecha_ingreso)
VALUES (103, 'Sin lesiones previas.', 'Preparación para maratón.', CURRENT_DATE - 15);

-- 2. Tabla de Mediciones Corporales (Detalle)
CREATE TABLE mediciones_corporales (
                                       id NUMBER(19,0) GENERATED AS IDENTITY,
                                       ficha_id NUMBER(19,0) NOT NULL,
                                       fecha DATE,
                                       peso FLOAT(53) NOT NULL,
                                       altura FLOAT(53) NOT NULL,
                                       imc FLOAT(53),
                                       cintura FLOAT(53),
                                       cadera FLOAT(53),
                                       objetivo VARCHAR2(30),

                                       CONSTRAINT pk_mediciones_corp PRIMARY KEY (id),
                                       CONSTRAINT fk_medicion_ficha FOREIGN KEY (ficha_id)
                                           REFERENCES fichas_clientes(id) ON DELETE CASCADE,
    -- Validación de Enum ObjetivoFisico
                                       CONSTRAINT chk_objetivo_fisico
                                           CHECK (objetivo IN ('PERDIDA_PESO', 'GANANCIA_MUSCULAR', 'MANTENIMIENTO', 'REHABILITACION'))
);
-- Mediciones para el Cliente 101 (Ficha ID 1)
INSERT INTO mediciones_corporales (ficha_id, fecha, peso, altura, imc, cintura, cadera, objetivo)
VALUES (1, CURRENT_DATE - 10, 85.5, 1.80, 26.39, 90.0, 100.0, 'GANANCIA_MUSCULAR');

-- Segunda medición para el Cliente 101 (Para mostrar progreso)
INSERT INTO mediciones_corporales (ficha_id, fecha, peso, altura, imc, cintura, cadera, objetivo)
VALUES (1, CURRENT_DATE, 84.8, 1.80, 26.17, 88.5, 99.5, 'GANANCIA_MUSCULAR');

-- Mediciones para el Cliente 102 (Ficha ID 2)
INSERT INTO mediciones_corporales (ficha_id, fecha, peso, altura, imc, cintura, cadera, objetivo)
VALUES (2, CURRENT_DATE, 70.0, 1.70, 24.22, 75.0, 85.0, 'REHABILITACION');