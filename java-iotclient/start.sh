#!/bin/bash
while true
do
  export IOTHUB=$(/opt/getRandomDevice.sh)
  export DEVICE=$(echo $IOTHUB | sed 's/^.*DeviceId=//' | sed 's/;SharedAccessKey.*$//')
  java AzureIotHubClient 
  sleep 3
done
