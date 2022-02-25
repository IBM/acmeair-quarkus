# AcmeAir MicroServices Quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

### Usage Instructions

##### Clone Git Repos

    git clone https://github.ibm.com/trl-quarkus/acmeair-quarkus.git

##### Building the application
  
      cd acmeair-quarkus
      mvn -DskipTests clean package
    
## Docker Instructions

Prereq: [Install Docker, docker-compose, and start Docker daemon on your local machine](https://docs.docker.com/installation/)

1. Create a symbolic link to Dockerfile in each repository
 * for d in acmeair-*-quarkus; do (cd $d; ln -s src/main/docker/Dockerfile.jvm Dockerfile); done
2. cd acmeair-mainservice-quarkus
3. Create docker network
 * docker network create --driver bridge my-net
4. Build/Start Containers. This will build all the micro-services, mongo db instances, and an nginx proxy.
    * docker-compose build
    * NETWORK=my-net docker-compose up

5. Go to http://docker_machine_ip/acmeair
6. Go to the Configuration Page and Load the Database

## Creating a native executable

Although we have not tested native execution of the services, you can try it by building a native binary
for each repository and re-creating docker images for the native binary after switching the symbolic link of Dockefile 
to `src/main/docker/Dockerfile.native`.

You can create a native executable using: 
```shell script
./mvnw -DskipTests package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw -DskipTests package -Pnative -Dquarkus.native.container-build=true
```

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.
