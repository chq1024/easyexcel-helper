FROM openjdk:11
EXPOSE 20002
ADD ./target/easyexcel-1.0.0-SNAPSHOT.jar easyexcel-demo.jar
ENTRYPOINT ["java","-jar","easyexcel-demo.jar"]
