FROM openjdk:9-jre-slim


COPY ./*.jar /usr/src/app/app.jar


WORKDIR /usr/src/app

EXPOSE 15678

CMD ["sh", "-c", "java  -Dreddit.secret=${REDDIT_SECRET} -Dreddit.client.id=${REDDIT_CLIENT_ID}  -Dbase.url=${BASE_URL} -jar app.jar"]
