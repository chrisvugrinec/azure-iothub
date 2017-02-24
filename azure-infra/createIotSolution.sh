#!/bin/bash
azure login
iothubname="iotdemo-hub"
sbusname="iotdemo-sbus"
sbusqueuename="sbus-demo-queue"
sbuskeynamedemo="sbus-demo"

echo "resourcegroup: "
read rg
azure group create $rg westeurope

echo "creating iot hub..."
azure group deployment create $rg -p "{\"IotHubs_iothub_demo_name\":{\"value\":\""$iothubname"\"}}" --template-file iothub.json

echo "waiting for 100 secs...for iothub to become ready before creating device"
sleep 100

connectionString=$(azure iothub connectionstring show $rg $iothubname --json | jq -r ".primary")
iothub-explorer login $connectionString
iothub-explorer create --auto

azure group deployment create $rg -p "{\"sbus_name\":{\"value\":\""$sbusname"\"}}" --template-file sbus.json
