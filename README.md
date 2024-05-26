# Chromat_ynk
CY TECH project on JAVA and JAVAFX made by:
- Adam Scwizka
- Clément Praud
- Farah Mahmoud
- Matthias Ribeiro
- Gaétan Retel

## Installation

### Clone the project

You can clone the project with the following command

```bash
git clone https://github.com/Reight123/chromat_ynk.git
```

### Java version

Our project uses Java 21, which is the latest LTS version.
If your version of java isn't up-to-date, you can do it by writing the following commands :

```bash
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz
tar -xzf jdk-21_linux-x64_bin.tar.gz
rm jdk-21_linux-x64_bin.tar.gz
sudo mv jdk-21.0.3/ /usr/lib/jvm/
echo "export JAVA_HOME=/usr/lib/jvm/jdk-21.0.3" >> ~/.bashrc
echo "export PATH=$JAVA_HOME/bin:$PATH" >> ~/.bashrc
bash
```

### Install maven

Our project work with maven, so you'll have to install it if you don't already have it :

```bash
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xzvf apache-maven-3.9.6-bin.tar.gz
rm apache-maven-3.9.6-bin.tar.gz
mv apache-maven-3.9.6 /opt/apache-maven-3.9.6
echo 'export PATH=/opt/apache-maven-3.9.6/bin:$PATH' >> ~/.bashrc
bash
```

## Running the Project

### Starting the Application


The app can be started either by clicking on a file (quite logically named `run_linux.bash` on Linux and Mac, and `run_windows.bat` on Windows) or by writing and executing a command in a terminal :

The first time, start the app with the following command :

```bash
mvn clean install -f chromat_ynk/pom.xml javafx:run
``` 

After the initial setup, you can start the app with the following command:

```bash
mvn clean -f chromat_ynk/pom.xml javafx:run
```

### Start just the drawing

If you want to start only the drawing of the last script you made and not all the app you can use this command

```bash
mvn clean -f plotter/pom.xml javafx:run
```

# Information on the function available in the project

A Javadoc is available to see how the project works.
However, the functions might be named slightly differently in Java. Here is the list of command names in the app:


- CURSOR
- SELECT
- REMOVE


- FWD
- BWD
- TURN
- TURNR
- TURNL
- LOOKAT
- MOV
- POS
- HIDE
- SHOW
- PRESS
- THICK
- COLOR


- NUM
- INT
- STR
- BOOL
- DEL
- MATH


- IF
- WHILE
- FOR
- BLOCKEND


- MIRROR
- MIRROREND
- MIMIC
- MIMICEND


- CIRCLED
- CIRCLEF
- CROSS
- RECTANGLED
- RECTANGLEF
- SQUARED
- SQUAREF
- TRIANGLED
- TRIANGLEF



