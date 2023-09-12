FROM maven:3.8.3-openjdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM ibm-semeru-runtimes:open-11-jre-focal
COPY --from=build /target/seed-0.0.1-SNAPSHOT.jar seed.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAM=70m", "-jar", "seed.jar"]

