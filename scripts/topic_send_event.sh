#!/bin/bash

docker exec -it broker kafka-console-producer --topic group-create-topic --bootstrap-server broker:9092
