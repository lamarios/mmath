FROM openjdk:8

EXPOSE 4567

COPY . /usr/src/app

RUN mkdir /usr/src/app/cache

VOLUME /usr/src/app/cache

WORKDIR /usr/src/app

CMD bash start.sh
