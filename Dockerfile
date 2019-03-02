FROM ubuntu:18.04

SHELL ["/bin/bash", "-c"]

RUN apt-get update
RUN apt-get install -y openjdk-11-jdk-headless

COPY . $HOME/app/
WORKDIR /app

RUN ./gradlew --no-daemon clean assemble

ENTRYPOINT ["./gradlew", "--no-daemon", "run"]

