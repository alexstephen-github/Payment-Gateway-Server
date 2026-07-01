# syntax=docker/dockerfile:1
#
# This is the Dockerfile the OpenShift build reads (build context = repo root,
# dockerfilePath = docker/Dockerfile). It is kept identical to the root
# ./Dockerfile; both build the same image. All COPY paths are relative to the
# repo root, so the build context MUST remain the repository root (no contextDir).

###############################################################################
# Stage 1 — Build the Spring Boot fat jar with Maven on a Red Hat UBI JDK image
###############################################################################
FROM registry.access.redhat.com/ubi9/openjdk-21:latest AS build

# The UBI OpenJDK image runs as user 185 (group 0). Keep ownership consistent so
# Maven can write the target/ dir and its local repository.
WORKDIR /build

# Copy the POM first and pre-fetch dependencies so this layer is cached and only
# re-runs when pom.xml actually changes.
COPY --chown=185:0 pom.xml ./
RUN mvn -B -ntp dependency:go-offline

# Now copy the sources and build. Tests are skipped for a faster image build;
# run them in CI instead.
COPY --chown=185:0 src ./src
RUN mvn -B -ntp clean package -DskipTests

###############################################################################
# Stage 2 — Slim JRE-only runtime image (also UBI, also OpenShift-ready)
###############################################################################
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest

# Metadata
LABEL org.opencontainers.image.title="Payment Gateway Mock Server" \
      org.opencontainers.image.description="Spring Boot mock of the Payment Gateway API returning random, spec-shaped data" \
      org.opencontainers.image.version="2.0.0"

WORKDIR /deployments

# Copy the built jar. Group 0 gets read access so the container works under any
# arbitrary UID that OpenShift assigns (the UID is always a member of group 0).
COPY --from=build --chown=185:0 /build/target/*.jar /deployments/app.jar

# Container-friendly JVM defaults. UseContainerSupport (on by default in Java 21)
# makes the JVM honour the pod's memory limit; MaxRAMPercentage caps heap within it.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport" \
    SERVER_PORT=8080

# Non-privileged port, matches server.port / SERVER_PORT.
EXPOSE 8080

# The base runtime image already runs as non-root user 185 — no USER root needed,
# which keeps it compatible with OpenShift's restricted SCC.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /deployments/app.jar"]
