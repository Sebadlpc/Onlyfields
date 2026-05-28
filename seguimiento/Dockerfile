# Etapa 1: Construcción Nativa (basado en la configuración estándar)
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

# Copiamos los archivos necesarios para la compilación del módulo
COPY pom.xml .
COPY seguimiento/pom.xml ./seguimiento/
COPY seguimiento/mvnw ./seguimiento/
COPY seguimiento/.mvn ./seguimiento/.mvn
COPY seguimiento/src ./seguimiento/src

# Nos movemos al directorio del módulo
WORKDIR /build/seguimiento

# Damos permisos de ejecución al Maven Wrapper y compilamos
RUN chmod +x ./mvnw
RUN ./mvnw clean native:compile -Pnative -DskipTests

# Etapa 2: Ejecución (basado en la configuración estándar)
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos las dependencias de librerías nativas necesarias
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el ejecutable nativo compilado
COPY --from=builder /build/seguimiento/target/ms-seguimiento /app/ms-seguimiento

# Exponemos el puerto correcto para seguimiento
EXPOSE 8087
ENTRYPOINT ["/app/ms-seguimiento"]
