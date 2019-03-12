#!/bin/bash

aws cloudformation execute-change-set \
  --change-set-name $JONET_API_SERVICE_STACK_NAME-changeset \
  --stack-name $JONET_API_SERVICE_STACK_NAME

