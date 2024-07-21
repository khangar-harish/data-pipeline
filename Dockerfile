FROM openjdk

WORKDIR /app

COPY target/data-pipeline.jar data-pipeline.jar

EXPOSE 9595

ENTRYPOINT ["java", "-jar", "data-pipeline.jar"]