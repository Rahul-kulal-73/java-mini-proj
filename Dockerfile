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

# REMOVE the old COPY command. 
# We're now copying the extracted contents to the ROOT directory.

# 🌟 NEW, RELIABLE COPY METHOD: Extracting WAR contents to ROOT
# 1. Create a directory to hold the extracted WAR contents (temporary location inside the build image)
RUN mkdir -p /app-exploded

# 2. Unzip the WAR file into the temporary directory
# This assumes the WAR is a standard zip/jar file structure
RUN unzip /app/target/photo-gallery-app-1.0-SNAPSHOT.war -d /app-exploded

# 3. Copy the extracted application content directly into Tomcat's ROOT folder
# This forces the application to load at the root context (/)
COPY --from=build /app-exploded /usr/local/tomcat/webapps/ROOT

# Set the port Render will expose
EXPOSE 8080

CMD ["catalina.sh", "run"]
