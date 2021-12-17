FROM openjdk:11-jre-slim


COPY ./target/hypetrain*.jar /usr/src/app/app.jar


WORKDIR /usr/src/app

EXPOSE 15678

CMD java -Xmx256M -jar app.jar

