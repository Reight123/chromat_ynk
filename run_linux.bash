#!/bin/bash

if [ -z "$MAVEN_HOME" ]; then
        echo "Maven isn't installed (\$MAVEN_HOME variable undefined)"
        exit 1
else
    if [ ! -d "$MAVEN_HOME/bin" ]; then
        echo "Maven installation incomplete (\$MAVEN_HOME/bin not found)"
        exit 1
    else
        if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
            echo "Maven installation incomplete (\$MAVEN_HOME/bin/mvn not found)"
            exit 1
        fi
    fi
fi

mvn -f chromat_ynk/pom.xml clean javafx:run
