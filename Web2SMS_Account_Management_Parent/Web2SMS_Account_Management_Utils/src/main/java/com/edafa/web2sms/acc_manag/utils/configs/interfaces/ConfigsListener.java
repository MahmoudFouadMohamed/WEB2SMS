package com.edafa.web2sms.acc_manag.utils.configs.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;

@Local
public interface ConfigsListener {
	void configurationRefreshed(AccountManagModulesEnum module);

	void configurationRefreshed();
}
