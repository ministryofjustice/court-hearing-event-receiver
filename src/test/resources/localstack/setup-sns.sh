#!/usr/bin/env bash
set -e
export TERM=ansi
export AWS_ACCESS_KEY_ID=foobar
export AWS_SECRET_ACCESS_KEY=foobar
export AWS_DEFAULT_REGION=eu-west-2
export PAGER=

# The topic that the receiver writes to
aws --endpoint-url=http://localhost:4566 sns create-topic --name court-case-events-topic

# This queue is needed so we can receive from it in tests and so confirm the topic was posted to
aws --endpoint-url http://localhost:4566 sqs create-queue --queue-name test-queue
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn "arn:aws:sns:eu-west-2:000000000000:court-case-events-topic" --protocol "sqs" --notification-endpoint "arn:aws:sqs:eu-west-2:000000000000:test-queue" --region eu-west-2

echo "SNS Configured"
