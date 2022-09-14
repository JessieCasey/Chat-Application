
# &A

Chat application is built on Spring and MongoDB


## Installation

To get started you need to have docker installed on your machine

Then, you can run .yaml files not only directly in IDE, but also in terminal

```bash
  docker-compose -f docker-mongoDB.yaml up
```
```bash
  docker-compose -f docker-rabbitMQ.yaml up
```


## Run it

The project has a bootstrap class, which  includes all pre-data you needs to test the application




```java
@Component
public class Bootstrap {
  ...
}
```

The server runs on 9091 port, but if have any conflicts, you can change it

```java
#application.properties
server.port=9091

#main.js
let port = 9091;
```