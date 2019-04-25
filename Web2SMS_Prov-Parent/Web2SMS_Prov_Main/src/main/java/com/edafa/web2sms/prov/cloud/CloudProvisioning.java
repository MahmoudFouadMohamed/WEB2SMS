package com.edafa.web2sms.prov.cloud;

import java.io.StringWriter;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.encyrptionutil.Interfaces.EncyrptionUtilInterface;
import com.edafa.web2sms.prov.cloud.model.GenericProductProvisionRequest;
import com.edafa.web2sms.prov.cloud.model.ProvisioningBaseType;
import com.edafa.web2sms.prov.cloud.model.callback.ProvisioningStatusUpdateType;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.remote.TrxId;
import com.edafa.web2sms.utils.remote.XmlHttpClient;
import com.sun.jersey.api.client.ClientResponse;


@Stateless
@LocalBean
@Path("/cloud/")
public class CloudProvisioning {
	static Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger cloudLogger;

	@EJB
	XmlHttpClient httpClient;

    @EJB
    AppErrorManagerAdapter appErrorManagerAdapter;
    
    @EJB
    EncyrptionUtilInterface encryptionUtil;
    
	static JAXBContext jaxbContext;

	@PostConstruct
	void init() {
            cloudLogger = LogManager.getLogger(LoggersEnum.CLOUD_PROV.name());
		if (jaxbContext == null) {
			try {
				appLogger.info("Creating Jaxb context");
				jaxbContext = JAXBContext.newInstance(GenericProductProvisionRequest.class,
						ProvisioningStatusUpdateType.class);
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
					Marshaller m = jaxbContext.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					return m;
				} catch (JAXBException e) {
					appLogger.error("Cannot initalize Jaxb context ", e);                                        
				}
			return null;
		};
	};

	@POST
	@Consumes("application/xml")
	public Response handleProvisioningRequest(GenericProductProvisionRequest provRequest) {
		// cloudLogger.info("Received new request " + provRequest);
//		cloudLogger.info("Received new provisioning request " + provRequest);
		cloudLogger.info("Received new request Xml: " + getXmlStringEncyrpted(provRequest));
//		String ip = request.getRemoteAddr().toString();
		boolean valid = false;
		int returnStatus;
		String logId= TrxId.getTrxId() + " | ";
		try {
			ProvisioningBaseType req;
			if ((req = provRequest.getGenericProductProvision()) != null) {
				req.setTypeName("GenericProductProvision");
				cloudLogger.trace(logId +req.logId() + "is recieved: " + req);
				valid = req.isValid();
			} else if ((req = provRequest.getGenericProductUpgrade()) != null) {
				req.setTypeName("GenericProductUpgrade");
				cloudLogger.trace(logId+req.logId() + "is recieved: " +req);
				valid = req.isValid();
			} else if ((req = provRequest.getGenericProductDowngrade()) != null) {
				req.setTypeName("GenericProductDowngrade");
				cloudLogger.trace(logId+req.logId() + "is recieved: " +req);
				valid = req.isValid();
			} else if ((req = provRequest.getGenericProductSuspension()) != null) {
				req.setTypeName("GenericProductSuspension");
				cloudLogger.trace(logId+req.logId() + "is recieved: " +req);
				valid = req.isValid();
			} else if ((req = provRequest.getGenericProductActivation()) != null) {
				req.setTypeName("GenericProductActivation");
				cloudLogger.trace(logId+req.logId() + "is recieved: " +req);
				valid = req.isValid();
			} else if ((req = provRequest.getGenericProductDelete()) != null) {
				req.setTypeName("GenericProductDelete");
				cloudLogger.trace(logId+req.logId() + "is recieved: " +req);
				valid = req.isValid();
			} else if ((req = provRequest.getGenericProductUserDelete()) != null) {
				req.setTypeName("genericProductUserDelete");
				cloudLogger.trace(logId+req.logId() + "is recieved: " +req);
				valid = req.isValid();
			} else if ((req = provRequest.getGenericProductUserCreate()) != null) {
				req.setTypeName("genericProductUserCreate");
				cloudLogger.trace(logId+req.logId() + "is recieved: " +req);
				valid = req.isValid();
			} 

			if (valid) {
				try {
					cloudLogger.info(logId+req.logId() + "Valid request");
					String cloudServiceURI = (String) Configs.CLOUD_PROV_SERVICE_URI.getValue();
					cloudLogger.trace(logId+req.logId() + "Will call cloud provisioning service URI:" + cloudServiceURI);
					ClientResponse cr = httpClient.sendHttpXmlRequest(cloudServiceURI, provRequest, "PROV");
					returnStatus = cr.getClientResponseStatus().getStatusCode();
//                                        if (returnStatus != 200) {
//                                            appErrorManagerAdapter.raiseError("Prov", AppErrors.FAILED_TO_FORWARD_REQUEST, "Failed to forward Prov request");
//                                        }
					cloudLogger.info(logId+req.logId() + "Returned from cloud provisioning service with status = "
							+ returnStatus + ". Returning to cloud with status " + returnStatus);
				} catch (Exception e) {
					cloudLogger.error(logId+req.logId()+"Failed to forward the request to cloud provisioning service");
					appLogger.error(logId+req.logId()+"Failed to forward the request to cloud provisioning service", e);

					returnStatus = Status.INTERNAL_SERVER_ERROR.getStatusCode();
                    appErrorManagerAdapter.raiseError("Prov", AppErrors.FAILED_TO_FORWARD_REQUEST, "Failed to forward Prov request");
				}
			} else {
				returnStatus = Status.BAD_REQUEST.getStatusCode();
				cloudLogger.info(logId+req.logId() + "Invalid request, return status=" + returnStatus);
                appErrorManagerAdapter.raiseError(req.logId(), AppErrors.INVALID_REQUEST, "Invalid Request");
               }
		} catch (Exception e) {
			cloudLogger.error(logId+"Unhandled exception coaught :");
			appLogger.error(logId+"Unhandled exception coaught :", e);

			returnStatus = Status.INTERNAL_SERVER_ERROR.getStatusCode();
            appErrorManagerAdapter.raiseError("Prov", AppErrors.GENERAL_ERROR, "Generic Failure");
		}

		return Response.status(returnStatus).build();
	}

