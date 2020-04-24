
#!/bin/bash

create_new_database_entry(){
# Create user for the new database
mysql -u admin -ppass datasource <<Create_Entry
insert into datasource.DataSource (dbname, bezeichnung, ip, username, password, aname, amail) values ('$1','$2','$3','$4','$5','$6','$7')
Create_Entry
}


# $1 name of the database
# $2 human name for the database
# $3 ip of the database
# $4 username for the database
# $5 password for the user to access the database
# $6 admin of the new instance
# $7 email of the admin
echo "Skript zur Erzeugung eines Datasource Datenbankeintrags"
if [ "$#" -ne 7 ]
then
    echo "Bitte setzen Sie folgende Argumente bei der Ausführung: 1. Name der Datenbank 2. Bezeichner der Datenbank 3. Ip der Datenbank 4. Name des Nutzers 5. Passwort des Nutzers 6. Name des Admins 7. Email des Admins"
        exit 1
else
    create_new_database_entry $1 $2 $3 $4 $5 $6 $7
fi
