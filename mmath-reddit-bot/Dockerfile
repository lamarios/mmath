FROM openjdk:11-jre-slim


COPY ./target/mmath*.jar /usr/src/app/app.jar

WORKDIR /usr/src/app

CMD java -Xmx128M -jar app.jar

