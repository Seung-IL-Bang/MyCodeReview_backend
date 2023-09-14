#!/bin/bash

BASE_PATH=/home/ubuntu/build
BUILD_PATH=$(ls $BASE_PATH/*.jar)
JAR_FILE=$(basename "$BUILD_PATH")

echo ">>> BUILD_PATH:" "$BUILD_PATH"
echo ">>> JAR_FILE: $JAR_FILE"

echo ">>> 현재 구동중인 Port 확인"
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

echo ">>> IDLE_APPLICATION 종료 프로세스 시작"
if [ -z "$IDLE_PID" ]
then
  echo ">>> 현재 PID:$IDLE_PID 로 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo ">>> kill -15 $IDLE_PID"
  kill -15 "$IDLE_PID"
  echo ">>> Success kill IDLE_APPLICATION"
  echo ">>> sleep 5"
  sleep 5
fi

echo ">>> New Revision Application START in PORT:$IDLE_PORT"
APPLICATION_PATH=$BASE_PATH/$JAR_FILE
echo ">>> APPLICATION_PATH: " "$APPLICATION_PATH"
nohup java -jar -Dserver.port=$IDLE_PORT "$APPLICATION_PATH" > /dev/null 2> /dev/null < /dev/null &

echo ">>> Port:$IDLE_PORT Application 10초 후 Health Check 시작"
echo "curl -s http://localhost:$IDLE_PORT/port"
sleep 10


