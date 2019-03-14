#!/bin/bash

./scripts/publishInfraTemplates.sh

AppName=$(aws cloudformation list-exports | \
  jq -cj '.[] | .[] | select(contains({Name: "ensusmanos-AppName"})) | .Value')

# TODO: Add an -$ENV string to the end of the stack-name
aws cloudformation create-stack \
  --stack-name $ESM_API_BUILD_STACK_NAME \
  --template-body file://infra/build/Build.yaml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=AppName,ParameterValue=$AppName \
    ParameterKey=GithubUser,ParameterValue=skmgoldin \
    ParameterKey=GithubRepoName,ParameterValue=ensusmanos-api \
    ParameterKey=GithubRepoBranch,ParameterValue=$ESM_ENV \
    ParameterKey=GithubToken,ParameterValue=$GITHUB_TOKEN \
    ParameterKey=ServiceStackName,ParameterValue=$ESM_API_SERVICE_STACK_NAME \
    ParameterKey=ServiceName,ParameterValue=$ESM_API_SERVICE_NAME \
    ParameterKey=ServiceEnv,ParameterValue=$ESM_ENV

