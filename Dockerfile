FROM openjdk:17

WORKDIR /app

COPY ./build/libs/*-SNAPSHOT.jar app.jar

ENV IDLE_PORT=8080
ENV JMX_PORT=9010
ENV JAVA_RMI_SERVER_HOSTNAME=127.0.0.1


CMD java \
-Dserver.port=$IDLE_PORT \
-Dcom.sun.management.jmxremote=true \
-Dcom.sun.management.jmxremote.local.only=false \
-Dcom.sun.management.jmxremote.port=$JMX_PORT \
-Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT \
-Dcom.sun.management.jmxremote.authenticate=true \
-Dcom.sun.management.jmxremote.access.file=/home/ubuntu/app/config/jmxremote.access \
-Dcom.sun.management.jmxremote.password.file=/home/ubuntu/app/config/jmxremote.password \
-Dcom.sun.management.jmxremote.ssl=false \
-Djava.rmi.server.hostname=$JAVA_RMI_SERVER_HOSTNAME \
-Dlogging.config=/home/ubuntu/app/config/logback-spring.xml \
-jar app.jar