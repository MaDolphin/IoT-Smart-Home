#!/bin/bash
# Überprüfung ob alle benötigten Argumente vorhanden sind
if [ "$#" -ne 3 ]
then
    echo "Bitte setzen Sie folgende Argumente: 1. Name des Admins 2. Email des Admins 3. Domäne unter der das System erreichbar ist"
	exit 1
else
# Docker installieren und konfigurieren
apt-get update
apt-get -y install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common	
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"   
apt-get update
apt-get -y install docker-ce
groupadd docker
systemctl daemon-reload
service docker restart
# Einloggen beim MaCoCo Docker Nexus
docker login docker.se.rwth-aachen.de:5000 --username="macoco_installer" --password="macoco_installer"
# Bisherige Container stoppen und entfernen
docker ps -a -q | xargs -n 1 -P 8 -I {} bash -c "docker stop {} && docker rm {}"
# Aktuelle Docker Images aus dem Nexus holen
docker pull docker.se.rwth-aachen.de:5000/repository/macoco/backend:0.0.1
docker pull docker.se.rwth-aachen.de:5000/repository/macoco/mysql:0.0.1
docker pull docker.se.rwth-aachen.de:5000/repository/macoco/ssl:0.0.1
# Container deployen und Argumente des Scripts als Umgebungsvariablen für den Tomee container setzen
docker run -d -it --name "db" docker.se.rwth-aachen.de:5000/repository/macoco/mysql:0.0.1
docker run -d -it  --name "tomee" --link db -e "ADMIN_USERNAME=$1" -e "ADMIN_EMAIL=$2" -e "SERVER_DOMAIN=$3" docker.se.rwth-aachen.de:5000/repository/macoco/backend:0.0.1
docker run -d -it -p 443:443 -p 80:80 --name ssl --link tomee docker.se.rwth-aachen.de:5000/repository/macoco/ssl:0.0.1
fi