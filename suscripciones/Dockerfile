FROM eclipse-temurin:17-jdk AS builder

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
WORKDIR /build

COPY . .
RUN cd suscripciones && mvn clean package -B -DskipTests

FROM eclipse-temurin:17-jre AS runner

COPY --from=builder /build/suscripciones/target/ms-suscripciones-*.jar /app-suscripciones.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app-suscripciones.jar"]
