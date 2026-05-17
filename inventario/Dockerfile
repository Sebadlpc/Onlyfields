FROM eclipse-temurin:17-jdk AS builder

WORKDIR /build

COPY . .

RUN apt-get update && apt-get install -y maven

RUN mvn clean package -DskipTests -pl usuarios -am -B

FROM eclipse-temurin:17-jre AS runner

COPY --from=builder /build/usuarios/target/ms-usuarios-1.0.0.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]