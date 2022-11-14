FROM azul/zulu-openjdk:11
CMD ["./mvnw", "clean", "package"]
COPY build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]