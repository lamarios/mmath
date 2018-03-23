FROM openjdk:9-jre-slim


COPY ./*.jar /usr/src/app/app.jar

EXPOSE 4567

WORKDIR /usr/src/app

CMD java -jar app.jar

