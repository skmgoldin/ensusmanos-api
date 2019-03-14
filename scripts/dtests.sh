#!/bin/bash

exitCode=0
imageName=esm-test-image

echo creating docker network...
docker network create \
  --driver bridge \
  $ESM_API_TEST_NET_NAME

echo building image...
docker build \
  -t $imageName \
  .

echo running test db...
docker run \
  --name $ESM_API_TEST_USERS_DB_HOST_NAME \
  --network $ESM_API_TEST_NET_NAME \
  amazon/dynamodb-local \
  > /dev/null 2>&1 &

echo running tests...
docker run \
  -e ESM_API_PORT=$ESM_API_PORT \
  -e ESM_API_USERS_DB_NAME=$ESM_API_USERS_DB_NAME \
  -e ESM_API_TEST=$ESM_API_TEST \
  -e ESM_API_TEST_USERS_DB_HOST_NAME=$ESM_API_TEST_USERS_DB_HOST_NAME \
  -e ESM_API_TEST_USERS_DB_PORT=$ESM_API_TEST_USERS_DB_PORT \
  -e AWS_REGION=xxx \
  -e AWS_ACCESS_KEY_ID=xxx \
  -e AWS_SECRET_ACCESS_KEY=xxx \
  -p $ESM_API_PORT:$ESM_API_PORT \
  --network $ESM_API_TEST_NET_NAME \
  $imageName ./gradlew build

if [ $? != 0 ]
  then
    exitCode=1
fi

echo removing test db...
docker kill \
  $ESM_API_TEST_USERS_DB_HOST_NAME

echo removing stale resources...
docker system prune -f

exit $exitCode
