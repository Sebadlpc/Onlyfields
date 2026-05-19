# Etapa 1: Compilación Nativa (AOT)
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

COPY pom.xml .
COPY usuarios ./usuarios
WORKDIR /build/usuarios

RUN chmod +x ./mvnw
RUN ./mvnw native:compile -Pnative -DskipTests

# Etapa 2: Ejecución Ultra Ligera (Distroless)
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos la librería 'zlib'
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el binario compilado
COPY --from=builder /build/usuarios/target/ms-usuarios /app/ms-usuarios

EXPOSE 8080
ENTRYPOINT ["/app/ms-usuarios"]