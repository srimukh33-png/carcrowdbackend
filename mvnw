#!/bin/sh
# CarCrowd Maven Wrapper Script
MVN_BIN="/Users/srimukhas/.m2/wrapper/dists/apache-maven-3.9.16-bin/5grr65jo27hi51sujmtcldfovl/apache-maven-3.9.16/bin/mvn"
if [ -x "$MVN_BIN" ]; then
  exec "$MVN_BIN" "$@"
elif command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
else
  echo "Error: Maven not found." >&2
  exit 1
fi
