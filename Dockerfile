# build

FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.20_8-slim as builder

COPY . /src
WORKDIR /src

RUN ./gradlew build -x test

# runner

FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.20_8

COPY --from=builder /src/app/build/libs/app-all.jar /opt/app.jar

EXPOSE 8080/tcp
ENV AUTHENTICATION_BASEURL "http://127.0.0.1:8081/api/v1"
ENV METADATA_BASEURL "http://127.0.0.1:8082"
ENV WORKER_HOST "127.0.0.1"
ENV WORKER_PORT "1099"

CMD ["java", "-jar", "/opt/app.jar"]
