#!/bin/bash

create_new_database(){
# Create new database
mysql -u root -ppwd <<Create_DB_Query
create database $1
Create_DB_Query
}

import_schema_into_database(){
    if [ ! -e db-schema.sql ]
    then
        echo "Schema der Datenbank existiert nicht"
        exit 1
    fi
        # Import schema into new database
        mysql -u root -ppwd $1 < db-schema.sql
}

create_user_for_db(){
echo "Create user '$1' for the new database"
mysql -u root -ppwd <<Create_User
create user '$1'@'%' identified by '$2'
Create_User
}

grant_access_to_db(){
echo "Grant new user '$2' access to new database '$1'"
mysql -u root -ppwd <<Grant_Access
grant all on $1.* to '$2'@'%'
Grant_Access
}

check_database_name(){
        if mysql -u root -ppwd -e "use $1"
        then
                echo "Die Datenbank '$1' existiert bereits im System"
                exit 1
        fi
}

check_user_name(){
        RESULT="$(mysql -u root -ppwd -sse "SELECT EXISTS(SELECT 1 FROM mysql.user WHERE user = '$1')")"
        if      [ $RESULT != 0 ]
        then
                echo "Der Benutzer '$1' existiert bereits im System"
                exit 1
        fi
}

# $1 name of the database
# $2 username for the database
# $3 password for the user to access the database
echo "Skript zur Erzeugung einer neuen Datenbank"
if [ "$#" -ne 3 ]
then
    echo "Bitte setzen Sie folgende Argumente bei der Skript Ausführung: 1. Name der Datenbank 2. Name des Nutzers 3. Passwort des Nutzers"
        exit 1
else
    resource=$(echo "$1" | sed -e 's/\(.*\)/\L\1/')
    check_database_name $resource
    check_user_name $2

    create_new_database $resource
    import_schema_into_database $resource

    create_user_for_db $2 $3
    grant_access_to_db $resource $2

fi
