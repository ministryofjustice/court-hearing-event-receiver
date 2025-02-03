#!/usr/bin/env bash
set -e
export TERM=ansi
export AWS_ACCESS_KEY_ID=foobar
export AWS_SECRET_ACCESS_KEY=foobar
export AWS_DEFAULT_REGION=eu-west-2
export PAGER=

# Create the bucket
aws s3 --endpoint-url=http://localhost:4566 --region eu-west-2 ls s3://cpg-s3-bucket  || aws --endpoint-url=http://localhost:4566 --region eu-west-2 s3 mb s3://cpg-s3-bucket
aws s3 --endpoint-url=http://localhost:4566 --region eu-west-2 ls s3://s3-large-cases-bucket  || aws --endpoint-url=http://localhost:4566 --region eu-west-2 s3 mb s3://s3-large-cases-bucket

aws --endpoint-url=http://localhost:4566 sns create-topic --name court-cases-topic.fifo --attributes '{"FifoTopic":"true", "ContentBasedDeduplication":"true"}'
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn "arn:aws:sns:eu-west-2:000000000000:court-cases-topic.fifo" --protocol "sqs" --notification-endpoint "arn:aws:sns:eu-west-2:000000000000:court_cases_queue.fifo"

echo "S3 Configured with cpg-s3-bucket"