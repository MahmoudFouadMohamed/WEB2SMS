package com.edafa.web2sms.utils.configs.enums;


public enum LoggersEnum {
	WEB2SMS_APP_UTILS(ModulesEnum.CoreUtils, "web2sms_app"), 
	CAMP_EXE_ENGINE(ModulesEnum.CampaignEngine, "web2sms_camp_engine"),
	CAMP_EXE(ModulesEnum.CampaignEngine,"web2sms_camp_exe"),
	ACCOUNT_HANDLER(ModulesEnum.CampaignEngine,"web2sms_acct_handler"),
	AGG_CDR(ModulesEnum.CampaignEngine,"web2sms_agg_cdr"),
	QUOTA_AGG_TASK(ModulesEnum.CoreUtils,"web2sms_quota_agg"),
	CDR_TASK_MONITOR(ModulesEnum.CoreUtils,"web2sms_cdr_task_monitor"),
	DETAILED_CDR(ModulesEnum.CampaignEngine,"web2sms_detailed_cdr"), 
	SCHEDULER(ModulesEnum.CoreUtils, "web2sms_scheduler"),
	SMS_API(ModulesEnum.SMSAPIEngine, "web2sms_sms-api_engine");

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
