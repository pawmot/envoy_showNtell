#!/bin/bash

kubectl apply -f ns.yaml
kubectl apply -f ing.yaml
kubectl apply -f bff.yaml
kubectl apply -f booksSvc.yaml
kubectl apply -f reviewsSvc.yaml
kubectl apply -f fe.yaml
