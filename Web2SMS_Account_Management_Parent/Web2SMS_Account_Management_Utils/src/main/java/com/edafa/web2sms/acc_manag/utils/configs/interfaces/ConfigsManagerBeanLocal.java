package com.edafa.web2sms.acc_manag.utils.configs.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.InvalidConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.model.ModuleConfigs;

@Local
public interface ConfigsManagerBeanLocal {

	void registerConfigsListener(AccountManagModulesEnum module, ConfigsListener listener);

	void registerConfigsListener(ConfigsListener listener);

	boolean isConfigurationLoaded(AccountManagModulesEnum module);

	void refreshModuleConfigs(String module) throws FailedToReadConfigsException, InvalidConfigsException;

	void refreshModuleConfigs(AccountManagModulesEnum module) throws FailedToReadConfigsException, InvalidConfigsException;

	void refreshAllModuleConfigs() throws FailedToReadConfigsException, InvalidConfigsException;

	public ModuleConfigs readModuleConfigs(String module) throws FailedToReadConfigsException;

	public ModuleConfigs readModuleConfigs(AccountManagModulesEnum module) throws FailedToReadConfigsException;

	public List<ModuleConfigs> readConfigs() throws FailedToReadConfigsException;

	void saveConfigs(ModuleConfigs moduleConfigs) throws FailedToSaveConfigsException, InvalidConfigsException;

	void refreshModuleConfigs(ModuleConfigs moduleConfigs) throws InvalidConfigsException;

}
