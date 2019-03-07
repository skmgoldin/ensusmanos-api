#!/bin/bash

export JONET_API_ENV=dev
export JONET_API_PORT=8080
export JONET_API_TEST=true
export JONET_API_TEST_USERS_DB_HOST_NAME=jonet_users_db
export JONET_API_TEST_USERS_DB_PORT=8000
export JONET_API_TEST_NET_NAME=jo_net
export AWS_REGION=us-east-2
export JONET_API_IMAGE_REGISTRY=054393190750.dkr.ecr.us-east-2.amazonaws.com/jonet-api-build-$JONET_API_ENV

