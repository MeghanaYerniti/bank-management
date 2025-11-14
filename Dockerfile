FROM openjdk:21-jdk
ADD target/bank.jar bank.jar
ENTRYPOINT["java", "-jar", "/bank.jar"]