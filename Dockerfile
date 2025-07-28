FROM ksm0200/ubuntu:1.0.0
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

ENV PYTHON_BASE=/python_project/.venv/bin
ENV PYTHON_EMOTION=/python_project/source

ENV MAIL_USERNAME=**
ENV MAIL_PASSWORD=**

ENV KAKAO_APIKEY=**

ENV NAVER_APIKEY=**
ENV NAVER_SECRET=**

ENV REDIS_HOST=localhost
ENV REDIS_PORT=6379

ENTRYPOINT [  "java",  "-Ddb.password=${DB_PASSWORD}", "-Ddb.url=${DB_URL}",  "-Ddb.username=${DB_USERNAME}",  "-Dspring.jpa.hibernate.ddl-auto=${DDL_AUTO}",  "-Dfile.path=${FILE_PATH}",  "-Dfile.url=${FILE_URL}",  "-Dkakao.apikey=${KAKAO_APIKEY}",  "-Dmail.username=${MAIL_USERNAME}", "-Dmail.password=${MAIL_PASSWORD}",  "-Dnaver.apikey=${NAVER_APIKEY}",  "-Dnaver.secret=${NAVER_SECRET}",  "-Dpython.base=${PYTHON_BASE}",  "-Dpython.emotion=${PYTHON_EMOTION}",  "-Dredis.host=${REDIS_HOST}",  "-Dredis.port=${REDIS_PORT}",  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}",  "-jar", "app.jar"]

EXPOSE ${PORT}