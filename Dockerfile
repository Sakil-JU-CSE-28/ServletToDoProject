# Use an official Tomcat base image
FROM tomcat:9.0

# Set the working directory inside the container
WORKDIR /usr/local/tomcat/webapps/

# Copy the WAR file into the webapps directory
COPY target/TaskBazaar-1.0-SNAPSHOT.war TaskBazaar-1.0-SNAPSHOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
