FROM openjdk:11

# Crea directorio de trabajo
RUN mkdir -p /home/app
WORKDIR /home/app

# Variable de entorno
ENV SPRING_PROFILES_ACTIVE=dev

# Copia directamente el .jar generado por Maven
COPY target/*.jar user-service.jar

# Expone el puerto
EXPOSE 8700

# Ejecuta el servicio
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "user-service.jar"]
