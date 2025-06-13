# Use a modern Java runtime
FROM openjdk:17-jre-slim
WORKDIR /app
COPY . /app
ENV MYSQL_HOST=db \
    MYSQL_PORT=3306 \
    MYSQL_USER=root \
    MYSQL_PASSWORD=secret \
    MYSQL_NAME=arenawars
EXPOSE 8081
ENTRYPOINT ["java","-cp","/app/awserver.jar:/app/dependencies/mysql-connector-j-8.3.0.jar","content.Init"]
