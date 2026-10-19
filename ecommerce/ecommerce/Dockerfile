# Etapa 1: Construcción (Build)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
# Usamos el Gradle Wrapper del proyecto para mantener la misma versión local y en Docker.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src
RUN chmod +x gradlew && ./gradlew bootJar -x test \
    && cp build/libs/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos solo el JAR generado de la etapa anterior
COPY --from=builder /app/app.jar app.jar
# Exponemos el puerto 8080
EXPOSE 8080
# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]