# ------------------
# STEP 1: Build Stage (Uses Maven 3 with OpenJDK 17)
# ------------------
FROM maven:3-openjdk-17 AS build 

# Set the current working directory inside the container
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
# This creates target/photo-gallery-app-1.0-SNAPSHOT.war
RUN mvn clean package -DskipTests

# ------------------
# STEP 2: Production Stage (Uses Tomcat/JRE 17)
# ------------------
FROM tomcat:9.0-jre17-temurin-jammy 

# CRITICAL FIX: Install unzip utility, as it's not present in the base image.
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# 1. Access the output of the build stage and copy the WAR file into a temporary location.
WORKDIR /tmp_app
COPY --from=build /app/target/photo-gallery-app-1.0-SNAPSHOT.war .

# 2. Extract the WAR contents into Tomcat's webapps/ROOT folder.
RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    unzip photo-gallery-app-1.0-SNAPSHOT.war -d /usr/local/tomcat/webapps/ROOT

# 3. Clean up the temporary directory (optional, but good practice)
RUN rm -rf /tmp_app

# Set the port Render will expose
EXPOSE 8080

CMD ["catalina.sh", "run"]