	@POST
	@Path("/callback")
	@Consumes(MediaType.APPLICATION_XML)
	public Response sendCallBackStatusUpdate(@QueryParam("baseURI") String baseURI, ProvisioningStatusUpdateType input) {
		ClientResponse cr;
		String logId= TrxId.getTrxId() + " | ";

		cloudLogger.info(logId+"Received cloud call back status update to URL:\"" + baseURI + "\" Xml: "
				+ getXmlStringEncyrpted(input));
		
//		cloudLogger.info(logId+"Received cloud call back status update, with object" + input);
		
		try {
			cr = httpClient.sendHttpXmlRequest(URI.create(baseURI), input, "PROV");
		} catch (Exception e) {
			cloudLogger.error(logId+input.logId() + "Failed to call back cloud on URL:\"" + baseURI + "\"");
			appLogger.error(logId+input.logId() + "Failed to call back cloud on URL:\"" + baseURI + "\"", e);

            appErrorManagerAdapter.raiseError("Prov", AppErrors.FAILED_TO_FORWARD_REQUEST, "Failed to forward CallBack request");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.BAD_GATEWAY).build();
		}
		cloudLogger.info(logId+input.logId() + "Call back on return status: " + cr.getClientResponseStatus().getStatusCode());
		return Response.status(cr.getClientResponseStatus().getStatusCode()).build();
	}

	private String getXmlStringEncyrpted(Object obj) {
		StringWriter writer = null;
		try {
			if (jaxbContext == null)
				jaxbContext = JAXBContext.newInstance(obj.getClass());
			writer = new StringWriter();
			Marshaller marshaller = jaxbMarshallers.get();

			marshaller.marshal(obj, writer);
		} catch (JAXBException e) {
			return null;
		}
		return "\n" + encryptionUtil.encrypt(writer.toString());
	}
                
}