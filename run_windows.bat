@echo off

if "%MAVEN_HOME%"=="" (
    echo .
    echo "Error : Maven isn't installed (%MAVEN_HOME% variable undefined)"
    pause
) else (
    if not exist "chromat_ynk\pom.xml" (
        echo "Error : chromat_ynk\pom.xml file not found"
        pause
    ) else (
        mvn clean install -f chromat_ynk\pom.xml javafx:run
    )
)


