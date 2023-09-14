echo ">>> Start ValidateService"

CURRENT_PORT=$(curl -s http://localhost/port)

if [ "$CURRENT_PORT" == 8081 ]
then
  IDLE_PORT=8082
elif [ "$CURRENT_PORT" == 8082 ]
then
  IDLE_PORT=8081
else
  IDLE_PORT=8081
fi

for retry_count in {1..10}
do
  response=$(curl -s http://localhost:$IDLE_PORT/port)
  count=$(echo "$response" | grep $IDLE_PORT | wc -l)

  if [ "$count" -ge 1 ]
  then
    echo ">>> Health Check 성공"
    break
  else
    echo ">>> Health Check 의 응답을 알 수 없습니다."
  fi

  if [ "$retry_count" -eq 10 ]
  then
    echo ">>> Health check 실패. "
    echo ">>> Nginx 에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo ">>> Health Check 연결 실패. 10초 후에 재시도 합니다."
  sleep 10
done

echo ">>> Application Port Switch 프로세스를 시작합니다."
echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" |sudo tee /etc/nginx/conf.d/service-url.inc

echo ">>> Nginx Current Proxy Port:" "$CURRENT_PORT"

echo ">>> Nginx Reload"
sudo service nginx reload

sleep 5
NEW_PROXY_PORT=$(curl -s http://localhost/port)
echo ">>> Nginx Reload Proxy Port:" "$NEW_PROXY_PORT"


echo ">>> End ValidateService"