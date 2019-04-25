package com.edafa.web2sms.utils.configs.enums;

public enum LoggersEnum {
	APP_UTILS(ModulesEnum.ProvUtils, "web2sms_app"), CLOUD_PROV(ModulesEnum.CloudProvisioning, "web2sms_cloud");

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
