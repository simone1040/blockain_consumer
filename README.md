# Receiver role of blockchain system - Spring boot + RabbitMQ + Docker 

Blochain simulation receiver 

## Getting Started
These instructions help you to start receiver role of blockchain.

### Prerequisities
In order to run this project you'll need: 

**docker** 

* [Windows](https://docs.docker.com/windows/started)
* [OS X](https://docs.docker.com/mac/started/)
* [Linux](https://docs.docker.com/linux/started/)

**JAVA**

*[JAVA](https://www.java.com/it/download/)

### Usage

In order to run this exaple project:
* you must update **/src/main/resources/receiver.conf** with the address where you install rabbitmq-management.

* run **mvn clean package** in the root of project.

* **docker build -t receiver** in the root of project in order to create image of receiver role.

* **docker stack deploy -c docker-compose.yml receiver_stack** in the root of project in order to run this example. docker-compose starts rabbitMQ management and two replicas of receiver code.

* for checking services status you can use **docker service ls**, while for remove services you can use **docker service rm #SERVICE_NAME**.

* you can check application log inside container under /tmp/ReceiverBlockchain folder, for entering in container you can use this command **docker exec -it #ID_CONTAINER /bin/bash**.

* for testing receiver application you must download Sender_rabbitmq project of my repository.

### Example
![prova_chain](https://user-images.githubusercontent.com/43130988/106159945-9f888100-6185-11eb-86a6-cf34a4d116c3.png)
