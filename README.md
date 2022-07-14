# court-hearing-event-receiver
[![Swagger API docs (needs VPN)](https://img.shields.io/badge/API_docs_(needs_VPN)-view-85EA2D.svg?logo=swagger)](https://court-hearing-event-receiver-dev.hmpps.service.justice.gov.uk/swagger-ui.html#)
[![CircleCI](https://circleci.com/gh/ministryofjustice/court-hearing-event-receiver.svg?style=svg)](https://circleci.com/gh/ministryofjustice/court-hearing-event-receiver)

The service will receive court hearing events from CP and publish these to SNS.

## Quick Start
This section contains the bare minimum you need to do to get the app running against the dev environment assuming you've got all the necessary dependencies (see Prerequisites section).
- Run `docker-compose up localstack-cher` to start  localstack with SNS, S3 and any other dependent AWS services
- Run `./gradlew clean build` to build the application
- Optional: Run `./gradlew installGitHooks` to install Git hooks from `./hooks` directory. Note these require localstack to be running to pass.

---

## Prerequisites
- Java 16
- Docker

We also use:
- `kubectl`,`minikube`,`helm` - For testing and managing k8s deployments
- `circleci` cli - For validating the circle configs
- `snyk` cli - For vulnerability checking

---

