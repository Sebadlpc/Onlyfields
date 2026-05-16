FROM eclipse-temurin:17-jdk AS builder

RUN apt-get update && apt-get install -y maven
WORKDIR /build

COPY . .
RUN mvn clean -B

RUN mvn -B -pl suscripciones -am package -DskipTests

FROM eclipse-temurin:17-jre AS runner

COPY --from=builder /build/suscripciones/target/ms-suscripciones-*.jar /app-suscripciones.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app-suscripciones.jar"]