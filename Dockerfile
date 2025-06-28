FROM openjdk:8-jre
WORKDIR /opt/arenawars
COPY server/awserver.jar ./
COPY server/config.ini ./
COPY server/dependencies/ ./dependencies
CMD ["java", "-jar", "awserver.jar"]
