# JoNet API
This is the API service for the JoNet project.

To build it, run `docker built -t jonet/core .`
To run it, run `docker run -e JONET_ENV=$JONET_ENV -e JONET_PORT=$JONET_PORT -e AWS_REGION=$AWS_REGION -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -p 8000:8000/tcp jonet/core`

To test it, make sure that your `JONET_TEST` envar is set to `true`, and that a dynamodb-local instance is running on some port and that port is specified in `JONET_DEV_DB_PORT`. You also need a `JONET_TEST_DB_NAME` and a `JONET_TESTNET_NAME`. Then run `./gradlew test`.

# Environment variables

The project uses environment variables for configuration.

|Variable|Remarks|
|--:|---|
|`JONET_ENV`|Generally should the active Git branch name, but "dev" is fine on a local machine|
|`JONET_PORT`|The port the API service should run on|
|`AWS_REGION`|The AWS region to put/find resources in|
|`AWS_ACCESS_KEY_ID`||
|`AWS_SECRET_ACCESS_KEY`||
|`JONET_TEST`|If "true", override implicit AWS SDK resource endpoints with test endpoints|
|`JONEST_TEST_USERS_DB_HOST_NAME`|Should be "localhost" when running outside of docker|
|`JONET_TEST_USERS_DB_PORT`|Should be "8000" when running docker tests|
|`JONET_TEST_NET_NAME`||

