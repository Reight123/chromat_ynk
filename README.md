# Chromat_ynk
CY TECH project on JAVA and JAVAFX made by:
- Adam Scwizka
- Clément Praud
- Farah Mahmoud
- Matthias Ribeiro

## Installation

### Clone the project

You can clone the project with the following command

```git clone https://github/Reight123/chromat_ynk.git```

### Install maven

Our project work with maven so you have to install it if you don't already have it

```
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xzvf apache-maven-3.9.6-bin.tar.gz
rm apache-maven-3.9.6-bin.tar.gz
mv apache-maven-3.9.6 /opt/apache-maven-3.9.6
echo 'export PATH=/opt/apache-maven-3.9.6/bin:$PATH' >> ~/.bashrc
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
bash
```

## Start the project

### Start the app

You can start the app with the following command

```mvn clean -f chromat_ynk/pom.xml javafx:run```

### Start just the drawing

If you want to start only the drawing of the last script you made and not all the app you can use this command

```mvn clean -f plotter/pom.xml javafx:run```







