#!/bin/bash

while ! nc -z db 3306;
do
    echo sleeping;
    sleep 5;
done;
echo database connected over port 3306!;

while ! nc -z datasource 3306;
do
    echo sleeping;
    sleep 5;
done;
echo datasource connected over port 3306!;

while ! nc -z datasource 12777;
do
    echo sleeping;
    sleep 5;
done;
echo datasource resource reachable;

sh /usr/local/tomee/bin/receiveResourceXML.sh true

echo run tomee;
exec "$@";