#!/bin/bash

TABLES=$(docker exec -it db mysql -u root -ppwd -e 'show databases;' | awk '{ print $2}' | awk '{gsub(/\[Warning\]/, ""); print}' | grep -v '^Database' | grep -v '^information_schema'| grep -v '^mysql'| grep -v '^sys'| grep -v '^performance_schema')

for t in $TABLES
do
        echo "Backup $t Database ..."
      ./../skripte/ServerInstanzen/dockerExport.sh "$t"

done
