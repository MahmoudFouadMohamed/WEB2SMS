package com.edafa.web2sms.utils.configs.enums;

public enum ModulesEnum {

	ProvUtils, CloudProvisioning

	;

	public String name;

	ModulesEnum() {
		this.name = name();
	}

	ModulesEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
