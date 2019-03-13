# JoNet API
This is the API service for the JoNet project.

# Developing
You need Java and Docker. Run `. ./scripts/setEnvars.sh` to set environment variables with test
values before doing anything else. Feel free to modify values in the setEnvars file as necessary.

# Scripts
The project has a bunch of useful scripts, both in gradle and in the `scripts` directory.

- `./gradlew run` runs the API server.
- `./gradlew test` runs the test suite.
- `./gradlew build` runs the test suite with code quality checks.
- `./scripts/dtests.sh` runs the test suite with code quality checks, in the docker environment.
- `./scripts/deployBuildInfra.sh` deploys the ECR and code pipeline for the project.
- `./scripts/deleteBuildInfra.sh` deletes the ECR and code pipeline for the project.
- `./scripts/publishImage.sh` creates a new application image, tags it, and pushes it to ECR.

