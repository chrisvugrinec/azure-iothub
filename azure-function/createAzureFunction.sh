#!/bin/bash
#azure login

echo "resourcegroup: "
read rg
azure group create $rg westeurope

echo "app name"
read appname

echo "creating iot hub azure function..."
azure group deployment create $rg -p "{\"appName\":{\"value\":\""$appname"\"}}" --template-file az-function.json
