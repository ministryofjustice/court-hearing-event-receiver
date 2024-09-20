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
- Java 21
- Docker

We also use:
- `kubectl`,`helm` - For testing and managing k8s deployments
- `circleci` cli - For validating the circle configs

---

## Downloading archived payloads

Cher believes in life after upload and so places a backup of each received payload in an S3 bucket. This produces a lot of data so it's useful to know exactly where you need to look for a given file in the S3 bucket. Files will be placed in the bucket at a path where each of the identifiers in the filename can be determined from AppInsights customEvent telemetry. The following is an example followed by meaning of each component of the file path:

```
/cp/CONFIRM_UPDATE/C44SA00/2022-08-30/15-24-09-623847927-e31c1d8e-c940-4b7c-a879-214a57ef3864
/cp/<hearingEventType>/<courtCode>/<dateOfReceipt:YYYY-MM-DD>/<receiptTime:HH-mm-ss-nnn>-<hearingId>
```

These files can be downloaded either using the AWS CLI or using the [`./copy-s3.bash` script which can be found in `court-case-source`](https://github.com/ministryofjustice/court-case-source/blob/main/copy-s3.bash). GUI's such as Cyberduck are not recommended because the huge numbers of files in the prod bucket make it *incredibly* slow to list the contents.

---

## Running tests

To run the  tests :
- Run `docker compose up localstack-cher` to start  localstack with SNS, S3, SQS and any other dependent AWS services
- Run `./gradlew check`
- Run `./gradlew integrationTest` 


