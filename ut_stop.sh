#!/bin/bash
kill -9 `cat pidfile.txt`
kill -9 `ps -ef | grep spring.profiles.active=ut | grep -v "grep" | awk '{print $2}'`
rm -f pidfile.txt
rm -f log.txt
rm -f tugraph_db_management_ut.db
