#!/bin/bash
set -e
# Start MariaDB
mysqld_safe &
# wait for DB
until mysqladmin ping --silent; do
  sleep 1
done
# initialize database if first run
if [ ! -f /var/lib/mysql/.initialized ]; then
  mysql -u root -e "CREATE DATABASE IF NOT EXISTS arenawars;"
  mysql -u root arenawars < /opt/arenawars/arenawars.sql
  touch /var/lib/mysql/.initialized
fi
# compile java sources
mkdir -p /opt/arenawars/build
javac -d /opt/arenawars/build -classpath "/opt/arenawars/dependencies/*" /opt/arenawars/src/content/*.java
# start websocket server
java -cp "/opt/arenawars/build:/opt/arenawars/dependencies/*" content.Init &
# run nginx in foreground
exec nginx -g 'daemon off;'
