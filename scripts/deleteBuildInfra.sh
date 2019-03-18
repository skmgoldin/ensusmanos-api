#!/bin/bash

ecrName=$(aws cloudformation list-exports | \
  jq -cj --arg searchValue $ESM_APP_NAME:$ESM_API_SERVICE_NAME:$ESM_ENV:EcrRepositoryName '.[] | .[] | select(contains({Name: $searchValue})) | .Value')

echo deleting ecr $ecrName
aws ecr batch-delete-image \
  --repository-name $ecrName \
  --image-ids \
    $(aws ecr describe-images --repository-name $ecrName | \
      jq -c -j '.[] | .[] | .imageDigest' | \
      sed 's/sha256:/imageDigest=sha256:/g' | \
      sed 's/image/ image/g')

echo deleting associated service stack $ESM_APP_NAME-$ESM_API_SERVICE_NAME-$ESM_ENV
aws cloudformation delete-stack \
  --stack-name $ESM_API_SERVICE_STACK_NAME \
  --role-arn arn:aws:iam::054393190750:role/CloudFormationReaper

echo deleting build stack $ESM_API_BUILD_STACK_NAME
aws cloudformation delete-stack \
  --stack-name $ESM_API_BUILD_STACK_NAME \
  --role-arn arn:aws:iam::054393190750:role/CloudFormationReaper

