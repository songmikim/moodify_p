FROM openjdk:21-jdk
ARG JAR_PATH=build/libs/moodify-0.0.1-SNAPSHOT.jar
ARG PORT=5000
COPY ${JAR_PATH} app.jar
RUN mkdir uploads

ENV SPRING_PROFILES_ACTIVE=default,prod
ENV DB_URL=**
ENV DB_PASSWORD=**
ENV DB_USERNAME=**

ENV DDL_AUTO=**

ENV FILE_PATH=/uploads
ENV FILE_URL=/uploads

ENV MAIL_USERNAME=**
ENV MAIL_PASSWORD=**

ENV KAKAO_APIKEY=**

ENV NAVER_APIKEY=**
ENV NAVER_SECRET=**

ENV REDIS_HOST=**
ENV REDIS_PORT=**

ENTRYPOINT [  "java",  "-Ddb.password=${DB_PASSWORD}", "-Ddb.url=${DB_URL}",  "-Ddb.username=${DB_USERNAME}",  "-Dspring.jpa.hibernate.ddl-auto=${DDL_AUTO}",  "-Dfile.path=${FILE_PATH}",  "-Dfile.url=${FILE_URL}",  "-Dkakao.apikey=${KAKAO_APIKEY}",  "-Dmail.username=${MAIL_USERNAME}", "-Dmail.password=${MAIL_PASSWORD}",  "-Dnaver.apikey=${NAVER_APIKEY}",  "-Dnaver.secret=${NAVER_SECRET}", "-Dredis.host=${REDIS_HOST}",  "-Dredis.port=${REDIS_PORT}",  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}",  "-jar", "app.jar"]

EXPOSE ${PORT}