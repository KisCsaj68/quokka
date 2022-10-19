FROM maven:3.8.6 as oms_builder

USER root
COPY ./model /model
COPY ./oms /oms

WORKDIR /model
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean install

WORKDIR /oms
# go-offline using the pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn clean package


FROM openjdk:17.0.1-jdk-slim
COPY --from=oms_builder /oms/target/oms-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./oms-0.0.1-SNAPSHOT.jar"]
