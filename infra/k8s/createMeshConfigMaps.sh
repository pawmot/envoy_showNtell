#!/bin/bash

function createSecret() {
  cmName=$1
  fromFile=$2

  kubectl get configmap "$cmName" -n envoy-snt >>/dev/null 2>>/dev/null
  if [[ $? != 0 ]]; then
    kubectl create configmap "$cmName" -n envoy-snt --from-file=envoy-config="$fromFile"
  else
    kubectl create configmap "$cmName" -n envoy-snt --from-file=envoy-config="$fromFile" --dry-run=client -o yaml | kubectl replace -f -
  fi
}

createSecret "envoy-snt-front-proxy" "../mesh/frontProxy.yaml"
createSecret "envoy-snt-fe-proxy" "../mesh/feProxy.yaml"
createSecret "envoy-snt-bff-proxy" "../mesh/bffProxy.yaml"
createSecret "envoy-snt-books-proxy" "../mesh/booksProxy.yaml"
createSecret "envoy-snt-reviews-proxy" "../mesh/reviewsProxy.yaml"
