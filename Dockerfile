FROM adoptopenjdk/openjdk11:x86_64-debianslim-jre-11.0.20_8

RUN mkdir -p /opt/app
COPY ./app/build/libs/app-all.jar /opt/app/app.jar

EXPOSE 8080/tcp
CMD ["java", "-jar", "/opt/app/app.jar"]
