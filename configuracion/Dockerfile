# Etapa 1: Construcción Nativa (basado en la configuración de 'usuarios')
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

# Copiamos los archivos necesarios para la compilación del módulo
COPY pom.xml .
COPY Configuracion/pom.xml ./Configuracion/
COPY Configuracion/mvnw ./Configuracion/
COPY Configuracion/.mvn ./Configuracion/.mvn
COPY Configuracion/src ./Configuracion/src

# Nos movemos al directorio del módulo
WORKDIR /build/Configuracion

# Damos permisos de ejecución al Maven Wrapper y compilamos
RUN chmod +x ./mvnw
RUN ./mvnw clean native:compile -Pnative -DskipTests

# Etapa 2: Ejecución (basado en la configuración de 'usuarios')
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos las dependencias de librerías nativas necesarias
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el ejecutable nativo compilado
COPY --from=builder /build/Configuracion/target/ms-configuracion /app/ms-configuracion

# Exponemos el puerto correcto para configuracion
EXPOSE 8089
ENTRYPOINT ["/app/ms-configuracion"]
