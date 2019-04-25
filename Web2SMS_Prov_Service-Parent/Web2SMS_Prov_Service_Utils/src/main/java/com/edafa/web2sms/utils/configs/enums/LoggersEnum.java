package com.edafa.web2sms.utils.configs.enums;

public enum LoggersEnum {
	PROV_UTILS(ModulesEnum.ProvUtils, "web2sms_app"),
        PROV(ModulesEnum.Provisioning, "web2sms_prov");

	private ModulesEnum module;
	private String logFileName;

	LoggersEnum(ModulesEnum module, String logFileName) {
		this.module = module;
		this.logFileName = logFileName;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public ModulesEnum getModule() {
		return module;
	}
}
