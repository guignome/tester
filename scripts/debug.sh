#!/bin/sh
QUARKUS_LOG_LEVEL=trace java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005\
 -jar target/quarkus-app/quarkus-run.jar -f src/test/resources/example6.yaml 