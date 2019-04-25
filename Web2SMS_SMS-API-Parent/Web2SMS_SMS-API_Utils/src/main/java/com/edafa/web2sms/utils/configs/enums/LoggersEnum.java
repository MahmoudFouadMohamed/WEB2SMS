package com.edafa.web2sms.utils.configs.enums;

public enum LoggersEnum {
	APP_UTILS(ModulesEnum.SMSAPIUtils, "web2sms_app"), SMS_API(ModulesEnum.SMSAPI,
			"web2sms_smsapi"), SMS_API_CACHING(ModulesEnum.SMSAPI, "web2sms_smsapi_caching");

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
