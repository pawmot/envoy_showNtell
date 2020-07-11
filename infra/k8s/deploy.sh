#!/bin/bash

./createMeshConfigMaps.sh

kubectl apply -f ns.yaml
kubectl apply -f booksSvc.yaml
kubectl apply -f reviewsSvc.yaml
kubectl apply -f bff.yaml
kubectl apply -f fe.yaml
kubectl apply -f frontProxy.yaml
kubectl apply -f ing.yaml
