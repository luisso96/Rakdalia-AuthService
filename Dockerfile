# ============================================
# 1. ETAPA DE CONSTRUCCIÓN (build)
# ============================================
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Instalar Gradle manualmente
RUN apk add --no-cache gradle

# Copiar archivos de configuración
COPY build.gradle settings.gradle ./
COPY src src/

# Ejecutar Gradle
RUN gradle dependencies --no-daemon
RUN gradle bootJar --no-daemon

# ============================================
# 2. ETAPA DE EJECUCIÓN (runtime)
# ============================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl
RUN addgroup -S authuser && adduser -S authuser -G authuser
USER authuser

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_OPTS="-Xmx512m"
EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]