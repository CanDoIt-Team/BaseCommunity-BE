FROM openjdk:11-jdk
ARG JAR_FILE=build/libs/community-1.0.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]