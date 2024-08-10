FROM tomcat:9.0.93-jdk17

ADD target/*.war /usr/local/tomcat/webapps/java-web-app.war

EXPOSE 8080

CMD ["catalina.sh" ,"run"]