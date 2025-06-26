FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    openjdk-8-jdk \
    mariadb-server \
    nginx && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /opt/arenawars

COPY server/src ./src
COPY server/dependencies ./dependencies
COPY server/config.ini ./config.ini
COPY arenawars.sql ./arenawars.sql
COPY client /usr/share/nginx/html
COPY run.sh /run.sh

EXPOSE 80 8081

CMD ["/run.sh"]
