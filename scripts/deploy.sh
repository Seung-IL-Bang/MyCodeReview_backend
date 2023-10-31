#!/bin/bash

echo "==========================START DEPLOY=========================="
DOCKER_COMPOSE_FILE=/home/ubuntu/docker-compose.yml # 인스턴스 초기화시 docker-compose.yml 다운

echo "==========================DOCKER CLIENT LOGIN=========================="
aws ecr-public get-login-password --region "${MY_REGION}" | docker login --username AWS --password-stdin "${ECR_URL}"

echo "==========================DOCKER PULL IMAGE=========================="
docker pull "${ECR_IMAGE}" # 인스턴스 초기화시 docker 설치  # 인스턴스 초기화시 ~/.bashrc 변수 입력 필요


CURRENT_PORT=$(curl -s http://localhost/port)


if [ "$CURRENT_PORT" == 8081 ]
then
  echo "==========================현재 구동중인 Port: $CURRENT_PORT=========================="
  IDLE_PORT=8082
  IDLE_SERVICE=app2
  NO_COMPOSE=0
elif [ "$CURRENT_PORT" == 8082 ]
then
  echo "==========================현재 구동중인 Port: $CURRENT_PORT=========================="
  IDLE_PORT=8081
  IDLE_SERVICE=app1
  NO_COMPOSE=0
else
  echo "==========================현재 구동중인 애플리케이션이 없습니다.=========================="
  NO_COMPOSE=1
  IDLE_PORT=8081
  IDLE_SERVICE=app1
fi


if [ "$NO_COMPOSE" == 1 ]
then
  echo "==========================기본 docker-compose.yml 구성으로 시작합니다.=========================="
  cd /home/ubuntu/
  docker-compose up -d # 인스턴스 초기화 시 docker-compose 설치
else
  NEW_IMAGE=${ECR_IMAGE}
  CURRENT_IMAGE=$(awk "/$IDLE_SERVICE:/,/image:/" $DOCKER_COMPOSE_FILE | grep "image:" | awk '{print $2}')
  sed -i "/$IDLE_SERVICE:/,/image:/s/image: $CURRENT_IMAGE/image: $NEW_IMAGE/" $DOCKER_COMPOSE_FILE # IDLE_SERVICE 컨테이너에 새로운 이미지 작성
  docker-compose up -d $IDLE_SERVICE # 새로운 이미지를 적용한 IDLE_SERVICE 컨테이너만 재시작
fi


echo "==========================<IDLE_SERVICE, IDLE_PORT>:<$IDLE_IDLE_SERVICE, $IDLE_PORT> Container 10초 후 Health Check 시작=========================="
sleep 10
echo "==========================START HEALTH CHECK=========================="
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

echo "==========================PROXY_PASS SWITCH=========================="
echo "set \$service_url http://$IDLE_SERVICE:$IDLE_PORT;" | sudo tee /home/ubuntu/app/nginx/service-url.inc
echo ">>> Nginx Proxy Port Before Reload:" "$CURRENT_PORT"

echo "==========================NGINX RELOAD=========================="
docker exec ubuntu-nginx-1 nginx -s reload # 인스턴스 초기화 시 docker 설치 필요

sleep 3
NEW_PROXY_PORT=$(curl -s http://localhost/port)
echo ">>> Nginx Proxy Port After Reload:" "$NEW_PROXY_PORT"

echo "==========================END DEPLOY=========================="