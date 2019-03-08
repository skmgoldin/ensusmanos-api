#!/bin/bash

if [ $JONET_API_LATEST_IMAGE_TAG == "" ]
  then 
    echo "No image tag found"
    exit 1
fi

aws cloudformation create-stack \
  --stack-name $JONET_API_SERVICE_STACK_NAME \
  --template-body file://infra/service/Service.yaml \
  --capabilities CAPABILITY_IAM \
  --parameters ParameterKey=ImageUrl,ParameterValue=$JONET_API_IMAGE_REGISTRY:$JONET_API_LATEST_IMAGE_TAG
