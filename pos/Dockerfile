# Etapa 1: Construcción Nativa (basado en la configuración estándar)
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

# Copiamos los archivos necesarios para la compilación del módulo
COPY pom.xml .
COPY pos/pom.xml ./pos/
COPY pos/src ./pos/src

# Nos movemos al directorio del módulo
WORKDIR /build/pos

# Compilamos usando el mvn global de la imagen base
RUN mvn clean native:compile -Pnative -DskipTests

# Etapa 2: Ejecución (basado en la configuración estándar)
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos las dependencias de librerías nativas necesarias
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el ejecutable nativo compilado
COPY --from=builder /build/pos/target/ms-pos /app/ms-pos

# Exponemos el puerto correcto para pos
EXPOSE 8083
ENTRYPOINT ["/app/ms-pos"]
