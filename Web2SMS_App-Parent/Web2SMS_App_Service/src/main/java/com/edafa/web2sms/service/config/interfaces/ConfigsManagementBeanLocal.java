/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.config.interfaces;

import com.edafa.web2sms.service.config.model.ConfigResultSet;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.model.Config;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface ConfigsManagementBeanLocal {

    public List<Config> getModuleConfigs(String moduleName) throws FailedToReadConfigsException;

    public void saveModuleConfigs(String moduleName, List<Config> configs) throws FailedToSaveConfigsException;

}
