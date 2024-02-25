FROM openjdk:20

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} orderservices.jar

ENTRYPOINT ["java","-jar","/orderservices.jar"]

EXPOSE 8082