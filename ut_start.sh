#!/bin/bash
nohup mvn spring-boot:run > log.txt 2>&1 & echo $! > pidfile.txt