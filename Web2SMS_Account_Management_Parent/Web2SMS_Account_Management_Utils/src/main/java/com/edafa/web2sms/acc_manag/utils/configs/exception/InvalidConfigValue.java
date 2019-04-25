package com.edafa.web2sms.acc_manag.utils.configs.exception;

import com.edafa.web2sms.acc_manag.utils.configs.enums.ConfigType;
import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;

public class InvalidConfigValue extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7206206148687031478L;

	String property;
	Object invalidValue;
	AccountManagModulesEnum module;
	ConfigType configType;

	public InvalidConfigValue() {
		super();
	}

	public InvalidConfigValue(AccountManagModulesEnum module, String property, Object invalidValue, ConfigType configType) {
		super("Invalid value:" + invalidValue + " for " + property + " in module " + module + " expected "
				+ configType.name());
		this.module = module;
		this.property = property;
		this.invalidValue = invalidValue;
		this.configType = configType;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getInvalidValue() {
		return invalidValue;
	}
}
