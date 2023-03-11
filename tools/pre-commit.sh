#!/bin/sh

cd "$(git rev-parse --show-toplevel)"
echo "./gradlew --init-script gradle/init.gradle.kts spotlessApply detekt lint test "
./gradlew --init-script gradle/init.gradle.kts spotlessApply detekt lint test --no-configuration-cache
exit $?