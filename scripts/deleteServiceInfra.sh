#!/bin/bash

aws cloudformation delete-stack \
  --stack-name $JONET_API_SERVICE_STACK_NAME

