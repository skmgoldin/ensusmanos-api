#!/bin/bash

aws s3 cp \
  --recursive \
  --exclude *.swp \
  infra/build \
  s3://054393190750-cloudformation-templates/jonet/build/

aws s3 cp \
  --recursive \
  --exclude *.swp \
  infra/service \
  s3://054393190750-cloudformation-templates/jonet/service/

