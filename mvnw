#!/bin/sh
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
fi

if [ -f "$HOME/.m2/wrapper/dists/apache-maven-3.9.10/a38810a491b03367137adfdfbe7d14c4/bin/mvn" ]; then
    exec "$HOME/.m2/wrapper/dists/apache-maven-3.9.10/a38810a491b03367137adfdfbe7d14c4/bin/mvn" "$@"
fi

echo "Maven not found in PATH."
exit 1
