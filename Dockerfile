# ------------------
# STEP 1: Build Stage (Uses a specific, stable Temurin 17 image)
# ------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
# Run go-offline first to leverage Docker layer caching
RUN mvn dependency:go-offline
COPY src ./src
# Build the package
RUN mvn clean package -DskipTests

# ------------------
# STEP 2: Production Stage (Uses Tomcat/JRE 17)
# ------------------
FROM tomcat:9.0-jre17-temurin-jammy

# UPDATED: Remove the 'apt-get install unzip' step. It's not needed.

# UPDATED: Clean out the default ROOT app that comes with Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# UPDATED: Copy your WAR file directly, renaming it to ROOT.war
# Tomcat will automatically see this and deploy it to the root.
COPY --from=build /app/target/photo-gallery-app-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
