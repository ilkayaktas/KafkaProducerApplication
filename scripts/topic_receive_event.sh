#!/bin/bash


docker exec -it broker bash

kafka-console-consumer --topic group-create-topic \
 --bootstrap-server broker:9092 \
 --from-beginning \
 --property print.key=true \
 --property key.separator=" : "