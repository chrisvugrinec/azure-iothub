package com.microsoft.azure.iotdemo.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
public class DeviceController {

    @Autowired
    DeviceUtil deviceUtil;
    
    private final Gson gson = new Gson();
    private final static String TEST_DEVICENAME = "testDeviceNr";
    final static String TEST_DEVICETYPE = "testdevice";
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public boolean registerDevice(@RequestBody String deviceRequest) throws Exception {
        return deviceUtil.registerDevice(deviceRequest);
    }

    //	Creates x amount of testdevices
    @RequestMapping(value = "/createTestDevices", method = RequestMethod.GET)
    public void createTestDevices(@RequestParam int numberOfDevices) throws Exception {
    	for(int nr=0; nr<numberOfDevices; nr++ ){
    		JsonObject testObject = new JsonObject();
    		testObject.addProperty("deviceId", TEST_DEVICENAME+nr);
    		testObject.addProperty("deviceType", TEST_DEVICETYPE);
    		deviceUtil.registerDevice(gson.toJson(testObject));
    	}
    }
    
    @RequestMapping(value = "/deleteTestDevices", method = RequestMethod.GET)
    public void deleteTestDevices() throws Exception {
    	deviceUtil.deleteTestDevices();
    }

    @RequestMapping(value = "/showMessages", method = RequestMethod.GET)
    public void showMessages() throws Exception {
    	deviceUtil.showSbusMessagesMessages();
    }

    
    @RequestMapping(value = "/deleteAllDevices", method = RequestMethod.GET)
    public void deleteAllDevices() throws Exception {
    	deviceUtil.deleteAllDevices();
    }
    
}
