#!/bin/bash
nohup mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=ut" > log.txt 2>&1 & echo $! > pidfile.txt
touch tugraph_db_management_ut.db

