FROM eclipse-temurin:21-jdk

COPY target/bank.jar /app/bank.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "bank.jar"]
