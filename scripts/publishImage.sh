#!/bin/bash

# Authenticate docker with the ECR registry
$(aws ecr get-login --region $AWS_REGION --no-include-email)

docker build -t $JONET_API_IMAGE_REGISTRY .

docker image rm $JONET_API_IMAGE_REGISTRY:$JONET_API_LATEST_IMAGE_TAG

# Tag the latest jonet/api as 054393190750.dkr.ecr.us-east-2.amazonaws.com/jonet-api-build-
latestTag="latest"
export JONET_API_LATEST_IMAGE_TAG="$(date +%Z-%Y.%m.%d-%H.%M.%S)"
docker tag \
  $JONET_API_IMAGE_REGISTRY:$latestTag \
  $JONET_API_IMAGE_REGISTRY:$JONET_API_LATEST_IMAGE_TAG

# Push all images + tags to the remote
docker push $JONET_API_IMAGE_REGISTRY

