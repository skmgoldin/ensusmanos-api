FROM ubuntu:18.04

SHELL ["/bin/bash", "-c"]

RUN apt-get update
RUN apt-get install -y openjdk-11-jdk-headless wget unzip

RUN wget -q https://services.gradle.org/distributions/gradle-5.2.1-bin.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-5.2.1-bin.zip
ENV PATH "$PATH:/opt/gradle/gradle-5.2.1/bin"

COPY . $HOME/app/
WORKDIR /app

RUN gradle --no-daemon clean assemble

CMD ["gradle", "--no-daemon", "run"]

