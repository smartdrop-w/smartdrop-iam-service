# =============================================================================
# SMARTDROP PLATFORM - MULTI-STAGE DOCKERFILE: IAM SERVICE (JAVA 21)
# =============================================================================

# Etapa 1: Build y empaquetado con Maven Wrapper
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de Maven Wrapper, repositorio local compartido y descriptor pom.xml
COPY .mvn/ .mvn/
COPY .m2-repo/ .m2-repo/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B || true

# Copiar codigo fuente y compilar JAR de produccion excluyendo tests unitarios
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime minimalista con Eclipse Temurin JRE 21 Alpine
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Instalar wget (nativo en Alpine) y tzdata para zona horaria UTC
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

# Crear usuario y grupo no-root por seguridad Zero-Trust
RUN addgroup -S smartdrop && adduser -S smartdrop -G smartdrop

# Copiar JAR generado desde la etapa de compilacion
COPY --from=build /app/target/*.jar app.jar
RUN chown -R smartdrop:smartdrop /app

USER smartdrop

# Puerto expuesto para smartdrop-iam-service
EXPOSE 8081

# Variables de entorno por defecto (compatibles con Docker Compose y Azure)
ENV SERVER_PORT=8081 \
    PORT=8081 \
    SPRING_PROFILES_ACTIVE=mysql \
    DATABASE_HOST=localhost \
    DATABASE_PORT=3306 \
    DATABASE_USER=root \
    DATABASE_PASSWORD=admin \
    DATABASE_USE_SSL=false \
    REDIS_HOST=localhost \
    REDIS_PORT=6379 \
    JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Healthcheck probe contra endpoint de salud Spring Boot
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8081/api/v1/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
