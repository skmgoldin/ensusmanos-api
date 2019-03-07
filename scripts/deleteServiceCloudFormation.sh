#!/bin/bash

aws cloudformation delete-stack --stack-name jonet-api-service-$JONET_API_ENV

