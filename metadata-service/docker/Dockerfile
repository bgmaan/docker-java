FROM amazoncorretto:17-alpine-jdk
COPY ../target/quarkus-app/ quarkus
ENV JAVA_OPTIONS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
EXPOSE 8080
CMD java -jar quarkus/quarkus-run.jar