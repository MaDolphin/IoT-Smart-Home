#!/bin/bash

docker_name="db"

# import sql backup
docker exec -i $docker_name mysql -u root -ppwd -e "CREATE DATABASE IF NOT EXISTS testdb"
(docker exec -i $docker_name mysql -u root -ppwd testdb) < db_backup.sql