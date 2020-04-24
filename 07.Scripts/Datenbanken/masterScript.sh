#!/bin/bash

exec_docker_db(){
    docker exec -i db /bin/sh -c "/usr/local/bin/createDBWithSchema.sh $1 $2 $3"
}

exec_docker_datasource(){
    docker exec -i datasource /bin/sh -c "/usr/local/bin/createInstanceDBEntry.sh $1 $2 $3 $4 $5 $6 $7"
}

copy_schema_from_tomee(){
    docker cp tomee:/usr/local/tomee/db-schema.sql db-schema.sql
    docker cp db-schema.sql db:/usr/local/bin/db-schema.sql
}

receive_resource_from_ds(){
    docker exec -i tomee /bin/sh -c "/usr/local/tomee/bin/receiveResourceXML.sh false"
}


restart_tomee_docker_container(){
    docker restart tomee
}

# $1 name of the database
# $2 human name for the database
# $3 ip of the database
# $4 username for the database
# $5 password for the user to access the database
# $6 admin of the new instance
# $7 email of the admin
echo "MasterScript"
if [ "$#" -ne 7 ]
then
    echo "Bitte setzen Sie folgende Argumente bei der Ausführung: 1. Name der Datenbank 2. Bezeichner der Datenbank 3. Ip der Datenbank 4. Name des Nutzers 5. Passwort des Nutzers 6. Name des Admins 7. Email des Admins"
        exit 1
else
    echo "Copy db-schema.sql from tomee to db"
    copy_schema_from_tomee
    echo "Create Database with schema"
    exec_docker_db $1 $4 $5
    echo "Create Instance DB entry"
    exec_docker_datasource $1 $2 $3 $4 $5 $6 $7
    # sh ./editResourceXML.sh $1 $4 $5
    echo "Receive resource.xml"
    receive_resource_from_ds
    echo "Restart tomee docker container"
    restart_tomee_docker_container
fi