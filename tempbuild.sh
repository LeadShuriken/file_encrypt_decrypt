#!/bin/bash
sudo yum install docker #AmzLin1
sudo amazon-linux-extras install docker #AmzLin2
sudo service docker start
sudo usermod -a -G docker ec2-user
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 864685453410.dkr.ecr.us-east-1.amazonaws.com
docker pull 864685453410.dkr.ecr.us-east-1.amazonaws.com/encrypto_repository:latest
