#!/bin/bash
# THESE ARE REFERENCE, DNS DOES NOT SCALE

ENV=${1:-dev}
function prop {
    grep "${1}" env/${ENV}.properties|cut -d'=' -f2
}

function set_dns_policy() {
    local json_file=`mktemp`
    sed -e "s/HOZTED_ZONE_ID/$1/g" \
        -e "s/DNS_NAME/$2/g" \
        -e "s/DOMAIN_NAME/$3/g" $5 > $json_file
    aws route53 change-resource-record-sets \
            --hosted-zone-id $4 \
            --change-batch file://$json_file
    rm $json_file
}

# GLOBAL SPEC
B_NAME=encrypto_r53
ACCOUNT_ID=$(prop 'account.id')
DEPLOY_REGION=$(prop 'account.region')
HOZTED_ZONE_ID=$(prop ''$B_NAME'.hosted_zone_id')
DNS_ZONE_ID=$(prop ''$B_NAME'.dns_zone_id')
DOMAIN_NAME=$(prop ''$B_NAME'.domain_name')
DNS_NAME=$(prop ''$B_NAME'.dns_name')
POLICY_BATCH=$(prop ''$B_NAME'.dns_s3_policy')


set_dns_policy $DNS_ZONE_ID $DNS_NAME $DOMAIN_NAME $HOZTED_ZONE_ID $POLICY_BATCH