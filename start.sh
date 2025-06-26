#!/bin/bash
set -e
HOST="${MYSQL_HOST:-db}"
PORT="${MYSQL_PORT:-3306}"
# Wait for MySQL to be available
until bash -c "</dev/tcp/$HOST/$PORT" 2>/dev/null; do
  echo "Waiting for MySQL at $HOST:$PORT..."
  sleep 2
done
exec java -jar awserver.jar
