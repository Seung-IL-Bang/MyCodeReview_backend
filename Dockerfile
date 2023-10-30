FROM openjdk:17

WORKDIR /app

COPY ./build/libs/*-SNAPSHOT.jar app.jar

ENV IDLE_PORT=8080
ENV JAVA_RMI_SERVER_HOSTNAME=localhost

CMD java \
-Dserver.port=$IDLE_PORT \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=1099 \
-Dcom.sun.management.jmxremote.rmi.port=1099 \
-Dcom.sun.management.jmxremote.authenticate=true \
-Dcom.sun.management.jmxremote.access.file=/app/config/jmxremote.access \
-Dcom.sun.management.jmxremote.password.file=/app/config/jmxremote.password \
-Dcom.sun.management.jmxremote.ssl=false \
-Djava.rmi.server.hostname=$JAVA_RMI_SERVER_HOSTNAME \
-jar app.jar