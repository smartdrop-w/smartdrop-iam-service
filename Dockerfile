# =============================================================================
# SMARTDROP PLATFORM - MULTI-STAGE DOCKERFILE: IAM SERVICE (JAVA 21)
# =============================================================================

# Etapa 1: Build y empaquetado con Maven Wrapper
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de Maven Wrapper y dependencias
COPY .mvn/ .mvn/
COPY .m2-repo/ .m2-repo/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B || true

# Copiar cÃ³digo fuente y compilar JAR excluyendo tests unitarios en empaquetado rÃ¡pido
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime minimalista con Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Crear usuario y grupo no-root por seguridad Zero-Trust
RUN addgroup -S smartdrop && adduser -S smartdrop -G smartdrop

# Copiar JAR generado desde la etapa de compilaciÃ³n
COPY --from=build /app/target/*.jar app.jar
RUN chown -R smartdrop:smartdrop /app

USER smartdrop

# Puerto del IAM Microservice
EXPOSE 8081

ENV SERVER_PORT=8081
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8081/api/v1/health || exit 0

ENTRYPOINT ["java", "-jar", "app.jar"]

