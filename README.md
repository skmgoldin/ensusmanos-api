# JoNet API
This is the API service for the JoNet project.

# Environment variables

The project uses environment variables for configuration. You can set some sane defaults for these with the `setEnvars.sh` script.

|Variable|Remarks|
|--:|---|
|`JONET_API_ENV`|Generally should the active Git branch name, but "dev" is fine on a local machine|
|`JONET_API_PORT`|The port the API service should run on|
|`AWS_REGION`|The AWS region to put/find resources in|
|`AWS_ACCESS_KEY_ID`||
|`AWS_SECRET_ACCESS_KEY`||
|`JONET_API_TEST`|If "true", override implicit AWS SDK resource endpoints with test endpoints|
|`JONEST_TEST_USERS_DB_HOST_NAME`|Should be "localhost" when running outside of docker|
|`JONET_API_TEST_USERS_DB_PORT`|Should be "8000" when running docker tests|
|`JONET_API_TEST_NET_NAME`||
|`JONET_API_IMAGE_REGISTRY`||


