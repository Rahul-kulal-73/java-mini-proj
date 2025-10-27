# ------------------
# STEP 1: Build Stage (Uses Maven 3 with OpenJDK 17)
# Use a known working tag that combines Maven and JDK 17
# ------------------
FROM maven:3.9.6-openjdk-17 AS build 
# NOTE: The tag was changed from 3.8.6-openjdk-17-slim to 3.9.6-openjdk-17

# Set the current working directory inside the container
WORKDIR /app
# Copy the pom.xml file first to cache dependencies
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline
# Copy the rest of the source code
COPY src ./src
# Build the WAR file (the 'package' goal runs 'compile' and creates the .war)
RUN mvn clean package -DskipTests

# ------------------
# STEP 2: Production Stage (Uses Tomcat/JRE 17)
# ------------------
# This image tag is generally reliable:
FROM tomcat:9.0-jre17-temurin-jammy 

# Copy the WAR file from the build stage into Tomcat's webapps directory
# The name 'photo-gallery-app.war' comes from your pom.xml <artifactId>
COPY --from=build /app/target/photo-gallery-app.war /usr/local/tomcat/webapps/ROOT.war

# Set the port Render will expose (Tomcat's default port)
EXPOSE 8080

# Command to start Tomcat (default for this base image)
CMD ["catalina.sh", "run"]
