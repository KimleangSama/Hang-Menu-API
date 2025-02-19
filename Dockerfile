FROM maven:3.9.9
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn package -DskipTests

FROM tomcat:jdk21-temurin-noble
COPY --from=0 /usr/src/app/target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 10000
CMD ["catalina.sh", "run"]
