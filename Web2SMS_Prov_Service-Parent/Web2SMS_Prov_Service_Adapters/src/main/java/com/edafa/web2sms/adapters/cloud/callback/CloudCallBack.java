package com.edafa.web2sms.adapters.cloud.callback;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;

import com.edafa.web2sms.adapters.cloud.callback.model.ProvisioningStatusUpdateType;
import com.edafa.web2sms.adapters.cloud.exception.FailedToCallBackCloud;
import com.edafa.web2sms.adapters.http.XmlHttpClient;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Stateless
@LocalBean
public class CloudCallBack {

	private Client client;

	@EJB
	XmlHttpClient httpClient;

	@PostConstruct
	void init() {
		client = createClient();
	}

	/**
	 * Template method to allow tooling to customize the new Client
	 * 
	 */
	private void customizeClientConfiguration(ClientConfig cc) {
		cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, 10);
	}

	/**
	 * Template method to allow tooling to override Client factory
	 * 
	 */
	private Client createClientInstance(ClientConfig cc) {
		return Client.create(cc);
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
		c.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, connectTimeout);
		c.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, requestTimeout);
		return createClientInstance(cc);
	}

	public void sendCallBackStatusUpdate(String cloudCallBackURL, ProvisioningStatusUpdateType provStatus)
			throws FailedToCallBackCloud {
		String cloudCallBackServiceURI = (String) Configs.CLOUD_CALL_BACK_SERVICE_URI.getValue();

		if (client == null) {
			client = createClient();
		}
		ClientResponse response;

		try {
			WebResource webResource = client.resource(URI.create(cloudCallBackServiceURI));
			response = webResource.queryParam("baseURI", cloudCallBackURL).type(MediaType.APPLICATION_XML)
					.post(ClientResponse.class, provStatus);
		} catch (Exception e) {
			throw new FailedToCallBackCloud(cloudCallBackServiceURI, e);
		}

		if (response.getStatus() != 200) {
			throw new FailedToCallBackCloud(cloudCallBackURL, response.getStatus());
		}
	}
}
