#!/bin/bash


if command -v mvn &> /dev/null; then
    if mvn -v &> /dev/null; then
        if [ -f "chromat_ynk/pom.xml" ]; then
            mvn -f chromat_ynk/pom.xml clean javafx:run
        else
            echo "Error : chromat_ynk/pom.xml file not found."
            read -p "Press Enter to exit"
            exit 1
        fi
    else
        echo "Error : Maven installation incomplete or not configured."
        read -p "Press Enter to exit"
        exit 1
    fi
else
    echo "Error : Maven isn't installed."
    read -p "Press Enter to exit"
    exit 1
fi
