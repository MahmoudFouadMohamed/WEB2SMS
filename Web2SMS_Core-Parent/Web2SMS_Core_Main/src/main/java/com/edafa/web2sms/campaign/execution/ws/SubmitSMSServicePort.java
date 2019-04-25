package com.edafa.web2sms.campaign.execution.ws;

import javax.ejb.Local;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;

@Local
@WebService(name = "SubmitSMSService", serviceName = "SubmitSMSService", targetNamespace = "http://www.edafa.com/web2sms/sms/submit")
public interface SubmitSMSServicePort {

	@WebMethod(operationName = "submitSMS")
	@WebResult(name = "SMSId", partName = "smsId")
	public String submitSMS(@WebParam(name = "Language", partName = "language") LanguageNameEnum language,
			@WebParam(name = "Sender", partName = "sender") String sender, 
			@WebParam(name = "Receiver", partName = "receiver") String receiver, 
			@WebParam(name = "SMSText", partName = "smsText") String smsText, 
			@WebParam(name = "RegisteredDelivery", partName = "registeredDelivery") boolean registeredDelivery)
			throws Exception;
}
