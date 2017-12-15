FROM openjdk:8

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/${artifactId}/${artifactId}.jar"]

# Add Maven dependencies
ADD target/lib /usr/share/${artifactId}/lib
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/${artifactId}/${artifactId}.jar
