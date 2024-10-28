## Use an official Ubuntu-based OpenJDK 17 runtime as a parent image
#FROM openjdk:17-jdk
#
## Set the working directory inside the container
#
#
## ARG allows passing of build-time variables; in this case, it's used to determine the JAR file
#ARG JAR_FILE=target/*.jar
#
## Copy the application JAR file into the container
##COPY ${JAR_FILE} blogsite-user-service.jar
#COPY target/blog-auth-server.jar.jar /blog-auth-server.jar.jar
#
## Expose the port that your application runs on (Spring Boot default is 8080)
#EXPOSE 8082
#
## Run the JAR file as the container's main process
##ENTRYPOINT ["java","-jar","/blogsite-user-service.jar"]
#ENV JAVA_OPTS=""
#
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /blog-auth-server.jar.jar" ]
#

# Use an official Ubuntu-based OpenJDK 17 runtime as a parent image
#FROM openjdk:17-jdk
#
## Set the working directory inside the container
#WORKDIR /app
#
## ARG allows passing of build-time variables; in this case, it's used to determine the JAR file
#ARG JAR_FILE=target/*.jar
#
## Copy the application JAR file into the container
##COPY ${JAR_FILE} blogsite-api-gateway.jar
#COPY target/blogsite-api-gateway.jar /app/blogsite-api-gateway.jar
#
## Expose the port that your application runs on (Spring Boot default is 8080)
#EXPOSE 8080
#
## Run the JAR file as the container's main process
#ENTRYPOINT ["java","-jar","/app/blogsite-api-gateway.jar"]

# Use an official Ubuntu-based OpenJDK 17 runtime as a parent image
FROM openjdk:17-jdk

# Set the working directory inside the container
#WORKDIR /app

# ARG allows passing of build-time variables; in this case, it's used to determine the JAR file
ARG JAR_FILE=target/*.jar

# Copy the application JAR file into the container
COPY target/blog-auth-server.jar blog-auth-server.jar

# Expose the port that your application runs on (Spring Boot default is 8080)
EXPOSE 8082

# Run the JAR file as the container's main process
#ENTRYPOINT ["java","-jar","blogsite-service-registry.jar"]
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /blog-auth-server.jar" ]

