FROM openjdk:11
RUN mkdir -p /home/app
WORKDIR /home/app
ENV SPRING_PROFILES_ACTIVE=dev
COPY target/*.jar user-service.jar
EXPOSE 8700
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "user-service.jar"]
