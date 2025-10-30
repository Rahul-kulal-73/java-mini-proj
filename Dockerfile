# ------------------
# STEP 1: Build Stage (Uses Maven 3 with OpenJDK 17)
# ------------------
FROM maven:3-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ------------------
# STEP 2: Production Stage (Uses Tomcat/JRE 17)
# ------------------
FROM tomcat:9.0-jre17-temurin-jammy

# Install unzip utility
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Copy and Extract WAR to ROOT
WORKDIR /tmp_app
COPY --from=build /app/target/photo-gallery-app-1.0-SNAPSHOT.war .
RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    unzip photo-gallery-app-1.0-SNAPSHOT.war -d /usr/local/tomcat/webapps/ROOT

# *** DIAGNOSTIC STEP: List the contents of the deployed ROOT directory ***
RUN echo "--- Contents of /usr/local/tomcat/webapps/ROOT ---" && \
    ls -lR /usr/local/tomcat/webapps/ROOT && \
    echo "--- End of contents ---"

# Clean up
RUN rm -rf /tmp_app

EXPOSE 8080
CMD ["catalina.sh", "run"]
