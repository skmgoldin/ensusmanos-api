#!/bin/bash

bucket=$(aws cloudformation list-exports | \
  jq -cj '.[] | .[] | select(contains({Name: "ensusmanos-ApplicationUtilityBucketName"})) | .Value')

aws s3 cp \
  --recursive \
  --exclude *.swp \
  infra/build \
  s3://$bucket/$ESM_API_SERVICE_NAME/$ESM_ENV/build-infra/

