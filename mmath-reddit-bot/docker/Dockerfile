FROM openjdk:11-jre-slim


COPY ./*.jar /usr/src/app/app.jar

WORKDIR /usr/src/app

CMD ["sh", "-c", "java  -Dreddit.username=${REDDIT_USERNAME} -Dreddit.password=${REDDIT_PASSWORD} -Dreddit.secret=${REDDIT_SECRET} -Dreddit.client.id=${REDDIT_CLIENT_ID} -Dreddit.sub=${REDDIT_SUB} -Dreddit.bot.owner=${REDDIT_BOT_OWNER} -jar app.jar"]

