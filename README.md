# CQRS-sample
Sample project implementing CQRS pattern.

## What is CQRS pattern?
In a microservice application, it is difficult to implement a query that retrieves data that spans multiple services.<br>
If you don't have a lot of services involved, it's also a good choice to configure with the API Composition pattern.
It is to define a query in each of the services involved, combine data in one service and return a value.

However, the API Composition pattern has the following disadvantages.

1. Increased Overhead
<br>An increase in overhead is unavoidable because multiple services and DBs are called multiple times. As network resource usage increases, operating costs will increase.<br><br>
2. Poor Availability
<br>If the three services each have an availability of 99.5%, when all services are combined, the availability is 99.5%^3=98.5%. Availability will be lower if more services are combined.<br><br>
3. Lack of Data Consistency
<br>Because ACID transactions are not guaranteed, the data queried by each service may be inconsistent.<br>

Command Query Responsibility Segregation (CQRS), as its name suggests, is the segregation of responsibility for processing queries from the responsibility of processing commands in the system.<br>
That is, the query (`R, Http GET`) function is implemented in the query-side module, and the create/update/delete (`CUD, Http POST PUT DELETE`) function is implemented in the command-side module.<br>
Data synchronization between the two modules is typically done in a way that the command-side module issues an event and the query-side module subscribes to the event. 

The advantages of applying the CQRS pattern are:

1. Efficient queries are available.
<br>Stores pre-joined views of data from multiple services, there is no need for expensive in-memory joins like API Composition pattern.
   There are also cases where it is difficult or impossible to implement different kinds of queries with only a single data model. The CQRS pattern allows you to overcome the limitations of a single data model (storage) by defining a data view that fits each query.<br><br>
2. Separation of interests simplifies the management of each module.
<br>CQRS pattern defines the DB schema for each interest in the query-side module and the command-side module. In addition, the service that implements the query may differ from the service that owns the data.<br>

But there are also disadvantages of CQRS.

1. Complex Architecture
2. Handling replication lag
<br>The lag between the command-side module and the query-side module must be handled. 
This is the gap before the event issued by the command-side is consumed and processed by the query-side module.
You must ensure that inconsistent data is not exposed to the user as much as possible.<br>


## Event flow
<img src="https://user-images.githubusercontent.com/17774927/187399789-9593b568-124f-4f22-92f8-dc628cf28197.png" alt="event flow">
<br>

1. Create pending order and send a `ORDER_CREATED` event to `order-customer` topic that customer service subscribes to.
2. Customer service receives an order aggregate and verify the value of `customerId` field is valid. Sends a `CUSTOMER_APPROVED` event if it is a valid value, or a `CUSTOMER_REJECTED` event if it is not valid.
3. If a message from `order-reply` topic tells `customerId` is verified, then send a `ORDER_CREATED` event to `order-ticket` topic that restaurant service subscribes to.
4. Restaurant service receives an order aggregate and creates a pending ticket if available. Sends `TICKET_REJECTED` if ticket creation fails, `TICKET_CREATED` event if successful.
5. If a message from `order-reply` topic tells ticket is created successfully, then send `ORDER_CREATED` event to `order-payment` topic that payment service subscribes to.
6. Payment service receives an order aggregate and approve payment if available. Sends `PAYMENT_REJECETED` if payment approval fails, `PAYMENT_APPROVED` event if successful. 
- If a message from `order-reply` topic tells payment is approved, then order service sends `ORDER_APPROVED` event to restaurant service.
- Restaurant service receives an order aggregate and change the state of ticket to `ACCEPTED` and send `TICKET_APPROVED` event.
7. If a message from `order-reply` topic tells ticket is accepted, then send a `ORDER_APPROVED` event to `order-kitchen` topic that kitchen service subscribes to.
8. Kitchen service receives an order aggregate and creates a `PREPARING` cook entity. Sends a `KITCHEN_PREPARING` event.
- If a message from `order-reply` topic tells that foods are cooking, change the state of ticket and order to `PREPARING`.
- State change proceeds in the same way as steps 6 and 7.
- After cooking has been finished, API `/kitchen/ready` will be called and send a `KITCHEN_READY` event.
- State fields of order and ticket are changed to `READY` and it will proceed in the same way as steps 6 and 7.
9. After a delivery man picks up the foods, API `/delivery/pickedup` will be called and send `DELIVERY_PICKEDUP` event.
- State fields of order and ticket are changed to `PICKEDUP` and it will proceed in the same way as steps 6 and 7.
- After a delivery man completes delivery, API `/delivery/complete` will be called and send `DELIVERY_COMPLETE` event.
- State fields of order and ticket are changed to `COMPLETE` and it will proceed in the same way as steps 6 and 7.

<br>
<img src="https://user-images.githubusercontent.com/17774927/187403981-4da74d79-ec0f-4577-b454-8f84e0a5ee84.png" alt="outbox pattern">

Use SERVICE-DB-Message Relay-Kafka transfer flow, not SERVICE to Kafka direct transfer.<br>
Therefore, the act of sending a message to the topic in the above description is that the message stored in the `OUTBOX` table by the service was read by the message relay and sent to the Kafka.

## State machine diagram
<img src="https://user-images.githubusercontent.com/17774927/187406991-5a3da84f-9805-4fe1-b6fe-0b76295da921.png" alt="state machine diagram">
<br>

## Usage
1. Build applications and docker images

        make compile
2. Launch applications and Wait until the servers to run (<a href="https://github.com/stedolan/jq">jq</a> required)
    
        make run
3. Testing event flows (<a href="https://github.com/stedolan/jq">jq</a> required)

        make test
4. Cleanup

        make clean

<br>

### Issues
Library issues that occurred during development have been summarized in ISSUE.md.