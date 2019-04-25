package com.edafa.web2sms.acc_manag.utils.configs.exception;

import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;

public class ConfigValueNotSetException extends RuntimeException {

	private static final long serialVersionUID = -8455213227404007715L;

	AccountManagModulesEnum module;
	String property;

	public ConfigValueNotSetException(AccountManagModulesEnum module, String property) {
		super("No value set for property " + property + " in module " + module.getName()
				+ " and there is no default value for it.");
		this.module = module;
		this.property = property;
	}

	public AccountManagModulesEnum getModule() {
		return module;
	}

	public void setModule(AccountManagModulesEnum module) {
		this.module = module;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
