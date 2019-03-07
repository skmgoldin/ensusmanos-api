#!/bin/bash

aws cloudformation create-stack --stack-name jonet-api-build-$JONET_API_ENV --template-body file://BuildCloudFormation.yaml --parameters ParameterKey=Stack,ParameterValue=jonet-api-build ParameterKey=Postfix,ParameterValue=$JONET_API_ENV

