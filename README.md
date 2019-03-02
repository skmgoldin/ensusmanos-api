# JoNet API
This is the API service for the JoNet project.

To build it, run `docker built -t jonet/core .`
To run it, run `docker run -e JONET_ENV=$JONET_ENV -e JONET_PORT=$JONET_PORT -e AWS_REGION=$AWS_REGION -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -p 8000:8000/tcp jonet/core`
