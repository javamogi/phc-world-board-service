FROM openjdk:17-ea-11-jdk-slim
ENV TZ="Asia/Seoul"
VOLUME /tmp
COPY build/libs/phc-world-board-service-1.0.jar BoardService.jar
ENTRYPOINT ["java","-jar","BoardService.jar"]