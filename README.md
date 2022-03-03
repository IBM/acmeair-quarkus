# Acme Air MicroServices with Quarkus

This is a Quarkus implementation of Acme Air, a sample microservice application that emulates an air ticketing system.
This is ported from its [SpringBoot implementation](https://github.com/blueperf/acmeair-mainservice-springboot).
The components and their connections are depicted
[here](https://github.com/blueperf/acmeair-mainservice-java/blob/main/images/AcmeairMS.png).

Quarkus is a cloud native Java framework based on modern standard APIs.
More information about its fancy features are available at [https://quarkus.io/](https://quarkus.io/).

This repository includes all five services of Acme Air, though the original five repositories are separated in
the [BLUEPERF](https://github.com/blueperf) GitHub organization.

This repository also includes configuration files to run the application and its load generator in containers
on a local machine or OpneShift cloud environment.

## Build Instructions

##### Clone git repo
```shell
git clone https://github.com/IBM/acmeair-quarkus.git
```

##### Building the application
```shell
cd acmeair-quarkus
mvn package
```

This step builds all five services, and creates docker images of the services and other services like mongodb.
It also creates a docker image of the driver, a load generator by using Apache JMeter scenario.
The scenario and its JMeter plugin are found in [acmeair-driver](https://github.com/blueperf/acmeair-driver)
repository in BLUEPERF.

##### Creating a native executable if interested

One of a major feature of Quarkus is native binary support. You can build the five microservices as native binaries
by adding `-Pnative` in the Maven command line:
```shell
mvn package -Pnative
```

You can distinguish whether an image is Java version or native version
by checking its image tag.  For example, `acmeair-mainservice-quarkus:jvm` is a Java version,
and `acmeair-mainservice-quarkus:native` is a native version.

## Usage Instructions

##### Setting up docker network

Make sure you correctly installed docker, docker-compose, and start docker daemon on your local machine.
The instruction is available [here](https://docs.docker.com/installation/).

Then, create docker network for connecting the microservices and other components:
```shell
docker network create --driver bridge my-net
```
You can pick any name for the network. `my-net` is just an example.

##### Starting the application

Use docker-compose to start containers of all five microservices, three mongodb instances, and an nginx proxy:
```shell
NETWORK=my-net docker-compose -f acmeair-mainservice-quarkus/docker-compose.yml up
```

##### Verifying the application manually

1. Access http://localhost/acmeair with your browser.
2. Click **a link at the bottom of the page** and go to the **Acme Air Configuration information** page.
3. Press the **Load the database** button in the ___Actions:___ bar to load the database for three services.

The application is ready if no error is reported.

##### Measuring performance using the driver

The instruction to use driver is described in [driver/Readme.md](driver/Readme.md).
