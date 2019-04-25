package com.edafa.web2sms.utils;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;

import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//gomaa note: why not using singleton
@Stateless
@LocalBean
public class XmlHttpClient {

	private static Client client;
        private static Client errorsRaisingClient;
        Logger appLogger;
	@PostConstruct
	void init() {
        appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
		System.out.println("Bean Created !");
		client = createClient();
        errorsRaisingClient = createClient();
	}

	/**
	 * Template method to allow tooling to customize the new Client
	 * 
	 */
	private void customizeClientConfiguration(ClientConfig cc) {
		cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, 32);
	}

	/**
	 * Template method to allow tooling to override Client factory
	 * 
	 */
	private Client createClientInstance(ClientConfig cc) {
		return Client.create(cc);
	}

	public void refreshConnectTimeOut()
	{
		if(client!=null)
		{
			client.setConnectTimeout((Integer) Configs.WS_CLIENT_CONNECT_TIMEOUT.getValue());
		}else {
			client = createClient();
		}
	}
	public void refreshReadTimeOut(){
		if(client!=null)
		{
			client.setReadTimeout((Integer) Configs.WS_CLIENT_REQUEST_TIMEOUT.getValue());
		}else {
			client = createClient();
		}
	}
	/**
	 * Create a new Client instance
	 * 
	 */
	private Client createClient() {
		ClientConfig cc = new DefaultClientConfig();
		customizeClientConfiguration(cc);
		Client c = createClientInstance(cc);
		Integer connectTimeout = (Integer) Configs.WS_CLIENT_CONNECT_TIMEOUT.getValue();
		Integer requestTimeout = (Integer) Configs.WS_CLIENT_REQUEST_TIMEOUT.getValue();
                // gomaa note: add this proreties at "customizeClientConfiguration" method
		c.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, connectTimeout);
		c.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, requestTimeout);
		return c;
	}

	public ClientResponse sendHttpXmlRequest(String baseURI, Object input, String requestType) {
                ClientResponse cr = null;
                try{
                   cr = sendHttpXmlRequest(URI.create(baseURI), input, requestType );
                } catch(Exception ex) {
                    appLogger.error("Error while send HttpXml Request: ", ex);
                    if (appLogger.isDebugEnabled()) {
                        appLogger.debug("Error while send HttpXml Request: " + input);
                    }
                }
                return cr;
	}

	public ClientResponse sendHttpXmlRequest(URI baseURI, Object input, String requestType) {
            
                Client usedClient = null;
                if (requestType.equals("ERROR")){
                    usedClient = errorsRaisingClient;
                } else {
                    usedClient = client;
                }
		if (usedClient == null) {
			usedClient = createClient();
		}

		WebResource webResource = usedClient.resource(baseURI);
		ClientResponse response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, input);
                
                if(response.getStatus() != ClientResponse.Status.OK.getStatusCode()){
                    return null;
                }
                
		return response;
	}
}
