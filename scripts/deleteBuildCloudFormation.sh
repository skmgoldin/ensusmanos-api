#!/bin/bash

aws cloudformation delete-stack --stack-name jonet-api-build-$JONET_API_ENV

