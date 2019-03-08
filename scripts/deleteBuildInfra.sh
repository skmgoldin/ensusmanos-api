#!/bin/bash

aws cloudformation delete-stack \
  --stack-name $JONET_API_BUILD_STACK_NAME

