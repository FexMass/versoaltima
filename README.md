# Task Application

<br>

### Introduction
This application handles the processing of XML data, interacts with external systems, and 
manages records of different types in a relational database.

### Tech Stack
Backend: Java 11, Spring Boot
Build: Maven

## Getting Started
Follow these steps to run the application:

### Prerequisites
- Java 11 or higher
- An external server (e.g., Tomcat) if deploying as a WAR
- A relational database (H2 is used by default)

### Installation
1. Clone the repository:
  ```bash
  $ git clone https://github.com/yourusername/task-application.git
  ```
<br>

## Running the Application via installed Tomcat

  ### Setting TASK_X_PATH:
  When starting Tomcat, system property TASK_X_PATH should be set:
  For UNIX/Linux/macOS:
  ```bash
   $ export TASK_X_PATH=/path/to/your/config/directory
  ```
  For Windows:
  set TASK_X_PATH=C:\path\to\your\config\directory

It is needed also to pass the TASK_X_PATH as a Java system property to Tomcat.
This can be achieved by editing the setenv.sh (or setenv.bat for Windows) in the bin directory of Your Tomcat installation:

* CATALINA_OPTS="$CATALINA_OPTS -DTASK_X_PATH=$TASK_X_PATH"

Also, under Tomcat folder in the lib folder we should paste jar: hsqldb-2.7.1, so it uses correct version of DB.

This ensures that when Tomcat starts up, the JVM has access to the TASK_X_PATH system property, and Spring Boot can then use this to locate the application.properties file.

After that, running startup.bat will start the server at http://localhost:8080.

### Getting application .war 

Running command mvn clean package (inside project directory) will generate task.war inside the TARGET folder.
Paste/deploy that .war file inside Tomcat/webapps it should reploy it in few seconds.
<br>

## Running the Application locally:
Application uses hsqldb and its properties are defined in application.properties, it is defined as well to match any OS that ${user.home} matches that and database folder will be created
there when application is stopped or shutddown that records remains.

* Setting TASK_X_PATH should be set as well, but this time just this if enough:
  For UNIX/Linux/macOS:
  ```bash
   $ export TASK_X_PATH=/path/to/your/config/directory
  ```
  For Windows:
  set TASK_X_PATH=C:\path\to\your\config\directory

Application can be run with favourite IDE or via console: mvn spring-boot:run 
  ```bash
   $ mvn spring-boot:run 
  ```

## Testing
Application holds tests and those can be inside project directory run with: 
 ```bash
   $ mvn test
  ```
<br>

* Author <b>Massimo Gruicic<b>

