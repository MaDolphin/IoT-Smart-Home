#!/bin/bash
#if ! grep -q "nexus.se.rwth-aachen.de" ~/.docker/config.json; then
#    echo "not logged in"
#    exit 1
#fi

# Überprüfung ob alle benötigten Argumente vorhanden sind
if [ "$#" -ne 1 ]
then
    echo "$0 VERSION"
	exit 1
else
    echo "Version: $1"
    
    VERSION=$1
    NAME="iot-lab"
    docker-compose -p ${NAME} build
    
    docker tag ${NAME}_tomee nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_tomee:$VERSION
    docker tag ${NAME}_db nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_mysql:$VERSION
    docker tag ${NAME}_datasource nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_datasource:$VERSION
    docker tag ${NAME}_ssl nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_ssl:$VERSION
    
    docker push nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_tomee:$VERSION
    docker push nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_mysql:$VERSION
    docker push nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_datasource:$VERSION
    docker push nexus.se.rwth-aachen.de:5002/repository/macoco-snapshots/${NAME}_ssl:$VERSION
fi
