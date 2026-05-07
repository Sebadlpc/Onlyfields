CREATE TABLE cancha (
    id NUMBER(19,0) GENERATED AS IDENTITY PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    deporte VARCHAR2(50) NOT NULL,
    capacidad NUMBER(10) NOT NULL,
    tarifa_hora NUMBER(12,2) NOT NULL,
    estado VARCHAR2(20)
);

CREATE TABLE reserva (
    id NUMBER(19,0) GENERATED AS IDENTITY PRIMARY KEY,
    cancha_id NUMBER(19,0) NOT NULL,
    cliente_id NUMBER(19,0) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    estado VARCHAR2(30) NOT NULL,
    total_cobrado NUMBER(12,2)
);

CREATE TABLE bloque_horario (
    id NUMBER(19,0) GENERATED AS IDENTITY PRIMARY KEY,
    cancha_id NUMBER(19,0) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    motivo VARCHAR2(255)
);