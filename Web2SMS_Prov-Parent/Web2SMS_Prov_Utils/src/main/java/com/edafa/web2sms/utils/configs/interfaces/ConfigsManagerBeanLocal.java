package com.edafa.web2sms.utils.configs.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.utils.configs.enums.ModulesEnum;

@Local
public interface ConfigsManagerBeanLocal {
	boolean refreshModuleConfigs(ModulesEnum module) throws Exception;

	void refreshAllModuleConfigs() throws Exception;

	void registerConfigsListener(ModulesEnum module, ConfigsListener listener);

	boolean isConfigurationLoaded(ModulesEnum module);

	void registerConfigsListener(ConfigsListener listener);

}
