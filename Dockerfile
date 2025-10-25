FROM pilet/java-base:24.21
ENV PROFILE=cloud
ENV DNS_PASSWORD=110b9ee1509df327bcae802444c641a90496f
VOLUME /tmp

COPY target/app.jar /app.jar
ADD deploy/config /deploy/config
ADD deploy/keys /.ssh
ADD deploy/sql /deploy/sql
RUN chmod 400 /.ssh/hetz_id

CMD ["java","-jar","app.jar"]