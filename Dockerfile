FROM eclipse-temurin:17-alpine

COPY build/libs/announcer.jar /usr/bin

RUN mkdir /etc/announcer
VOLUME ["/etc/announcer"]
WORKDIR /etc/announcer

CMD ["java", "-jar", "/usr/bin/announcer.jar"]
