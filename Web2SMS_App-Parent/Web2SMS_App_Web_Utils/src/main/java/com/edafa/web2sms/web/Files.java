package com.edafa.web2sms.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.utils.FileNameUtils;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.multipart.FormDataParam;

@Path("/files")
public class Files {

	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	@GET
	@Path("/download")
	@Produces(MediaType.TEXT_PLAIN)
	public Response downloadFile(@QueryParam("fileToken") String fileToken, @QueryParam("trxId") String trxId) {
		if (fileToken == null || fileToken.isEmpty()) {
			appLogger.error("Invalid request to download: trxId=" + trxId + ", fileToken=" + fileToken);
			ResponseBuilder response = Response.status(Status.BAD_REQUEST);
			return response.build();
		}

		appLogger.info(logTrxId(trxId) + "Request to download: fileToken=" + fileToken);
		String locPath = AppSettings.BaseDir.getEnvEntryValue() + FileNameUtils.decodeFileToken(fileToken);
		appLogger.debug(logTrxId(trxId) + "Download path: " + locPath);
		// Put some validations here such as invalid file name or missing file
		// name

		// Prepare a file object with file to return
		File file = new File(locPath);
		if (file.exists()) {
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition", "attachment; filename=" + file.getName());
			return response.build();
		}
		appLogger.error(logTrxId(trxId) + "File not found");
		return Response.status(Status.BAD_REQUEST).build();
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String uploadFile(@FormDataParam("file") InputStream uploadedInputStream) throws IOException {
		// TODO: logging
		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String locPath = (String) Configs.FILES_PATH.getValue();
		String fileName = getUniqueFileName();
		String uploadedFileLocation = basePath + locPath + fileName;

		// save it
		File newFile = new File(uploadedFileLocation);
		long fileSize = 0;
		try {
			fileSize = writeToFile(uploadedInputStream, newFile);
		} catch (IOException e) {
			appLogger.error("Failed to handle new file [" + uploadedFileLocation + "] ", e);
			throw e;
		}
		String fileToken = FileNameUtils.encodeFileToken(locPath + fileName);

		appLogger.info("File uploaded to : [" + uploadedFileLocation + "], fileSize=" + fileSize
				+ " byte(s), returned fileToken=[" + fileToken + "]");

		return fileToken;

	}

	private String getUniqueFileName() {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
		String postfix = String.valueOf((int) (Math.random() * 100));
		return df.format(new Date()) + "_" + postfix;
	}

	// save uploaded file to new location
	private long writeToFile(InputStream uploadedInputStream, File newFile) throws IOException {
		OutputStream out;
		int read = 0;
		long totalRead = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(newFile);
		while ((read = uploadedInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
			totalRead += read;
		}
		out.flush();
		out.close();
		return totalRead;
	}

	public String logTrxId(String trxId) {
		return "Trx(" + trxId + "): ";
	}
}
