#!/bin/bash

export JONET_API_PORT=8080
export JONET_API_USERS_DB_NAME=xxx
export JONET_API_TEST=true
export JONET_API_TEST_USERS_DB_HOST_NAME=xxx
export JONET_API_TEST_USERS_DB_PORT=8000
export JONET_API_TEST_NET_NAME=xxx
export JONET_API_IMAGE_REGISTRY=054393190750.dkr.ecr.us-east-2.amazonaws.com/jonet-api

# The AWS variables are not used in tests, but cannot be null
export AWS_REGION=xxx
export AWS_ACCESS_KEY_ID=xxx
export AWS_SECRET_ACCESS_KEY=xxx

