#!/bin/bash
while true
do
  IOTHUB=$(/opt/getRandomDevice.sh)
  DEVICE=$(echo $IOTHUB | sed 's/^.*DeviceId=//' | sed 's/;SharedAccessKey.*$//')
  java AzureIotHubClient  $IOTHUB $DEVICE
  sleep 2
done
