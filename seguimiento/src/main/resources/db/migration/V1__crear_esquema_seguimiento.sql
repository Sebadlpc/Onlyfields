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