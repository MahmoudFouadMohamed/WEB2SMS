package com.edafa.web2sms.report.web.rest.service;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.service.model.AdminReportRequest;
import com.edafa.web2sms.service.model.UserReportRequest;
import com.edafa.web2sms.utils.FileNameUtils;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.ClientResponse.Status;

@Stateless
@LocalBean
@Path("reporting/files/")
public class ReportingRestWebServiceBean {

	private Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	public ReportingRestWebServiceBean() {}

	@PostConstruct
	public void onStartUp() {}

	@PreDestroy
	public void onDestroy() {}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("getAdminReport")
	public Response getAdminReport(AdminReportRequest request) {
		if (request.getFileToken() == null || request.getFileToken().isEmpty()) {
			appLogger.error(
					"Invalid request to download: trxId=" + request.getTrx() + ", fileToken=" + request.getFileToken());
			ResponseBuilder response = Response.status(Status.BAD_REQUEST);
			return response.build();
		}
		appLogger.info(request.getTrx() + " Request to download: fileToken=" + request.getFileToken());

		String locPath = AppSettings.BaseDir.getEnvEntryValue() + FileNameUtils.decodeFileToken(request.getFileToken());
		appLogger.debug(request.getTrx() + "Download path: " + locPath);

		File file = new File(locPath);
		if (file.exists()) {
			ResponseBuilder response = Response.ok(file);
			response.header("Content-Disposition", "attachment; filename=" + file.getName());
			return response.build();
		}

		appLogger.error(request.getTrx() + "File not found");
		return Response.status(Status.BAD_REQUEST).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("getUserReport")
	public Response getUserReport(UserReportRequest request) {
		if (request.getFileToken() == null || request.getFileToken().isEmpty()) {
			appLogger.error(
					"Invalid request to download: trxId=" + request.getTrx() + ", fileToken=" + request.getFileToken());
			ResponseBuilder response = Response.status(Status.BAD_REQUEST);
			return response.build();
		}
		appLogger.info(request.getTrx() + " Request to download: fileToken=" + request.getFileToken());

		String locPath = AppSettings.BaseDir.getEnvEntryValue() + FileNameUtils.decodeFileToken(request.getFileToken());
		appLogger.debug(request.getTrx() + "Download path: " + locPath);

		File file = new File(locPath);
		if (file.exists()) {
			ResponseBuilder response = Response.ok(file);
			response.header("Content-Disposition", "attachment; filename=" + file.getName());
			return response.build();
		}

		appLogger.error(request.getTrx() + "File not found");
		return Response.status(Status.BAD_REQUEST).build();
	}

}
