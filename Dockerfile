FROM ubuntu:18.04

SHELL ["/bin/bash", "-c"]
WORKDIR $HOME/app

RUN apt-get update && apt-get install -y openjdk-11-jdk-headless

COPY build.gradle.kts $HOME/app/
COPY gradlew $HOME/app
COPY gradle $HOME/app/gradle
RUN ./gradlew --no-daemon assemble

COPY src $HOME/app/src
RUN ./gradlew --no-daemon assemble

CMD ["./gradlew", "--no-daemon", "run"]

