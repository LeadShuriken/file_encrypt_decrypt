#!/bin/bash
ENV=${1:-dev}
function prop {
    grep "${1}" env/${ENV}.properties|cut -d'=' -f2
}

# GLOBAL SPEC
B_NAME=encrypto_r53
ACCOUNT_ID=$(prop 'account.id')
DEPLOY_REGION=$(prop 'account.region')
