#!/bin/bash

function compile {
    clean
    mvn package -DskipTests
}

function start {
    touch tugraph_db_management_ut.db
    nohup java -jar target/tugraph-db-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=ut > log.txt 2>&1 &
    echo $! > pidfile.txt
}

function stop {
    kill -9 `cat pidfile.txt`
    # kill -9 `ps -ef | grep spring.profiles.active=ut | grep -v "grep" | awk '{print $2}'`
    rm -f pidfile.txt log.txt tugraph_db_management_ut.db
}

function clean {
    mvn clean
}

$1