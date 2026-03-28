FROM maven:3.9-eclipse-temurin-25-alpine AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:25-jdk-alpine AS jlink-builder

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN jar xf app.jar && \
    jdeps \
      --ignore-missing-deps \
      --print-module-deps \
      --multi-release 25 \
      --recursive \
      --class-path 'BOOT-INF/lib/*' \
      app.jar > modules.txt && \
    cat modules.txt

RUN jlink \
      --module-path "$JAVA_HOME/jmods" \
      --add-modules "$(cat modules.txt)" \
      --strip-debug \
      --no-man-pages \
      --no-header-files \
      --compress zip-6 \
      --output /custom-jre

FROM eclipse-temurin:25-jdk-alpine AS extractor

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM alpine:3.21

WORKDIR /app

COPY --from=jlink-builder /custom-jre /opt/java

ENV PATH="/opt/java/bin:$PATH" \
    JAVA_HOME="/opt/java"

COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup && \
    mkdir -p /app/data && \
    chown -R appuser:appgroup /app/data
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Dspring.datasource.url=jdbc:h2:file:/app/data/dcbapp;DB_CLOSE_ON_EXIT=FALSE", \
  "org.springframework.boot.loader.launch.JarLauncher"]