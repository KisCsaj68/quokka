FROM maven:3.8.6 as oms_builder

USER root
COPY ./model /model
COPY ./oms /oms

WORKDIR /model
RUN mvn clean install

WORKDIR /oms
RUN mvn clean package
CMD ["ls",'-latrR']


FROM openjdk:17.0.1-jdk-slim
COPY --from=oms_builder /oms/target/oms-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java"]
CMD ["-jar","./oms-0.0.1-SNAPSHOT.jar"]
