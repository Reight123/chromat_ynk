@echo off

if "%MAVEN_HOME%"=="" (
    echo .
    echo "Maven isn't installed (%MAVEN_HOME% variable undefined)"
    exit /b 1
)

mvn -f chromat_ynk\pom.xml javafx:run
