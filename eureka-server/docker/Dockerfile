FROM amazoncorretto:17-alpine-jdk
MAINTAINER baeldung.com
COPY ../target/*.jar eurekaserver-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/eurekaserver-0.0.1-SNAPSHOT.jar"]