#!/bin/bash

docker exec -it tomee bash /usr/local/tomee/scripts/resetDB.sh
docker exec -it tomee bash /usr/local/tomee/scripts/createDummy.sh
