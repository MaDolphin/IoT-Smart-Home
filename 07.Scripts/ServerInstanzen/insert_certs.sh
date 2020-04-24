#!/bin/bash

docker cp certs/cert.pem ssl:/etc/nginx/certs/cert.pem
docker cp certs/private.pem ssl:/etc/nginx/certs/key.pem
docker exec ssl /etc/init.d/nginx restart