# ------------------
# STEP 1: Build Stage (Uses Maven 3 with OpenJDK 17)
# ------------------
FROM maven:3-openjdk-17 AS build 

# Set the current working directory inside the container
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ------------------
# STEP 2: Production Stage (Uses Tomcat/JRE 17)
# ------------------
FROM tomcat:9.0-jre17-temurin-jammy 

# 1. COPY THE WAR FILE (using the corrected path)
COPY --from=build /app/target/photo-gallery-app-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# 2. COPY THE CONTEXT FILE (Crucial for forcing annotation scanning)
# Assumes tomcat-config/context.xml is in your project root
COPY tomcat-config/context.xml /usr/local/tomcat/webapps/ROOT/META-INF/context.xml

# Set the port Render will expose
EXPOSE 8080

CMD ["catalina.sh", "run"]
