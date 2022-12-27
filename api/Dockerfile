ARG MVN_VERSION=3.8.6
ARG JAVA_IMAGE_VERSION=17.0.1
FROM maven:${MVN_VERSION} as dependencies

# go-offline using the pom.xml
COPY ./common /common
COPY ./api/pom.xml /api/pom.xml

WORKDIR /common
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline install

WORKDIR /api
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

ARG MVN_VERSION
ARG JAVA_IMAGE_VERSION
FROM dependencies as builder

#COPY ./common /common
COPY ./api /api

#WORKDIR /common
#RUN --mount=type=cache,target=/root/.m2 mvn clean install

WORKDIR /api
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

ARG JAVA_IMAGE_VERSION
FROM openjdk:${JAVA_IMAGE_VERSION}-jdk-slim
COPY --from=builder /api/target/api-0.0.1-SNAPSHOT-jar-with-dependencies.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./api-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]
