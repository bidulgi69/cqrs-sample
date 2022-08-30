HOST=localhost
ORDER_COMMAND_PORT=8080
ORDER_QUERY_PORT=8087
KITCHEN_PORT=8085
DELIVERY_PORT=8086
orderId=1

function assertCurl() {

  local expectedHttpStatus=$1
  local commands="$2 -w \"%{http_code}\""
  local result
  result=$(eval "$commands")
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpStatus" ]
  then
    echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
  else
    echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpStatus, GOT: $httpCode"
    exit 1
  fi
}

function assertOrderState() {
  echo "Wait till the event consumed.."
  sleep 6

  local expectedOrderState="$1"
  local result
  result=$(curl -XGET $HOST:$ORDER_QUERY_PORT/history/$orderId -s | jq '.state')

  if [[ "$result" == "$expectedOrderState" ]]
  then
    echo "Test OK, VERIFIED $expectedOrderState"
  else
    echo "Test FAILED, EXPECTED $expectedOrderState, GOT: $result"
    exit 1
  fi
}

# check order entity is created.
assertCurl 200 "curl -X POST -H 'Content-Type: application/json' $HOST:$ORDER_COMMAND_PORT/order -d @scripts/order.json -s"
assertOrderState "\"PREPARING\""

# Trigger KITCHEN_READY event.
assertCurl 200 "curl -X GET $HOST:$KITCHEN_PORT/kitchen/ready -s"
assertOrderState "\"READY\""

# Trigger DELIVERY_PICKEDUP event.
assertCurl 200 "curl -X GET $HOST:$DELIVERY_PORT/delivery/pickedup -s"
assertOrderState "\"PICKEDUP\""

# Trigger DELIVERY_COMPLETED event.
assertCurl 200 "curl -X GET $HOST:$DELIVERY_PORT/delivery/complete -s"
assertOrderState "\"COMPLETED\""