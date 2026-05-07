-- V1__crear_esquema_inicial.sql (Para el MS de Reportes)


CREATE TABLE reporte_generado (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    usuario_id NUMBER(19,0),
    
    
    tipo VARCHAR2(255) NOT NULL, 
    
    -- Para la ruta del archivo
    ruta_archivo VARCHAR2(255),
    
    -- LocalDateTime se mapea como TIMESTAMP
    fecha_generacion TIMESTAMP(6),
    
    -- IMPORTANTE: Aquí NO usamos TEXT. 
    -- Usamos CLOB para textos muy largos (JSON, parámetros, etc.)
    parametros CLOB, 
    
    CONSTRAINT pk_reporte_generado PRIMARY KEY (id)
);