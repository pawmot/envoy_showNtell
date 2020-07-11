#!/bin/bash

./createMeshConfigMaps.sh

kubectl apply -f ns.yaml
kubectl apply -f booksSvc.yaml --force
kubectl apply -f reviewsSvc.yaml
kubectl apply -f bff.yaml --force
kubectl apply -f fe.yaml --force
kubectl apply -f frontProxy.yaml
kubectl apply -f ing.yaml
