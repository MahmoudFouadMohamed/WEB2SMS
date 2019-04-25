package com.edafa.web2sms.utils.configs.enums;

public enum ModulesEnum {

	/*
	 * TODO should reactor this to follow the java enum instances naming convention
	 * Instances of an enum are constants and should follow the conventions for constants.
	*/

	ProvUtils,
	Provisioning,
	;

	public String name;
	private boolean configsApplied = true;

	ModulesEnum() {
		this.name = name();
	}

	ModulesEnum(String name) {
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
