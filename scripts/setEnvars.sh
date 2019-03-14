#!/bin/bash

# The AppName exported by ensusmanos-core
export ESM_APP_NAME=ensusmanos

# The name of the subdomain under $ESM_DNS_NAME to run the API service on
export ESM_API_SERVICE_NAME=api

# Name of the Github branch for the CodePipeline to watch. Change this and re-run the
# deployBuildInfra script to create a pipeline on a new branch.
export ESM_ENV=$(git branch | grep \* | sed 's/* //')

# The name of the CloudFormation stack defining the build pipeline and ECR
export ESM_API_BUILD_STACK_NAME=$ESM_APP_NAME-$ESM_API_SERVICE_NAME-build-$ESM_ENV

# The cloudformation stack name for the API service
export ESM_API_SERVICE_STACK_NAME=$ESM_APP_NAME-$ESM_API_SERVICE_NAME-service-$ESM_ENV

# Port to run the API service on
export ESM_API_PORT=8080

# Should be true when running tests. Uses local resources instead of AWS resources.
export ESM_API_TEST=true

# When $ESM_API_TEST is true, the network host name of the test users DB. Should be 'localhost'
# if running the tests outside of the Docker test harness.
export ESM_API_TEST_USERS_DB_HOST_NAME=xxx

# Port where the test user's DB is accessible. Should be '8000' if running the tests in the Docker
# test harness.
export ESM_API_TEST_USERS_DB_PORT=8000

# Name of the ephemeral Docker network to create in the Docker test harness
export ESM_API_TEST_NET_NAME=xxx

# DynamoDb table name for the user's table
export ESM_API_USERS_DB_NAME=xxx

# ARN of a TLS certificate covering $ESM_DNS_NAME and *.$ESM_DNS_NAME
export ESM_TLS_CERTIFICATE_ARN=arn:aws:acm:us-east-2:054393190750:certificate/34bd2c46-b9e5-4492-bb7c-9833a0b5567f

