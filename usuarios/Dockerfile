FROM eclipse-temurin:17-jdk AS builder

RUN apt-get update && apt-get install -y maven
WORKDIR /build

COPY . .

RUN mvn clean -B

RUN mvn -B -pl usuarios -am package -DskipTests

FROM eclipse-temurin:17-jre AS runner

COPY --from=builder /build/usuarios/target/ms-usuarios-1.0.0.jar /app-usuarios.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app-usuarios.jar"]
