package com.edafa.web2sms.acc_manag.utils.configs.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.model.Config;

@Local
public interface ConfigsSource {

	public List<Config> readConfigs(AccountManagModulesEnum module) throws FailedToReadConfigsException;

	public void saveConfigs(AccountManagModulesEnum module, List<Config> configs) throws FailedToSaveConfigsException;

}
