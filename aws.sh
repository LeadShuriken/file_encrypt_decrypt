#!/bin/bash

# ACCOUNT INFO
ACCOUNT=864685453410
REGION=us-east-1
PROJECT_NAME=encrypto
KEY=encrypto-pair

# IMAGE INFO
AMI=ami-04ad2567c9e3d7893
TYPE=t2.micro

# IAAS INFO
EC2_ROLE=SSM-FULL-ACCESS 
# NEEDED ROLES:
# AmazonEC2ContainerRegistryFullAccess
# AmazonSSMFullAccess

SECURITY_GROUP=sg-04b18df51238c1c5f
VPC_SUBNET=subnet-813e768f

# Authenticate with ecr registry
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ACCOUNT.dkr.ecr.$REGION.amazonaws.com

# Build Docker
docker build -f Dockerfile -t $PROJECT_NAME .
# TAG
docker tag $PROJECT_NAME:latest $ACCOUNT.dkr.ecr.$REGION.amazonaws.com/$PROJECT_NAME:latest
docker push $ACCOUNT.dkr.ecr.$REGION.amazonaws.com/$PROJECT_NAME:latest

INSTANCE_ID=$(aws ec2 run-instances \
        --image-id $AMI \
        --count 1 \
        --instance-type $TYPE \
        --key-name $KEY \
        --security-group-ids $SECURITY_GROUP \
        --subnet-id $VPC_SUBNET \
        --output text \
        --user-data file://docketboot.sh \
        --query "Instances[*].InstanceId")

# # SSH WORKFLOW
# aws ec2 wait instance-status-ok --instance-ids $INSTANCE_ID
#
# PUBLIC_IP=$(aws ec2 describe-instances \
#     --instance-ids $INSTANCE_ID \
#     --query 'Reservations[*].Instances[*].PublicIpAddress' \
#     --output text)

# ssh -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null -i $KEY.pem \
#     ec2-user@$PUBLIC_IP "aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ACCOUNT.dkr.ecr.$REGION.amazonaws.com; docker pull $ACCOUNT.dkr.ecr.$REGION.amazonaws.com/$PROJECT_NAME:latest; docker run --rm -it -p 80:8080 $ACCOUNT.dkr.ecr.$REGION.amazonaws.com/$PROJECT_NAME:latest;"

# SSM WORKFLOW
aws ec2 wait instance-running --instance-ids $INSTANCE_ID

aws ec2 associate-iam-instance-profile \
    --instance-id $INSTANCE_ID \
    --iam-instance-profile Name=$EC2_ROLE \
    --output text

aws ec2 wait instance-status-ok --instance-ids $INSTANCE_ID
aws ec2 wait system-status-ok --instance-ids $INSTANCE_ID

while STATE=$(aws ec2 describe-instances --instance-ids $INSTANCE_ID --output text --query 'Reservations[*].Instances[*].IamInstanceProfile.Arn'); test "$STATE" != "arn:aws:iam::$ACCOUNT:instance-profile/$EC2_ROLE"; do
    sleep 1;
done;

aws ssm send-command \
    --document-name "AWS-RunShellScript" \
    --targets "Key=InstanceIds,Values=$INSTANCE_ID" \
    --output text \
    --parameters 'commands=["aws ecr get-login-password --region '$REGION' | docker login --username AWS --password-stdin '$ACCOUNT'.dkr.ecr.'$REGION'.amazonaws.com","docker pull '$ACCOUNT'.dkr.ecr.'$REGION'.amazonaws.com/'$PROJECT_NAME':latest","docker run --rm -d -p 80:8080 '$ACCOUNT'.dkr.ecr.'$REGION'.amazonaws.com/'$PROJECT_NAME':latest"]'