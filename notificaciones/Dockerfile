FROM ghcr.io/graalvm/native-image-community:17 AS builder
WORKDIR /build

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

COPY src ./src

RUN ./mvnw -Pnative native:compile -DskipTests

FROM debian:12-slim
WORKDIR /app

RUN apt-get update && apt-get install -y zlib1g && rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/target/ms-notificaciones /app/ms-notificaciones

EXPOSE 8080
ENTRYPOINT ["/app/ms-notificaciones"]