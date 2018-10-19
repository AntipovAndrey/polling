FROM openjdk:8
VOLUME /tmp
EXPOSE 8080
ADD /build/libs/poll-0.0.1-SNAPSHOT.jar poll.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=container", "-jar", "poll.jar"]