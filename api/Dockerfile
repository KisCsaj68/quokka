FROM maven:3.8.6 as builder

USER root
COPY ./model/pom.xml /model/pom.xml
COPY ./api/pom.xml /api/pom.xml

WORKDIR /model
# go-offline using the pom.xml
# TODO: dependency:resolve-plugins
RUN mvn dependency:go-offline
COPY ./model /model
RUN mvn clean install

WORKDIR /api
# go-offline using the pom.xml
RUN mvn dependency:go-offline
COPY ./api /api
RUN mvn clean package

FROM openjdk:17.0.1-jdk-slim
COPY --from=builder /api/target/api-0.0.1-SNAPSHOT-jar-with-dependencies.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./api-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]
