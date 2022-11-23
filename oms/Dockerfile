ARG MVN_VERSION=3.8.6
ARG JAVA_IMAGE_VERSION=17.0.1
FROM maven:${MVN_VERSION} as builder


COPY ./common /common
COPY ./oms /oms

WORKDIR /common
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean install

WORKDIR /oms
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


ARG JAVA_IMAGE_VERSION
FROM openjdk:${JAVA_IMAGE_VERSION}-jdk-slim
COPY --from=builder /oms/target/oms-0.0.1-SNAPSHOT-jar-with-dependencies.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./oms-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]
