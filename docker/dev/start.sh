#!/bin/bash

watchexec -e java,properties,yml -w src mvn compile &

mvn spring-boot:run -Dspring-boot.run.fork=false -Dspring-boot.run.jvmArguments="$JAVA_OPTS"