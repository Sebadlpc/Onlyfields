# Etapa 1: Compilación Nativa (AOT)
FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

COPY pom.xml .
COPY pos ./pos
WORKDIR /build/pos

RUN chmod +x ./mvnw
RUN ./mvnw clean native:compile -Pnative -DskipTests


FROM gcr.io/distroless/base-debian12
WORKDIR /app


COPY --from=builder /usr/lib64/libz.so.1 /lib/


COPY --from=builder /build/pos/target/ms-pos /app/ms-pos

EXPOSE 8080
ENTRYPOINT ["/app/ms-pos"]