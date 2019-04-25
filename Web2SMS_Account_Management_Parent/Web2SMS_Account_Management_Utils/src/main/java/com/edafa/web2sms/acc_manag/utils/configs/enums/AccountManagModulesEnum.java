package com.edafa.web2sms.acc_manag.utils.configs.enums;

public enum AccountManagModulesEnum {
	AccountManagement, AccountManagUtils; 

	public String name;
	private boolean configsApplied = true;

	AccountManagModulesEnum() {
		this.name = name();
	}

	AccountManagModulesEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isConfigsApplied() {
		return configsApplied;
	}

	public void setConfigsApplied(boolean configsApplied) {
		this.configsApplied = configsApplied;
	}

	@Override
	public String toString() {
		return name;
	}

}
