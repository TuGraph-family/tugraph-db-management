#!/bin/bash
kill -9 `cat pidfile.txt`
rm -f pidfile.txt
rm -f log.txt
rm -f tugraph_db_management_ut.db
