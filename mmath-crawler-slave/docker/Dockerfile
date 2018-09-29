FROM openjdk:10


COPY ./*.jar /usr/src/app/app.jar

EXPOSE 4567

WORKDIR /usr/src/app

CMD java -Xmx32m -jar app.jar

