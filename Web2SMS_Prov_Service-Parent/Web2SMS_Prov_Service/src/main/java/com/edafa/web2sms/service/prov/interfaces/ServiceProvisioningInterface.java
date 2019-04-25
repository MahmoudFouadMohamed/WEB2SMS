package com.edafa.web2sms.service.prov.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.service.model.ProvResultStatus;
import com.edafa.web2sms.service.model.ProvTrxInfo;

@WebService(name = "ServiceProvisioning", portName = "ServiceProvisioning", targetNamespace = "http://www.edafa.com/web2sms/service/prov/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ServiceProvisioningInterface {

	@WebMethod(operationName = "requestChangeSender")
	@WebResult(name = "ProvResultStatus", partName = "resultStatus")
	public ProvResultStatus requestChangeSender(
			@WebParam(name = "ProvTrxInfo", partName = "provTrxInfo") ProvTrxInfo provTrxInfo,
			@WebParam(name = "OldSender", partName = "oldSender") String oldSender,
			@WebParam(name = "NewSender", partName = "newSender") String newSender);
	
	
	@WebMethod(operationName = "requestAddSender")
	@WebResult(name = "ProvResultStatus", partName = "resultStatus")
	public ProvResultStatus requestAddSender(
			@WebParam(name = "ProvTrxInfo", partName = "provTrxInfo") ProvTrxInfo provTrxInfo,
			@WebParam(name = "NewSender", partName = "newSender") String newSender);
	
	
	
	@WebMethod(operationName = "requestDeleteSender")
	@WebResult(name = "ProvResultStatus", partName = "resultStatus")
	public ProvResultStatus requestDeleteSender(
			@WebParam(name = "ProvTrxInfo", partName = "provTrxInfo") ProvTrxInfo provTrxInfo,
			@WebParam(name = "Sender", partName = "sender") String sender);

}
