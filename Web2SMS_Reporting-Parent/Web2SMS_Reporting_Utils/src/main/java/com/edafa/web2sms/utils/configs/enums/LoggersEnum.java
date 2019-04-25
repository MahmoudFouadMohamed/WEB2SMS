package com.edafa.web2sms.utils.configs.enums;

public enum LoggersEnum {
	APP_UTILS(ModulesEnum.Reporting, "web2sms_app"),
	CAMP_MNGMT(ModulesEnum.Reporting, "web2sms_camp"),
	SMS_API_MNGT(ModulesEnum.Reporting, "web2sms_smsapi");

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
