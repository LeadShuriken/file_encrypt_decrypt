#!/bin/bash
# THESE ARE REFERENCE, DNS DOES NOT SCALE

ENV=${1:-dev}
function prop {
    grep "${1}" env/${ENV}.properties|cut -d'=' -f2
}

function set_dns_policy() {
    local json_file=`mktemp`
    sed -e $1 $3 > $json_file
    aws route53 change-resource-record-sets \
            --hosted-zone-id $2 \
            --change-batch file://$json_file
    rm $json_file
}

# GLOBAL SPEC
B_NAME=encrypto_r53
ACCOUNT_ID=$(prop 'account.id')
DEPLOY_REGION=$(prop 'account.region')
HOSTED_ZONE_ID=$(prop ''$B_NAME'.hosted_zone_id')
DNS_ZONE_ID=$(prop ''$B_NAME'.dns_zone_id')
DOMAIN_NAME=$(prop ''$B_NAME'.domain_name')
DNS_NAME=$(prop ''$B_NAME'.dns_name')
POLICY_BATCH=$(prop ''$B_NAME'.dns_s3_policy')
CER_VAL_BATCH=$(prop ''$B_NAME'.cer_valid_policy')
TLSC_TOKEN=$(prop ''$B_NAME'.tlsc_token')

# S# BUCKET POLICY
set_dns_policy 's/#1#/'$DNS_ZONE_ID'/g;s/##2##/'$DNS_NAME'/g;s/#3#/'$DOMAIN_NAME'/g;' \
            $HOSTED_ZONE_ID $POLICY_BATCH

ARN_ID=$(aws acm request-certificate --domain-name www.$DOMAIN_NAME \
        --validation-method DNS \
        --idempotency-token $TLSC_TOKEN \
        --subject-alternative-names $DOMAIN_NAME \
        --query "CertificateArn" \
        | tr -d '"')

# CERTIFICATES
INVALID_CERTS=$(aws acm describe-certificate \
        --certificate-arn $ARN_ID \
        --query 'Certificate.DomainValidationOptions[*].ResourceRecord')

for row in $(echo "$INVALID_CERTS" | jq -r '.[] | @base64'); do
    _jq() { 
        echo ${row} | base64 --decode | jq -r ${1} 
    }
    set_dns_policy 's/#1#/'$(_jq '.Name')'/g;s/#2#/'$(_jq '.Value')'/g;' \
    $HOSTED_ZONE_ID $CER_VAL_BATCH
done
