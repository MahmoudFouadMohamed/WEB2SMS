/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.config;

import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.service.config.interfaces.ConfigsManagementBeanLocal;
import com.edafa.web2sms.service.config.interfaces.ConfigsManagementService;
import com.edafa.web2sms.service.config.model.ConfigResultSet;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.model.Config;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author mahmoud
 */
@Stateless
@LocalBean
@WebService(name = "ConfigsManagementService", serviceName = "ConfigsManagementService", targetNamespace = "http://www.edafa.com/web2sms/service/config", endpointInterface = "com.edafa.web2sms.service.config.interfaces.ConfigsManagementService")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class ConfigsManagementServiceImpl implements ConfigsManagementService {

    @EJB
    ConfigsManagementBeanLocal configsManagementBean;

    @EJB
    AppErrorManagerAdapter appErrorManagerAdapter;

    private org.apache.logging.log4j.Logger appLogger;

    @PostConstruct
    private void initLoger() {
        appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
    }

    @Override
    public ConfigResultSet getModuleConfigs(String moduleName) {
        ConfigResultSet configResultSet = new ConfigResultSet(ResponseStatus.SUCCESS);
        List<Config> configs;

        if (moduleName == null || moduleName.isEmpty()) {
            appLogger.error("Invalid get module configs request");
            reportAppError(AppErrors.INVALID_REQUEST, "Invalid get module configs request");
            configResultSet.setStatus(ResponseStatus.INVALID_REQUEST);
            return configResultSet;
        }

        try {
            configs = configsManagementBean.getModuleConfigs(moduleName);
            configResultSet.setConfigs(configs);
        } catch (FailedToReadConfigsException e) {
            String logMsg = "Failed to handle getModuleConfigs request";
            appLogger.error(logMsg, e);
            reportAppError(AppErrors.DATABASE_ERROR, "Read configs failure");
            configResultSet.setStatus(ResponseStatus.FAIL);
            configResultSet.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            String logMsg = "Failed to handle getModuleConfigs request";
            appLogger.error(logMsg, e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            configResultSet.setStatus(ResponseStatus.FAIL);
            configResultSet.setErrorMessage(e.getMessage());
        }

        return configResultSet;
    }

    @Override
    public ResultStatus saveModuleConfigs(String moduleName, List<Config> configs) {
        ResultStatus status = new ResultStatus(ResponseStatus.SUCCESS);

        if (moduleName == null || moduleName.isEmpty() || configs == null || configs.isEmpty()) {
            appLogger.error("Invalid save module configs request");
            reportAppError(AppErrors.INVALID_REQUEST, "Invalid save module configs request");
            status.setStatus(ResponseStatus.INVALID_REQUEST);
            return status;
        }

        try {
            configsManagementBean.saveModuleConfigs(moduleName, configs);
        } catch (FailedToSaveConfigsException e) {
            String logMsg = "Failed to handle getModuleConfigs request";
            appLogger.error(logMsg, e);
            reportAppError(AppErrors.DATABASE_ERROR, "Save configs failure");
            status.setStatus(ResponseStatus.FAIL);
            status.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            String logMsg = "Failed to handle getModuleConfigs request";
            appLogger.error(logMsg, e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            status.setStatus(ResponseStatus.FAIL);
            status.setErrorMessage(e.getMessage());
        }
        return status;
    }

    private void reportAppError(AppErrors error, String msg) {
        appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.CONFIGS_MANAGEMENT);
    }
}
