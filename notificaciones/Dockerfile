# Etapa 1: Construcción Nativa (basado en la configuración estándar)
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

# Copiamos los archivos necesarios para la compilación del módulo
COPY pom.xml .
COPY notificaciones/pom.xml ./notificaciones/
COPY notificaciones/src ./notificaciones/src

# Nos movemos al directorio del módulo
WORKDIR /build/notificaciones

# Compilamos usando el mvn global de la imagen base
RUN mvn clean native:compile -Pnative -DskipTests

# Etapa 2: Ejecución (basado en la configuración estándar)
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos las dependencias de librerías nativas necesarias
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el ejecutable nativo compilado
COPY --from=builder /build/notificaciones/target/ms-notificaciones /app/ms-notificaciones

# Exponemos el puerto correcto para notificaciones
EXPOSE 8090
ENTRYPOINT ["/app/ms-notificaciones"]
