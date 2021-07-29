#!/bin/bash

./gradlew build

containerId=$(docker stop kafkaproducer)

# Remove container
docker rm $containerId

# Remove image
docker rmi ilkayaktas/kafkaproducer

# Build image
docker build --build-arg JAR_FILE=build/libs/\*.jar -t ilkayaktas/kafkaproducer .


# Run container
docker run --name kafkaproducer --restart=unless-stopped --net mcc-network ilkayaktas/kafkaproducer
