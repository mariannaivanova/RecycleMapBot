FROM openjdk:17-alpine
COPY target/RecycleMapBot-0.0.1-SNAPSHOT.jar RecycleMapBot-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/RecycleMapBot-0.0.1-SNAPSHOT.jar"]