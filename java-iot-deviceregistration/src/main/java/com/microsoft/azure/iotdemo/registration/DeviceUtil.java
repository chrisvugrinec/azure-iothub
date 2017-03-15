package com.microsoft.azure.iotdemo.registration;

import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.documentdb.QueryIterable;
import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;


@RestController
public class DeviceUtil {
	
    @Value("${documentDBHost}")
    private String documentDBHost;

    @Value("${documentDBMasterKey}")
    private String documentDBMasterKey;
	
    @Value("${registrationConnectionString}")
    private String registrationConnectionString;

	
	private static final Logger LOGGER = Logger.getLogger(DeviceUtil.class.getName());
	private static final String databasename = "devices";
	private static final String collectionName = "deviceItems";
	private static Database db = new Database();
	private static DocumentCollection docColl = new DocumentCollection();
	private static RegistryManager registryManager = null;
	private final Gson gson = new Gson();

    private static DocumentClient documentClient;
    
    @PostConstruct
    void init() throws Exception{
    	
    	//	Init IOTHub
    	registryManager = RegistryManager.createFromConnectionString(registrationConnectionString);
    	
    	//	Init DocumentDB
    	documentClient = new DocumentClient(documentDBHost, documentDBMasterKey, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);    	
    	db.setId(databasename);
    	docColl.setId(collectionName);
    	
    	List<Database> dbases = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + databasename + "'", null).getQueryIterable().toList();
        if(dbases == null || dbases.isEmpty()){
        	documentClient.createDatabase(db, null);
        	db = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + databasename + "'", null).getQueryIterable().toList().get(0);
        }else{
        	db = dbases.get(0);
        }
    	List<DocumentCollection> docCollz =	documentClient.queryCollections(db.getSelfLink(),
        		"SELECT * FROM root r WHERE r.id='" + collectionName + "'", null).getQueryIterable().toList();
        if(docCollz == null || docCollz.isEmpty()){
        	documentClient.createCollection(db.getSelfLink(), docColl, null);
        	docColl = documentClient.queryCollections(db.getSelfLink(),
            		"SELECT * FROM root r WHERE r.id='" + collectionName + "'", null).getQueryIterable().toList().get(0);    
        }else{
        	docColl = docCollz.get(0);
        }

    }

    void deleteTestDevices() throws Exception {
    	//	Get list of TestDevices from documentDB
    	List<Document> documentList = documentClient.queryDocuments(docColl.getSelfLink(),
                        "SELECT * FROM c where c.deviceType =  '"+DeviceController.TEST_DEVICETYPE+"'", null).getQueryIterable().toList();
    	for(Document doc : documentList){

    		String azureUUID = doc.get("azureiot_uuid").toString();
    		LOGGER.info("remove device from iotHub: "+azureUUID);
    		registryManager.removeDevice(azureUUID);
    		
    		LOGGER.info("remove document from docDB: "+doc.get("deviceId"));
    		documentClient.deleteDocument(doc.getSelfLink(), null);
    	}
    }

    void deleteAllDevices() throws Exception {

    		QueryIterable<Document> test = documentClient.queryDocuments(docColl.getSelfLink(),
    				                       "SELECT * FROM c", null).getQueryIterable();
    		
    		if(test!=null){
    			List<Document> documentListz = test.toList();
		    	for(Document doc : documentListz){
		
		    		if(doc.get("azureiot_uuid")!=null){
			    		String azureUUID = doc.get("azureiot_uuid").toString();
			    		
			    		LOGGER.info("remove device from iotHub: "+azureUUID);
			    		registryManager.removeDevice(azureUUID);
		    		}
		    		LOGGER.info("remove document from docDB: "+doc.get("deviceId"));
		    		documentClient.deleteDocument(doc.getSelfLink(), null);
		    	}    			
    		}
    }

    
    
    boolean registerDevice(@RequestBody String deviceRequest) throws Exception {
    	boolean result = false, res1  = false, res2  = false;
        try{
        	JsonElement element = gson.fromJson (deviceRequest, JsonElement.class);
        	JsonObject jsonObj = element.getAsJsonObject();

            res1 = registerAzureIOTDevice(jsonObj);
            res2 = persistDeviceModel(jsonObj);
            if(res1 && res2)
            	result = true;
            else
            	throw new Exception("One of the registration methods failed:");
        }catch(Exception ex){
        	LOGGER.info("X message: "+ex.getMessage());
        	LOGGER.info("- registerAzureIOTDevice "+res1);
        	LOGGER.info("- persistDeviceModel "+res2);
        }
        return result;
    }
        
    private String someNamingLogic(){
    	return java.util.UUID.randomUUID().toString();    	
    }

    private boolean registerAzureIOTDevice(JsonObject deviceRequest) throws Exception {
    	boolean result = false;
    	    	
        String deviceId = deviceRequest.get("deviceId").toString();
        String azureIotUud = someNamingLogic();
        
        deviceRequest.addProperty("azureiot_createdate", Instant.now().toString());
        deviceRequest.addProperty("azureiot_uuid", azureIotUud);

        deviceRequest.addProperty("azureiot_createdate", Instant.now().toString());
        
        Device device = Device.createFromId( azureIotUud , null, null);
        try {
            device = registryManager.addDevice(device);
    		LOGGER.info("Created Azure IOT device with ID: "+deviceId+" azureId "+azureIotUud);
            result = true;
        } catch (IotHubException iote) {
            LOGGER.log(Level.SEVERE, "Connection String used: "+registrationConnectionString);
        	LOGGER.log(Level.SEVERE, "Exception creating device with ID: "+deviceId+" azureID "+azureIotUud, iote);
        }
        return result;
    }
    
       
    
    private boolean persistDeviceModel(JsonObject deviceRequest) throws DocumentClientException{
    	boolean result = false;

    	//	Adding timestamp
        deviceRequest.addProperty("azuredocdb_createdate", Instant.now().toString());
    	//	Persisting in docDB
        Document deviceDocument = new Document(gson.toJson(deviceRequest));
        documentClient.createDocument(docColl.getSelfLink(), deviceDocument, null,false);
        result = true;
    	return result;
    } 
    
    
    
}
