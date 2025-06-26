FROM openjdk:8-jre
WORKDIR /opt/arenawars
COPY server/awserver.jar ./
COPY server/config.ini ./
COPY server/dependencies/ ./dependencies
COPY start.sh ./
RUN chmod +x start.sh
CMD ["./start.sh"]

