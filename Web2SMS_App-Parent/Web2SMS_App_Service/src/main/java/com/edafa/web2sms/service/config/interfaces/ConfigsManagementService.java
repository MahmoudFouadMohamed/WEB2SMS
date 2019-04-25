/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.config.interfaces;

import com.edafa.web2sms.service.config.model.ConfigResultSet;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.utils.configs.model.Config;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 *
 * @author mahmoud
 */
@WebService(name = "ConfigsManagementService", portName = "ConfigsManagementServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/config")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ConfigsManagementService {

    @WebMethod(operationName = "readConfigsModule")
    @WebResult(name = "ConfigResultSet", partName = "configResultSet")
    public ConfigResultSet getModuleConfigs(
            @WebParam(name = "ModuleName", partName = "moduleName") String moduleName);

    @WebMethod(operationName = "saveModuleConfigs")
    @WebResult(name = "ResultStatus", partName = "resultStatus")
    public ResultStatus saveModuleConfigs(
            @WebParam(name = "ModuleName", partName = "moduleName") String moduleName,
            @WebParam(name = "Config", partName = "config") List<Config> configs);
}
