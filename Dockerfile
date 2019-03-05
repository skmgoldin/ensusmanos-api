FROM ubuntu:18.04

SHELL ["/bin/bash", "-c"]

RUN apt-get update
RUN apt-get install -y openjdk-11-jdk-headless wget unzip

RUN wget -q https://services.gradle.org/distributions/gradle-5.2.1-bin.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-5.2.1-bin.zip
ENV PATH "$PATH:/opt/gradle/gradle-5.2.1/bin"

ADD build.gradle.kts $HOME/app/
WORKDIR /app
RUN gradle --no-daemon assemble

ADD src $HOME/app/src
RUN gradle --no-daemon assemble

CMD ["gradle", "--no-daemon", "run"]

