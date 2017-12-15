# docker-kafka-consumer-archetype

This is a [Apache Maven](https://maven.apache.org/) archetype for generating Kafka **consumers** that are packaged as [Docker](https://docker.com/) containers.

## Making the Archetype Available

Simply clone this repository, navigate to the project directory and execute

```sh
mvn install
```

This will install the archetype into your local maven repository, making it available for project generation.

## Using the Archetype

To create a project from this archetype, navigate to the directory in which you want the generated project to reside and run

```sh
mvn archetype:generate \
  -DarchetypeGroupId=de.uni-stuttgart.ipvs.dds \
  -DarchetypeArtifactId=kafka-consumer-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT
```

This will open up an interactive project generation dialogue.

There are a number of template parameters that can be set during this process:

- `groupId`: The Maven groupId to use for the generated project's pom.xml
- `artifactId`: The Maven artifactId to use for the generated project's pom.xml
- `version`: The version string of the generated project
- `package`: The Java package in which the main class should reside in
- `dockerRepository`: The repository name of the resulting Docker container
- `mainClass`: The name for the main class of the project

The resulting project structure will look like this:

```
project-name/
+-- Dockerfile  .  .  .  .  .  .  .  .  .  .  .  The Dockerfile for the resulting container
+-- pom.xml  .  .  .  .  .  .  .  .  .  .  .  .  Maven's project object model
+-- README.md   .  .  .  .  .  .  .  .  .  .  .  A readme file explaining how to build and
|                                                run the project
`-- src/
    `-- main
        +-- java
        |   `-- your
        |       `-- package
        |           `-- name
        |               `-- MainClass.java .  .  The main entry point of the application
        `-- resources
            +-- avro
            |   `-- your
            |       `-- package
            |           `-- name
            |               `-- example.avsc  .  An example Avro schema for Kafka values
            `-- log4j.properties  .  .  .  .  .  Log4j logger configuration file
```
