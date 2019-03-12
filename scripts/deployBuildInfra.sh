#!/bin/bash

./scripts/publishInfraTemplates.sh

aws cloudformation create-stack \
  --stack-name $JONET_API_BUILD_STACK_NAME \
  --template-body file://infra/build/Build.yaml \
  --parameters ParameterKey=RepositoryName,ParameterValue=$JONET_API_APP_NAME

