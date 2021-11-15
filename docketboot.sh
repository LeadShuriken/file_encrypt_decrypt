#!/bin/bash
sudo yum install docker #AmzLin1
sudo amazon-linux-extras install docker #AmzLin2
sudo service docker start
sudo usermod -a -G docker ec2-user