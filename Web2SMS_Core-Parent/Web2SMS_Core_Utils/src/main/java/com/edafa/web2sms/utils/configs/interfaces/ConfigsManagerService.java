package com.edafa.web2sms.utils.configs.interfaces;

import java.util.List;

import javax.ejb.Local;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigsException;
import com.edafa.web2sms.utils.configs.model.ModuleConfigs;
import com.sun.xml.ws.developer.SchemaValidation;


@WebService(name = "CoreConfigsManagerService", portName = "CoreConfigsManagerServicePort", targetNamespace = "http://www.edafa.com/ws/utils/configs")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@SchemaValidation
@Local
public interface ConfigsManagerService {

	@WebMethod(operationName = "getModules")
	@WebResult(name = "Modules", partName = "modules")
	public List<String> getModules();

	@WebMethod(operationName = "readConfigs")
	@WebResult(name = "ModuleConfigs", partName = "moduleConfigs")
	public List<ModuleConfigs> readConfigs() throws FailedToReadConfigsException;

	@WebMethod(operationName = "readModuleConfigs")
	@WebResult(name = "ModuleConfigs", partName = "moduleConfigs")
	public ModuleConfigs readModuleConfigs(@WebParam(name = "Module", partName = "module") String module)
			throws FailedToReadConfigsException;

	@WebMethod(operationName = "refreshModuleConfigs")
	public void refreshModuleConfigs(@WebParam(name = "Module", partName = "module") String module)
			throws FailedToReadConfigsException, InvalidConfigsException, FailedToSaveConfigsException;

	@WebMethod(operationName = "refreshAllModuleConfigs")
	public void refreshAllModuleConfigs() throws FailedToReadConfigsException, InvalidConfigsException;

	@WebMethod(operationName = "saveConfigs")
	public void saveConfigs(@WebParam(name = "ModuleConfigs", partName = "moduleConfigs") ModuleConfigs moduleConfigs)
			throws FailedToSaveConfigsException, InvalidConfigsException;

	
	@WebMethod(operationName = "readLimiters")
	@WebResult(name = "SendingRateLimiter", partName = "sendingRateLimiter")
	List<SendingRateLimiter> readLimiters() throws FailedToReadConfigsException;
	
	@WebMethod(operationName = "saveLimiter")
	void saveLimiter(@WebParam(name="SendingRateLimiter" , partName="limiter")SendingRateLimiter limiter) throws FailedToSaveConfigsException;
	
	@WebMethod(operationName = "getRunningLimiters")
	@WebResult(name = "SendingRateLimiter", partName = "sendingRateLimiter")
	List<SendingRateLimiter> getRunningLimiters() throws FailedToReadConfigsException;
	
	@WebMethod(operationName = "refreshLimiters")
	void refreshLimiters(@WebParam(name="SendingRateLimiter" , partName="limiters")List<SendingRateLimiter> limiters) 
			throws FailedToSaveConfigsException;
	
	@WebMethod(operationName = "refreshLimiter")
	void refreshLimiter(@WebParam(name="SendingRateLimiter" , partName="limiter")SendingRateLimiter limiter) throws FailedToSaveConfigsException;
	
	@WebMethod(operationName = "addLimiter")
	void addLimiter(@WebParam(name="SendingRateLimiter" , partName="limiter")SendingRateLimiter limiter) throws FailedToSaveConfigsException;

	@WebMethod(operationName = "saveLimiters")
	void saveLimiters(@WebParam(name="SendingRateLimiter" , partName="limiters")List<SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException;

	@WebMethod(operationName = "createLimiter")
	@WebResult(name = "SendingRateLimiterResult", partName = "limiter")
	SendingRateLimiter createLimiter(@WebParam(name="SendingRateLimiterParameter" , partName="limiter")SendingRateLimiter limiter)
			throws FailedToSaveConfigsException;

	@WebMethod(operationName = "deleteLimiter")
	void deleteLimiter(@WebParam(name="SendingRateLimiter" , partName="limiter")SendingRateLimiter limiter) throws DBException;

	@WebMethod(operationName = "RemoveLimiter")
	void RemoveLimiter(@WebParam(name="SendingRateLimiter" , partName="limiter")SendingRateLimiter limiter) throws FailedToSaveConfigsException;
}
