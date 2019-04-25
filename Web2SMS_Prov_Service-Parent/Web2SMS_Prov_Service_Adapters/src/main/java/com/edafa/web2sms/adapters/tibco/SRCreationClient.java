package com.edafa.web2sms.adapters.tibco;

import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.adapters.http.XmlHttpClient;
import com.edafa.web2sms.adapters.tibco.exception.SRCreationFailed;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eg.com.vfe.xmlns.eai.celfocus.setservice.reply.SetServiceReply;
import eg.com.vfe.xmlns.internet.celfocus.client.autocreatesr.process.HTTPSR;

@Singleton
@LocalBean
public class SRCreationClient {

	static Logger appLogger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());
	private static Client client;
	private static JAXBContext jaxbContext;

	@EJB
	XmlHttpClient httpClient;

	@PostConstruct
	void init() {
		client = createClient();

		if (jaxbContext == null) {
			try {
				appLogger.info("Creating Jaxb context");
				jaxbContext = JAXBContext.newInstance(HTTPSR.class, SetServiceReply.class);
				appLogger.info("Jaxb context created");
			} catch (JAXBException e) {
				appLogger.error("Cannot initalize Jaxb context ", e);
			}
		}
	}

	private static final ThreadLocal<Marshaller> jaxbMarshallers = new ThreadLocal<Marshaller>() {
		protected Marshaller initialValue() {
			if (jaxbContext != null)
				try {
					return jaxbContext.createMarshaller();
				} catch (JAXBException e) {
					appLogger.error("Cannot initalize Jaxb context ", e);
				}
			return null;
		};
	};

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
		return c;
	}

	public SetServiceReply createSR(Logger logger, String trxId, String baseURL, JAXBElement<HTTPSR> request)
			throws SRCreationFailed {
		String trxIdlog = "ProvTrx(" + trxId + "): ";

		if (client == null) {
			client = createClient();
		}
		ClientResponse response;

		String xmlStr = getXmlString(request);
		logger.debug(trxIdlog + "SR Xml:" + xmlStr);

		try {
			WebResource webResource = client.resource(baseURL);
			response = webResource.accept(MediaType.APPLICATION_XML).type(MediaType.APPLICATION_XML)
					.post(ClientResponse.class, request);
		} catch (Exception e) {
			throw new SRCreationFailed(baseURL, e);
		}

		if (response.getStatus() != 200) {
			throw new SRCreationFailed(baseURL, response.getStatus());
		}

		logger.debug(trxIdlog + "SR request sent, getting the reply ");
		SetServiceReply reply  =response.getEntity(SetServiceReply.class);
		logger.debug(trxIdlog + "Reply: " + getXmlString(reply));

		if (!reply.getECode().equals("0")) {
			throw new SRCreationFailed(baseURL, reply);
		}

		return reply;
	}

	private String getXmlString(Object input) {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			Marshaller marshaller = jaxbMarshallers.get();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(input, writer);
		} catch (JAXBException e) {
			return null;
		}
		return "\n" + writer.toString();
	}
}
