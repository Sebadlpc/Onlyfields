-- V1__crear_esquema_inicial.sql (Para el MS de Reportes)

CREATE TABLE reporte_generado (

    id NUMBER(19,0)
        GENERATED ALWAYS AS IDENTITY,

    usuario_id NUMBER(19,0),

    tipo VARCHAR2(255) NOT NULL,

    ruta_archivo VARCHAR2(255),

    fecha_generacion TIMESTAMP(6),

    parametros CLOB,

    CONSTRAINT pk_reporte_generado PRIMARY KEY (id)

);

INSERT INTO reporte_generado (id, usuario_id, tipo, ruta_archivo, fecha_generacion, parametros) 
VALUES (1, 105, 'REPORTE_RESERVAS_MENSUAL', '/storage/reportes/reservas_mayo_2026.pdf', TO_TIMESTAMP('2026-05-01 08:30:00', 'YYYY-MM-DD HH24:MI:SS'), '{"mes": 5, "anio": 2026, "formato": "PDF"}');

INSERT INTO reporte_generado (id, usuario_id, tipo, ruta_archivo, fecha_generacion, parametros) 
VALUES (2, 210, 'REPORTE_INGRESOS_SEMANAL', '/storage/reportes/ingresos_sem1_mayo.xlsx', TO_TIMESTAMP('2026-05-07 18:45:00', 'YYYY-MM-DD HH24:MI:SS'), '{"fecha_inicio": "2026-05-01", "fecha_fin": "2026-05-07", "incluir_impuestos": true}');

INSERT INTO reporte_generado (id, usuario_id, tipo, ruta_archivo, fecha_generacion, parametros) 
VALUES (3, 105, 'REPORTE_OCUPACION_CANCHAS', '/storage/reportes/ocupacion_padel.csv', TO_TIMESTAMP('2026-05-07 19:00:00', 'YYYY-MM-DD HH24:MI:SS'), '{"deporte": "Padel", "agrupar_por": "dia"}');

COMMIT;
