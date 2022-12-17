#!/bin/sh

(cd deployment && sudo docker-compose down)

git pull
(cd deployment \
  && sudo docker-compose pull \
  && sudo docker-compose up -d)
