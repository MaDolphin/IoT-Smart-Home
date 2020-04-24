#!/bin/bash
# Dieses Skript Erzeugt Dummy-Werte in der MaCoCo-Instanz-Datenbank

# Überprüfen ob alle Argumente vorhanden sind.
if [ "$#" -ne 3 ]
then
    echo "Bitte setzen Sie folgende Argumente: 1. Name des Admins 2. passwort des Admins 3. Pfad der SAP Datei"
	exit 1
else
    echo "CSV Import Konten"
    echo
    USERNAME=$1
    PASSWORD=$2

    data='{"username":"'${USERNAME}'","password":"'${PASSWORD}'","resource":"TestDB"}'
    echo ${data}
    token=$(curl -X POST -H "Content-Type:application/json" http://localhost:8080/macoco-be/api/auth/login -d ${data}  | awk -F: '/\".*\"/{print $2}' | awk -F, '{print $1}' | tr -d '\"\"')
	echo $token
    result=$(curl -X POST -H "Content-Type:text/comma-separated-values; charset=UTF-8" -H "X-Auth:$token" --data-binary "@$3" http://localhost:8080/macoco-be/api/domain/csv/importkonto)
    echo $result
    echo "Done"
fi