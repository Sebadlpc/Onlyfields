# =============================================================================
# Etapa 1: Builder - Compilación con Maven standard
# =============================================================================
FROM eclipse-temurin:17-jdk AS builder

RUN apt-get update && apt-get install -y maven
WORKDIR /build

# 1. Copiamos TODO el proyecto (padre y todas las carpetas de los hijos)
COPY . .

# 2. Limpiamos cualquier build previo para evitar errores de duplicados
RUN mvn clean -B

# 3. Compilamos SOLO el módulo "usuarios"
RUN mvn -B -pl usuarios -am package -DskipTests

# =============================================================================
# Etapa 2: Runtime - Imagen Runtime solo con JRE
# =============================================================================
FROM eclipse-temurin:17-jre AS runner

# 4. Extraemos EXCLUSIVAMENTE el jar de usuarios. 
COPY --from=builder /build/usuarios/target/ms-usuarios-1.0.0.jar /app-usuarios.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app-usuarios.jar"]
