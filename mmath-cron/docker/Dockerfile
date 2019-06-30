FROM openjdk:11


COPY ./*.jar /usr/src/app/app.jar

EXPOSE 4567

WORKDIR /usr/src/app

CMD java -Xmx128M -jar app.jar

