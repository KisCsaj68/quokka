ARG MVN_VERSION=3.8.6
ARG JAVA_IMAGE_VERSION=17.0.1
FROM maven:${MVN_VERSION} as builder


COPY ./model /model
COPY ./persister /persister

WORKDIR /model
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean install

WORKDIR /persister
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean package


ARG JAVA_IMAGE_VERSION
FROM openjdk:${JAVA_IMAGE_VERSION}-jdk-slim
COPY --from=builder /persister/target/persister-0.0.1-SNAPSHOT-jar-with-dependencies.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./persister-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]