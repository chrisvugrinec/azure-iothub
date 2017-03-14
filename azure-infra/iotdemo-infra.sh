#!/bin/bash

rg="iotdemo"
iothubname="iotdemo-hub"
sbusname="iotdemo-sbus"
sbusqueuename="sbus-demo-queue"
sbuskeynamedemo="sbus-demo"
jobname="iotdemo-sa"
databaseAccounts_name="iotdemo-db"

#azure login

# Create Resource Group (if not exists)
azure group create $rg westeurope

# Create IOT DocumentDB
azure group deployment create $rg -p "{\"databaseAccounts_name\":{\"value\":\""$databaseAccounts_name"\"}}" --template-file documentdb.json

# Create IOT Hub
azure group deployment create $rg -p "{\"IotHubs_iothub_demo_name\":{\"value\":\""$iothubname"\"}}" --template-file iothub.json

# Create Service Bus
azure group deployment create $rg -p "{\"sbus_name\":{\"value\":\""$sbusname"\"}}" --template-file sbus.json

# Create Stream Analytics
azure group deployment create $rg -p "{\"streamAnalyticsJobName\":{\"value\":\""$jobname"\"}}" --template-file stream_analytics.json
