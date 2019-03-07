#!/bin/bash

aws cloudformation create-stack --stack-name jonet-api-serive-$JONET_API_ENV --template-body file://ApplicationCloudFormation.yaml --parameters ParameterKey=Env,ParameterValue=$JONET_API_ENV ParameterKey=ImageRegistry,ParameterValue=$JONET_API_IMAGE_REGISTRY ParameterKey=ImageTag,ParameterValue=$JONET_API_LATEST_IMAGE_TAG

