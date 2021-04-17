FROM azul/zulu-openjdk:11
VOLUME /tmp
VOLUME /target

ARG JAR_FILE
ARG FIREBASE_KEY
COPY ${JAR_FILE} app.jar
COPY ${FIREBASE_KEY} firebase.key

EXPOSE 8227
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
