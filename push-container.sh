#!/bin/bash

echo "$2" | docker login docker.pkg.github.com -u "$1" --password-stdin

IMAGE_NAME=race-announcer
IMAGE_ID=docker.pkg.github.com/scaramangado/rtgg-race-announcer/$IMAGE_NAME

docker build . --tag $IMAGE_NAME
docker tag $IMAGE_NAME $IMAGE_ID:latest
docker push $IMAGE_ID:latest

docker logout
