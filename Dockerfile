# ------------------
# STEP 1: Build Stage (Uses Maven 3 with OpenJDK 17)
# ------------------
FROM maven:3-openjdk-17 AS build

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

# Install unzip utility
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Copy and Extract the renamed WAR to ROOT
WORKDIR /tmp_app
# *** UPDATED: Copy target/photo-gallery-app-1.0-SNAPSHOT.war ***
COPY --from=build /app/target/photo-gallery-app-1.0-SNAPSHOT.war .
RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    # *** UPDATED: Unzip photo-gallery-app-1.0-SNAPSHOT.war ***
    unzip photo-gallery-app-1.0-SNAPSHOT.war -d /usr/local/tomcat/webapps/ROOT

# DIAGNOSTIC: List contents to verify (optional but useful)
RUN echo "--- Contents of /usr/local/tomcat/webapps/ROOT ---" && \
    ls -lR /usr/local/tomcat/webapps/ROOT && \
    echo "--- End of contents ---"

# Clean up
RUN rm -rf /tmp_app

EXPOSE 8080
CMD ["catalina.sh", "run"]
