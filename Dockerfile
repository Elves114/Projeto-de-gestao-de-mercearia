# ============================================================
# STAGE 1 — BUILD
# ============================================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia o pom e as fontes
COPY pom.xml .
COPY src ./src

# Build limpo (sem testes — corremos testes localmente)
RUN mvn clean package -DskipTests -B


# ============================================================
# STAGE 2 — RUNTIME
# ============================================================
FROM eclipse-temurin:21.0.5_11-jre-alpine

WORKDIR /app

# Instala curl para o healthcheck
RUN apk add --no-cache curl

# Utilizador não-root
RUN addgroup -S spring && adduser -S spring -G spring

# ★ Cria o diretório de logs ANTES de mudar de user
RUN mkdir -p /app/logs && chown -R spring:spring /app

USER spring:spring

# Copia o JAR compilado da stage anterior
COPY --from=build /app/target/*.jar app.jar

# Porta da API
EXPOSE 8080

# Tuning da JVM
ENV JAVA_OPTS="-Xmx400m -Xms256m -XX:+ExitOnOutOfMemoryError"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Arranca com o perfil de produção
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=prod"]