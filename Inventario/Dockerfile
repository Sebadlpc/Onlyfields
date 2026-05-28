# Etapa 1: Construcción Nativa (basado en la configuración de 'usuarios')
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

# Copiamos los archivos necesarios para la compilación del módulo
COPY pom.xml .
COPY Inventario/pom.xml ./Inventario/
COPY Inventario/mvnw ./Inventario/
COPY Inventario/.mvn ./Inventario/.mvn
COPY Inventario/src ./Inventario/src

# Nos movemos al directorio del módulo
WORKDIR /build/Inventario

# Damos permisos de ejecución al Maven Wrapper y compilamos
RUN chmod +x ./mvnw
RUN ./mvnw clean native:compile -Pnative -DskipTests

# Etapa 2: Ejecución (basado en la configuración de 'usuarios')
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos las dependencias de librerías nativas necesarias
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el ejecutable nativo compilado
COPY --from=builder /build/Inventario/target/ms-inventario /app/ms-inventario

# Exponemos el puerto correcto para inventario
EXPOSE 8085
ENTRYPOINT ["/app/ms-inventario"]
