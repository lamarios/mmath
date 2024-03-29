# used by drone ci, same file for all projects
FROM openjdk:11-jre-slim

COPY ./*.jar /usr/src/app/app.jar


WORKDIR /usr/src/app

EXPOSE 15678

CMD java -Xmx256M -jar app.jar
