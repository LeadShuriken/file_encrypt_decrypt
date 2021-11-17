#!/bin/bash
ENV=${1:-dev}
function prop {
    grep "${1}" env/${ENV}.properties|cut -d'=' -f2
}
