# Guidelines for Contributors

This repository contains a Java based WebSocket server (in `server/`) and a
static client (in `client/`).

## Building the server
- Run `./server/build.sh` whenever Java sources or dependencies change. The
  script compiles the code with Java 8 compatibility and assembles
  `server/awserver.jar` including all dependencies.
- Use this script as the project test step.

## General
- Keep existing LF line endings.
- Write commit messages in English.
