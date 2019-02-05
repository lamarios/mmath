FROM openjdk:11


COPY ./*.jar /usr/src/app/app.jar

EXPOSE 4567

WORKDIR /usr/src/app

CMD java -Xmx32m -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 -jar app.jar

