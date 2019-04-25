package com.edafa.web2sms.utils.configs.enums;

// SMPPModule(ModulesEnum.SMPPModule, "smppmodule"),
public enum LoggersEnum {
	// SMPPModule(ModulesEnum.SMPPModule, "smppmodule"),
	APP_UTILS(ModulesEnum.AppUtils, "web2sms_app"),

	CAMP_MNGMT(ModulesEnum.CampaignManagement, "web2sms_camp"),

	LIST_MNGMT(ModulesEnum.ListManagement, "web2sms_list"),

	TEMPLATE_MNGMT(ModulesEnum.TemplateManagement, "web2sms_template"),

	ADMIN_UI(ModulesEnum.AdminManagement, "web2sms_admin_ui"),

	ADMIN_MNGMT(ModulesEnum.AdminManagement, "web2sms_admin"),
	
	SMS_API_MNGT(ModulesEnum.SMSAPIManagement, "web2sms_smsapi"),
        
        SMS_API_CACHING(ModulesEnum.SMSAPIManagement, "web2sms_smsapi_caching")
	;

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
