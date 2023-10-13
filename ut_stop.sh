#!/bin/bash
kill -9 `cat pidfile.txt`
rm -f pidfile.txt
tm -f log.txt
