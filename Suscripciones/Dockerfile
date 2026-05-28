# Etapa 1: Construcción Nativa (basado en la configuración de 'usuarios')
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

# Copiamos los archivos necesarios para la compilación del módulo
COPY pom.xml .
COPY Suscripciones/pom.xml ./Suscripciones/
COPY Suscripciones/mvnw ./Suscripciones/
COPY Suscripciones/.mvn ./Suscripciones/.mvn
COPY Suscripciones/src ./Suscripciones/src

# Nos movemos al directorio del módulo
WORKDIR /build/Suscripciones

# Damos permisos de ejecución al Maven Wrapper y compilamos
RUN chmod +x ./mvnw
RUN ./mvnw clean native:compile -Pnative -DskipTests

# Etapa 2: Ejecución (basado en la configuración de 'usuarios')
FROM gcr.io/distroless/base-debian12
WORKDIR /app

# Copiamos las dependencias de librerías nativas necesarias
COPY --from=builder /usr/lib64/libz.so.1 /lib/

# Copiamos el ejecutable nativo compilado
COPY --from=builder /build/Suscripciones/target/ms-Suscripciones /app/ms-Suscripciones

# Exponemos el puerto correcto para suscripciones
EXPOSE 8084
ENTRYPOINT ["/app/ms-Suscripciones"]
