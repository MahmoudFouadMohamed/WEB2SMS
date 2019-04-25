/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.config;

import com.edafa.web2sms.service.config.interfaces.ConfigsManagementBeanLocal;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsSource;
import com.edafa.web2sms.utils.configs.model.Config;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author mahmoud
 */
@Stateless
public class ConfigsManagementBean implements ConfigsManagementBeanLocal {

    @EJB//(lookup = "java:module/DefaultConfigsSource")
    ConfigsSource defaultConfigsSource;

    @Override
    public List<Config> getModuleConfigs(String moduleName) throws FailedToReadConfigsException {
        return defaultConfigsSource.readConfigs(moduleName);
    }

    @Override
    public void saveModuleConfigs(String moduleName, List<Config> configs) throws FailedToSaveConfigsException {
        defaultConfigsSource.saveConfigs(moduleName, configs);
    }

}
