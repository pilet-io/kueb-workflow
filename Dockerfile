FROM pilet/java-base
ENV PROFILE=cloud
VOLUME /tmp

COPY target/app.jar /app.jar
ADD deploy/config /config
ADD deploy/keys /.ssh
ADD deploy/sql /sql
RUN chmod 400 /.ssh/hetz_id

CMD ["java","-jar","app.jar"]