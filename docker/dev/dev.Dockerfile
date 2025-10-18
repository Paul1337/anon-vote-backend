FROM maven:3.9-eclipse-temurin-21 AS development

WORKDIR /app

RUN apt-get update && apt-get install -y curl

ARG WATCH_EXEC_DOWNLOAD
RUN curl -L -o /tmp/watchexec.deb $WATCH_EXEC_DOWNLOAD
RUN dpkg -i /tmp/watchexec.deb || apt-get install -f -y
RUN rm /tmp/watchexec.deb

COPY ../../pom.xml .
RUN mvn dependency:go-offline

COPY ../../src ./src

EXPOSE ${APP_PORT}

COPY ../../docker/dev/start.sh /app/start.sh
RUN chmod +x /app/start.sh

CMD ["/app/start.sh"]
