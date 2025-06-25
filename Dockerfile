FROM pilet/java-base
ENV PROFILE=cloud
VOLUME /tmp

COPY target/app.jar /app.jar
ADD config /config
ADD keys /.ssh
RUN chmod 400 /.ssh/hetz_id

CMD ["java","-jar","app.jar"]