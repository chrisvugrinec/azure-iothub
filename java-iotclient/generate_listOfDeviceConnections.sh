echo "iothub_name:"
read iothub_name
#iothub_name=iotdemo-hub
echo "resource group:"
read rg
#rg=iotdemohub

rm -f result.txt

# show connection to iothub
iothubconnection=$(azure iothub connectionstring show $rg $iothub_name  --json | jq -r '.primary')
iothub-explorer login $iothubconnection

keyz=$(iothub-explorer list | grep -e deviceId -e primaryKey)
counter=0
for line in $(echo $keyz)
do
  ((counter++))
  if [ $counter == 2 ]
  then 
    device=$line
  fi
  if [ $counter == 4 ]
  then
    key=$line
  fi
  if [ $counter == 6 ]
  then
    echo "HostName=$iothub_name.azure-devices.net;DeviceId=$device;SharedAccessKey=$key" >>result.txt
    counter=0
  fi   
done
# example of valid device connectionString
##HostName=iotdemo-hub.azure-devices.net;DeviceId=b7e43196-0d7d-49a5-8ba8-c36d72c1f9f4;SharedAccessKey=h+d5OMLMuwo1cWqqnEo2yQ==
