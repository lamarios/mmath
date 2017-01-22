FROM openjdk:8


COPY . /usr/src/app

RUN mkdir /usr/src/app/cache

VOLUME /usr/src/app/cache

WORKDIR /usr/src/app

CMD bash start.sh
