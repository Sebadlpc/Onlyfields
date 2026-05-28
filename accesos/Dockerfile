# Etapa 1: Construcción Nativa (basado en la configuración estándar)
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

# Copiamos los archivos necesarios para la compilación del módulo
COPY pom.xml .
COPY accesos/pom.xml ./accesos/
COPY accesos/mvnw ./accesos/
COPY accesos/.mvn ./accesos/.mvn
COPY accesos/src ./accesos/src

# Nos movemos al directorio del módulo
WORKDIR /build/accesos

# Damos permisos de ejecución al Maven Wrapper y compilamos
RUN chmod +x ./mvnw
RUN ./mvnw clean native:compile -Pnative -DskipTests

# Etapa 2: Ejecución (basado en la configuración estándar)
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos las dependencias de librerías nativas necesarias
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el ejecutable nativo compilado
COPY --from=builder /build/accesos/target/ms-accesos /app/ms-accesos

# Exponemos el puerto correcto para accesos
EXPOSE 8082
ENTRYPOINT ["/app/ms-accesos"]
