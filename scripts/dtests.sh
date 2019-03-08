#!/bin/bash

echo creating docker network...
docker network create \
  --driver bridge \
  $JONET_API_TEST_NET_NAME

echo building image...
docker build \
  -t $JONET_API_IMAGE_REGISTRY \
  .

echo running test db...
docker run \
  --name $JONET_API_TEST_USERS_DB_HOST_NAME \
  --network $JONET_API_TEST_NET_NAME \
  amazon/dynamodb-local \
  > /dev/null 2>&1 &

echo running tests...
docker run \
  -e JONET_API_PORT=$JONET_API_PORT \
  -e JONET_API_USERS_DB_NAME=$JONET_API_USERS_DB_NAME \
  -e JONET_API_TEST=$JONET_API_TEST \
  -e JONET_API_TEST_USERS_DB_HOST_NAME=$JONET_API_TEST_USERS_DB_HOST_NAME \
  -e JONET_API_TEST_USERS_DB_PORT=$JONET_API_TEST_USERS_DB_PORT \
  -e AWS_REGION=xxx \
  -e AWS_ACCESS_KEY_ID=xxx \
  -e AWS_SECRET_ACCESS_KEY=xxx \
  -p $JONET_API_PORT:$JONET_API_PORT \
  --network $JONET_API_TEST_NET_NAME \
  $JONET_API_IMAGE_REGISTRY ./gradlew build

echo removing test db...
docker kill \
  $JONET_API_TEST_USERS_DB_HOST_NAME

echo removing stale resources...
docker system prune -f

