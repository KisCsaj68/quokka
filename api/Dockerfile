FROM maven:3.8.6 as builder

USER root
COPY ./model /model
COPY ./api /api

WORKDIR /model
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean install

WORKDIR /api
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean package



FROM openjdk:17.0.1-jdk-slim
COPY --from=builder /api/target/api-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./api-0.0.1-SNAPSHOT.jar"]
