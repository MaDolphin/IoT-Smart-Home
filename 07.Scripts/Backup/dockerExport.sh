#!/bin/bash

if [ "$#" -ne 1 ]
then
  echo "usage: $0 dbname"
  exit 1
fi

dbname="$1"
docker_name="db"

# create sql backup
filename="eis_db_backup_${dbname}_$(date +%Y%m%d_%H%M%S).sql"
(docker exec -t $docker_name mysqldump -u root -ppwd $dbname) > $filename
echo -e "$filename"

