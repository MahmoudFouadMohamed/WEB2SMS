package com.edafa.web2sms.ui.admin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Session Bean implementation class FileDownloadClient
 */
@Stateless
@LocalBean
public class FileDownloadClient {
	static Client client;

	static {
		client = Client.create();
	}

	@PostConstruct
	void init() {
		if (client == null)
			client = Client.create();
	}
	public FileDownloadClient() {
		// TODO Auto-generated constructor stub
	}

	// public InputStream download(String url, String fileName) throws
	// IOException {
	// WebResource resource = Client.create().resource(url);
	//
	// ClientResponse response = resource.queryParam("fileToken",
	// fileName).get(ClientResponse.class);
	// InputStream input = response.getEntityInputStream();
	//
	// if (response.getStatus() != 200 || input == null)
	// throw new FileNotFoundException();
	//
	// return input;
	// }

	public DownloadedFileInfo downloadFile(String trxId, String fileName, String url) throws IOException {
		Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

		if (client == null)
			client = Client.create();
		WebResource resource = client.resource(url);
		DownloadedFileInfo result;
		logger.info(" ("+ trxId + ") url : " + url);
		ClientResponse response = resource.queryParam("trxId", trxId).queryParam("fileToken", fileName).get(ClientResponse.class);
		logger.info(" ("+ trxId + ") response: " + response);
		InputStream input = response.getEntityInputStream();
		logger.info(" ("+ trxId + ") input: " + input);
		if (response.getStatus() != 200 || input == null)
			throw new FileNotFoundException();
		
		result = new DownloadedFileInfo((String) response.getHeaders().get("Content-Disposition").get(0).split("=")[1], 0, response.getEntityInputStream());
		logger.info(" ("+ trxId + ") result: ");
		return result;
	}
}
