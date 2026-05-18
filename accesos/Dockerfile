# Etapa 1: Construcción y Compilación con Maven
FROM eclipse-temurin:17-jdk AS builder
RUN apt-get update && apt-get install -y maven
WORKDIR /build
COPY . .
RUN mvn clean -B
# Compila selectivamente el módulo correspondiente omitiendo las pruebas unitarias
RUN mvn -B -pl acccesos -am package -DskipTests

# Etapa 2: Entorno de Ejecución Ligero JRE
FROM eclipse-temurin:17-jre AS runner
WORKDIR /app
# Copia el artefacto JAR generado de la etapa anterior de forma segura
COPY --from=builder /build/accesos/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]