#!/bin/bash

export JONET_API_PORT=8080
export JONET_API_USERS_DB_NAME=xxx
export JONET_API_TEST=true
export JONET_API_TEST_USERS_DB_HOST_NAME=xxx
export JONET_API_TEST_USERS_DB_PORT=8000
export JONET_API_TEST_NET_NAME=xxx
export JONET_API_IMAGE_REGISTRY=054393190750.dkr.ecr.us-east-2.amazonaws.com/jonet-api
export JONET_API_BUILD_STACK_NAME=jonet-api-build
export JONET_API_SERVICE_STACK_NAME=jonet-api-service
export JONET_API_APP_NAME=jonet-api
export JONET_API_SERVICE_DNS_PREPEND=api

export JONET_TLS_CERTIFICATE_ARN=arn:aws:acm:us-east-2:054393190750:certificate/34bd2c46-b9e5-4492-bb7c-9833a0b5567f
export JONET_DNS_NAME=ensusmanos.net

export AWS_REGION=us-east-2

