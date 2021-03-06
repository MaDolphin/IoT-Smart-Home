#!/bin/bash

# Überprüfung ob alle benötigten Argumente vorhanden sind:
if [ "$#" -lt 3 ]
then
    echo "Bitte setzen Sie folgende Argumente: 1. Name des Admins 2. Email des Admins 3. Domäne unter der das System erreichbar ist"
	exit 1
elif [ "$#" -eq 3 ] # THREE ARGUMENTS: no Version - Deploy Latest
then
    # Container deployen und Argumente des Scripts als Umgebungsvariablen für den Tomee container setzen
    docker run -d -it --restart always --name "tomee" --link db -e "ADMIN_USERNAME=$1" -e "ADMIN_EMAIL=$2" -e "SERVER_DOMAIN=$3" implementation_tomee:latest
    docker run -d -it -p 443:443 -p 80:80 --restart always --name ssl --link tomee implementation_ssl:latest

# FOUR ARGUMENTS: Depoy Version x
elif [ "$#" -eq 4 ]
then
    VERSION=$4
    # Container deployen und Argumente des Scripts als Umgebungsvariablen für den Tomee container setzen
    docker run -d -it --restart always --name "tomee" --link db -e "ADMIN_USERNAME=$1" -e "ADMIN_EMAIL=$2" -e "SERVER_DOMAIN=$3" implementation_tomee:${VERSION}
    docker run -d -it -p 443:443 -p 80:80 --restart always --name ssl --link tomee implementation_ssl:${VERSION}
else
    echo "Bitte setzen Sie folgende Argumente: 1. Name des Admins 2. Email des Admins 3. Domäne unter der das System erreichbar ist"
	exit 1
fi