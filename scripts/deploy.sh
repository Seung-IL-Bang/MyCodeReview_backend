#!/bin/bash

echo "==========================START DEPLOY=========================="
BASE_PATH=/home/ubuntu/build
BUILD_PATH=$(ls $BASE_PATH/*.jar)
JAR_FILE=$(basename "$BUILD_PATH")

echo ">>> BUILD_PATH:" "$BUILD_PATH"
echo ">>> JAR_FILE: $JAR_FILE"

echo "==========================현재 구동중인 Port 확인=========================="
CURRENT_PORT=$(curl -s http://localhost/port)
echo ">>> CURRENT_PORT:" "$CURRENT_PORT"

if [ "$CURRENT_PORT" == 8081 ]
then
  IDLE_PORT=8082
elif [ "$CURRENT_PORT" == 8082 ]
then
  IDLE_PORT=8081
else
  echo ">>> 일치하는 Port 가 없습니다."
  echo ">>> 8081 Port 를 할당합니다."
  IDLE_PORT=8081
fi

echo ">>> IDLE_PORT: " $IDLE_PORT

echo ">>> $IDLE_PORT 로 실행중인 애플리케이션 PID 확인"
IDLE_PID=$(sudo lsof -t -i:$IDLE_PORT)
echo ">>> IDLE_PID: $IDLE_PID"

echo "==========================IDLE_APPLICATION 종료 프로세스 시작=========================="
if [ -z "$IDLE_PID" ]
then
  echo ">>> 현재 PID:$IDLE_PID 로 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo ">>> kill -15 $IDLE_PID"
  kill -15 "$IDLE_PID"
  echo "==========================Success kill IDLE_APPLICATION=========================="
  echo ">>> sleep 5"
  sleep 5
fi

echo "==========================New Revision Application START in PORT:$IDLE_PORT=========================="
APPLICATION_PATH=$BASE_PATH/$JAR_FILE
echo ">>> APPLICATION_PATH: " "$APPLICATION_PATH"
nohup java \
-Dserver.port=$IDLE_PORT \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=1099 \
-Dcom.sun.management.jmxremote.rmi.port=1099 \
-Dcom.sun.management.jmxremote.authenticate=true \
-Dcom.sun.management.jmxremote.access.file=/home/ubuntu/app/config/jmxremote.access \
-Dcom.sun.management.jmxremote.password.file=/home/ubuntu/app/config/jmxremote.password \
-Dcom.sun.management.jmxremote.ssl=false \
-Djava.rmi.server.hostname=43.202.39.58 \
-jar "$APPLICATION_PATH" \
> /dev/null \
2> /dev/null \
< /dev/null &

echo "==========================IDLE Port:$IDLE_PORT Application 10초 후 Health Check 시작=========================="
sleep 10


echo "==========================Start Health Check=========================="
echo ">>> curl -s http://localhost:$IDLE_PORT/actuator/health"
for retry_count in {1..10}
do
  response=$(curl -s http://localhost:$IDLE_PORT/actuator/health)
  count=$(echo "$response" | grep 'UP' | wc -l)

  if [ "$count" -ge 1 ]
  then
    echo "==========================Health Check 성공=========================="
    break
  else
    echo ">>> Health Check 의 응답을 알 수 없습니다."
  fi

  if [ "$retry_count" -eq 10 ]
  then
    echo "==========================Health check 실패=========================="
    echo ">>> Nginx 에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo ">>> 10초 후에 재시도 합니다."
  sleep 10
done

echo "==========================Application Switch Process=========================="
echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" |sudo tee /etc/nginx/conf.d/service-url.inc

echo ">>> Nginx Current Proxy Port:" "$CURRENT_PORT"

echo "==========================Nginx Reload=========================="
sudo service nginx reload

sleep 3
NEW_PROXY_PORT=$(curl -s http://localhost/port)
echo ">>> Current Proxy Port:" "$NEW_PROXY_PORT"

echo "==========================END DEPLOY=========================="