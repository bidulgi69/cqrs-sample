confirmed=0

function wait() {
  echo "Wait for: $1... "
  local retry
  retry=0
  while [[ "$retry" -lt 20 ]]
  do
      status=$(curl -XGET "$1" -s | jq '.status')
      if [[ "$status" == "\"UP\"" ]]
      then
        echo "Server $2 is running."
        ((confirmed++))
        break
      fi
      sleep 2
      ((retry++))
  done
}

wait localhost:8080/actuator/health order-service
wait localhost:8081/actuator/health customer-service
wait localhost:8082/actuator/health restaurant-service
wait localhost:8084/actuator/health payment-service
wait localhost:8085/actuator/health kitchen-service
wait localhost:8086/actuator/health delivery-service
wait localhost:8087/actuator/health order-history-service

if [[ "$confirmed" == 7 ]]
then
  echo "All services are running now."
else
  echo "Some services does not running..."
fi