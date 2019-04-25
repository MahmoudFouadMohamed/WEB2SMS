package com.edafa.web2sms.service.template.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.TemplateModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.template.model.TemplatesResultSet;

@WebService(name = "TemplateManegementService", portName = "TemplateManegementServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/template")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface TemplateManegementService {

	@WebMethod(operationName = "getUserTemplates")
	@WebResult(name = "Templates", partName = "templates")
	public TemplatesResultSet getUserTemplates(
			@WebParam(name = "RequsetInfo", partName = "requsetInfo") UserTrxInfo userTrxInfo);

	@WebMethod(operationName = "getUserAndAdminTemplates")
	@WebResult(name = "Templates", partName = "templates")
	public TemplatesResultSet getUserAndAdminTemplates(
			@WebParam(name = "RequsetInfo", partName = "requsetInfo") UserTrxInfo userTrxInfo);

	@WebMethod(operationName = "createTemplate")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus createTemplate(
			@WebParam(name = "RequsetInfo", partName = "requsetInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "TemplateModel", partName = "templateModel") TemplateModel templateModel);

	@WebMethod(operationName = "deleteTemplate")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus deleteTemplate(
			@WebParam(name = "RequsetInfo", partName = "requsetInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "TemplateId", partName = "templateId") Integer templateId);

	@WebMethod(operationName = "editTemplate")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus updateTemplate(
			@WebParam(name = "RequsetInfo", partName = "requsetInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "TemplateModel", partName = "templateModel") TemplateModel templateModel);

	@WebMethod(operationName = "getAdminTemplates")
	@WebResult(name = "Templates", partName = "templates")
	public TemplatesResultSet getAdminTemplates(
			@WebParam(name = "RequsetInfo", partName = "requsetInfo") UserTrxInfo userTrxInfo);

}
