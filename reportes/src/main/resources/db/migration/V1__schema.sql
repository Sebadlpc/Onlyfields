-- V1__crear_esquema_inicial.sql (Para el MS de Reportes - Versión MySQL)

CREATE TABLE reporte_generado (
    id BIGINT AUTO_INCREMENT,
    usuario_id BIGINT,
    tipo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(255),
    fecha_generacion DATETIME,
    parametros JSON,
    CONSTRAINT pk_reporte_generado PRIMARY KEY (id)
);

INSERT INTO reporte_generado (id, usuario_id, tipo, ruta_archivo, fecha_generacion, parametros) 
VALUES (1, 105, 'REPORTE_RESERVAS_MENSUAL', '/storage/reportes/reservas_mayo_2026.pdf', '2026-05-01 08:30:00', '{"mes": 5, "anio": 2026, "formato": "PDF"}');

INSERT INTO reporte_generado (id, usuario_id, tipo, ruta_archivo, fecha_generacion, parametros) 
VALUES (2, 210, 'REPORTE_INGRESOS_SEMANAL', '/storage/reportes/ingresos_sem1_mayo.xlsx', '2026-05-07 18:45:00', '{"fecha_inicio": "2026-05-01", "fecha_fin": "2026-05-07", "incluir_impuestos": true}');

INSERT INTO reporte_generado (id, usuario_id, tipo, ruta_archivo, fecha_generacion, parametros) 
VALUES (3, 105, 'REPORTE_OCUPACION_CANCHAS', '/storage/reportes/ocupacion_padel.csv', '2026-05-07 19:00:00', '{"deporte": "Padel", "agrupar_por": "dia"}');