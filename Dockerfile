FROM maven:3.6.3-jdk-14

ADD . /usr/src/sparkcrawler
WORKDIR /usr/src/sparkcrawler
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]
