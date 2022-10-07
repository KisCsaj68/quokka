FROM maven:3.8.6 as builder

USER root
COPY ./model /model
COPY ./api /api

WORKDIR /model
RUN mvn clean install

WORKDIR /api
RUN mvn clean package -Dmaven.test.skip=true



FROM openjdk:17.0.1-jdk-slim
COPY --from=builder /api/target/api-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./api-0.0.1-SNAPSHOT.jar"]
