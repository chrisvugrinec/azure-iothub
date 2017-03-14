#!/bin/bash

rg="iotdemo2"
iothubname="iotdemo-hub2"
sbusname="iotdemo-sbus2"
sbusqueuename="sbus-demo-queue2"
sbuskeynamedemo="sbus-demo2"
jobname="iotdemo-sa2"
databaseAccounts_name="iotdemo-db2"

#azure login

# Create Resource Group (if not exists)
azure group create $rg westeurope

# Create IOT DocumentDB
azure group deployment create $rg -p "{\"databaseAccounts_name\":{\"value\":\""$databaseAccounts_name"\"}}" --template-file documentdb.json

# Create IOT Hub
#azure group deployment create $rg -p "{\"iothub_name\":{\"value\":\""$iothubname"\"}}" --template-file iothub.json

# Create Service Bus
#azure group deployment create $rg -p "{\"sbus_name\":{\"value\":\""$sbusname"\"}}" --template-file sbus.json

# Create Stream Analytics
#azure group deployment create $rg -p "{\"streamAnalyticsJobName\":{\"value\":\""$jobname"\"}}" --template-file stream_analytics.json
