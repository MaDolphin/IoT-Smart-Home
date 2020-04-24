#!/bin/bash

version=0.0.1

if [ "$#" -gt 0 ]
then
        version=$1
fi

docker stop ssl
docker rm ssl
docker stop tomee
docker rm tomee


NAME="erc_eis"

docker pull docker.se.rwth-aachen.de:5000/repository/macoco/${NAME}_tomee:$version
docker pull docker.se.rwth-aachen.de:5000/repository/macoco/${NAME}_ssl:$version

docker tag docker.se.rwth-aachen.de:5000/repository/macoco/${NAME}_tomee:$version docker.se.rwth-aachen.de:5000/repository/macoco/${NAME}_tomee:latest
docker tag docker.se.rwth-aachen.de:5000/repository/macoco/${NAME}_ssl:$version docker.se.rwth-aachen.de:5000/repository/macoco/${NAME}_ssl:latest

# start the server
bash startTomeeSSLContainer.sh admin admin@email https://eis.se.rwth-aachen.de $version
