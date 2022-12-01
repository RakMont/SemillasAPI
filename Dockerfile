FROM openjdk:11

EXPOSE 8081

ADD target/seeds-backend.jar seeds-backend.jar

WORKDIR /usr/scr/app

ENTRYPOINT ["java", "-jar", "seeds-backend.jar"]