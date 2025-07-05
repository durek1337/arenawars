#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
BUILD_DIR="build/classes"
MANIFEST="MANIFEST.MF"
OUT_JAR="awserver.jar"
rm -rf "$BUILD_DIR" "$OUT_JAR" "$MANIFEST"
mkdir -p "$BUILD_DIR"
javac -encoding ISO-8859-1 --release 8 -classpath "dependencies/*:dependencies/tyrus/*" -d "$BUILD_DIR" src/content/*.java
cat > "$MANIFEST" <<EOM
Manifest-Version: 1.0
Class-Path: .
Main-Class: content.Init
EOM
jar cfm "$OUT_JAR" "$MANIFEST" -C "$BUILD_DIR" .
TMP_DIR=$(mktemp -d)
ABS_DIR=$(pwd)
for dep in dependencies/*.jar dependencies/tyrus/*.jar; do
  (cd "$TMP_DIR" && jar xf "$ABS_DIR/$dep")
  rm -rf "$TMP_DIR"/META-INF
done
jar uf "$OUT_JAR" -C "$TMP_DIR" .
rm -rf "$TMP_DIR" "$MANIFEST"
