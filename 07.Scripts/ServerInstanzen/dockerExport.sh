#!/bin/bash

docker_name="db"

# create sql backup
(docker exec -i $docker_name mysqldump -u root -ppwd testdb) > db_backup.sql