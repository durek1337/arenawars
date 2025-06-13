# Use a modern Java runtime
FROM openjdk:17-jre-slim
WORKDIR /app
COPY . /app
RUN sed -i 's/^mysql_host=.*/mysql_host=db/' config.ini
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/awserver.jar"]
