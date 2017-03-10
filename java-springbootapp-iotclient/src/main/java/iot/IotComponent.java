package iot;

import com.google.gson.Gson;
import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//import com.microsoft.azure.documentdb.DocumentClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ConfigurationProperties()
class IotComponent {


    @Value("${app.connection}")
    private String connectionString;

    @Value("${app.clientconnection}")
    private String clientConnectionString;


    @Value("${app.deviceId}")
    static String deviceId;


    private static Logger logger = Logger.getLogger(IotComponent.class);

    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    protected static DeviceClient client;

  //  DocumentClient client = new DocumentClient("https://querydemo.documents.azure.com",
             //                                        "+9x2hFc7QsZ5hReULaqmBs01amCFiQAJZuoTqdZ79h/fGd2RSYoJVXAegVS7suJBg1pB+RQC8D45gp7bk0rSUw==", new ConnectionPolicy(),ConsistencyLevel.Session);

    void registerDevice() throws Exception {

        RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);
        Device device;

        device = Device.createFromId(deviceId, null, null);
        try {
            device = registryManager.addDevice(device);
        } catch (IotHubException iote) {
            try {
                device = registryManager.getDevice(deviceId);
            } catch (IotHubException iotf) {
                logger.error(iotf.getStackTrace());
            }
        }
    }


    List<Gson> getDocumentDBItemsTest(){

       // FeedResponse<Document> queryResults = this.client.queryDocuments(
         //       "/dbs/familydb/colls/familycoll",
           //     "SELECT * FROM Family WHERE Family.lastName = 'Andersen'",
             //   null);
        return null;
    }

    void sendMessages() throws Exception {

        client = new DeviceClient(clientConnectionString, protocol);
        client.open();
        IotMessageSender sender = new IotMessageSender();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(sender);
        System.out.println("Press ENTER to exit.");
        System.in.read();
        executor.shutdownNow();
        client.close();
    }

}
