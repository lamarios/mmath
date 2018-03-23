FROM openjdk:9-jre-slim


COPY ./*.jar /usr/src/app/app.jar

WORKDIR /usr/src/app

CMD ["sh", "-c", "java  -Dreddit.username=${REDDIT_USERNAME} -Dreddit.password=${REDDIT_PASSWORD} -Dreddit.secret=${REDDIT_SECRET} -Dreddit.client.id=${REDDIT_CLIENT_ID}  -jar app.jar"]

