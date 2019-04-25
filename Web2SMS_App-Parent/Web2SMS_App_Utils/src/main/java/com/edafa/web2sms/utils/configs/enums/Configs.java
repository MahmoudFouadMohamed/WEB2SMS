package com.edafa.web2sms.utils.configs.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edafa.web2sms.utils.configs.exception.ConfigValueNotSetException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigValue;

public enum Configs {

	// Campaign Management
	CAMPAIGN_SUBMITTION_VALIDITY_PERIOD(
			ModulesEnum.CampaignManagement, "CAMPAIGN_SUBMITTION_VALIDITY_PERIOD", 5, ConfigType.INTEGER), // min
	RESEND_CAMPAIGN_SUFFIX_NAME(
			ModulesEnum.CampaignManagement, "RESEND_CAMPAIGN_SUFFIX_NAME", "_ReSubmitted", ConfigType.STRING),

	LOG_LEVEL_CAMP(ModulesEnum.CampaignManagement, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
	REPORT_EXPORT_CHUNK_SIZE(ModulesEnum.CampaignManagement, "REPORT_EXPORT_CHUNK_SIZE", 50000, ConfigType.INTEGER),
	EXCEL_WINDOW_SIZE(ModulesEnum.CampaignManagement, "EXCEL_WINDOW_SIZE", 100000, ConfigType.INTEGER),
	EXCEL_SHEET_MAX_ROWS_NUM(ModulesEnum.CampaignManagement, "EXCEL_SHEET_ROWS_NUM", 10000, ConfigType.INTEGER),
	REPORT_EXPORT_CHARSET(ModulesEnum.CampaignManagement, "REPORT_EXPORT_CHARSET", "UTF-8", ConfigType.STRING),
	REPORT_EXPORT_PATH(
			ModulesEnum.CampaignManagement, "REPORT_EXPORT_PATH", "files/exports/reports/", ConfigType.STRING),
	REPORT_EXPORT_FILE_DELIM(ModulesEnum.CampaignManagement, "REPORT_EXPORT_FILE_DELIM", ",", ConfigType.STRING),
	REPORT_EXPORT_FILE_QUOTE_CHAR(
			ModulesEnum.CampaignManagement, "REPORT_EXPORT_FILE_QUOTE_CHAR", "\"", ConfigType.STRING),
	REPORT_EXPORT_FILE_COMPRESSED(
			ModulesEnum.CampaignManagement, "REPORT_EXPORT_FILE_COMPRESSED", true, ConfigType.BOOLEAN),
	CAMP_SEND_AT_APPROVAL(ModulesEnum.CampaignManagement, "CAMP_SEND_AT_APPROVAL", true, ConfigType.BOOLEAN),

	// Lists Management
	DEFAULT_VIRTUAL_LIST_NAME(
			ModulesEnum.ListManagement, "DEFAULT_VIRTUAL_LIST_NAME", "Individuals", ConfigType.STRING),
	DEFAULT_INTRA_LIST_NAME(ModulesEnum.ListManagement, "DEFAULT_INTRA_LIST_NAME", "Internal List", ConfigType.STRING),
	EXPORTED_LISTS_FILES_PATH(
			ModulesEnum.ListManagement, "EXPORTED_LISTS_FILES_PATH", "files/exports/lists/", ConfigType.STRING),
	EXPORTED_LISTS_CHUNK_SIZE(ModulesEnum.ListManagement, "EXPORTED_LISTS_CHUNK_SIZE", 50000, ConfigType.INTEGER),
	EXPORTED_LISTS_CSV_DELIM(ModulesEnum.ListManagement, "EXPORTED_LISTS_CSV_DELIM", ",", ConfigType.STRING),
	EXPORTED_LISTS_CSV_QUOTE_CHAR(ModulesEnum.ListManagement, "EXPORTED_LISTS_CSV_QUOTE_CHAR", "", ConfigType.STRING),
	EXPORTED_LISTS_FILE_COMPRESSED(
			ModulesEnum.ListManagement, "EXPORTED_LISTS_FILE_COMPRESSED", true, ConfigType.BOOLEAN),
	MAX_CONTACT_NAME_CHAR(ModulesEnum.ListManagement, "MAX_CONTACT_NAME_CHAR", 50, ConfigType.INTEGER),

	IMPORTED_LISTS_FILES_PATH(
			ModulesEnum.ListManagement, "IMPORTED_LISTS_FILES_PATH", "files/imports/lists/", ConfigType.STRING),
	IMPORTED_LISTS_CSV_DELIM(ModulesEnum.ListManagement, "IMPORTED_LISTS_CSV_DELIM", ",", ConfigType.STRING),
	IMPORTED_LISTS_CSV_QUOTE_CHAR(ModulesEnum.ListManagement, "IMPORTED_LISTS_CSV_QUOTE_CHAR", "", ConfigType.STRING),
	PERSIST_CONTACTS_CHUNK_SIZE(ModulesEnum.ListManagement, "PERSIST_CONTACTS_CHUNK_SIZE", 50000, ConfigType.INTEGER),

	LOG_LEVEL_LIST(ModulesEnum.ListManagement, "LOG_LEVEL", ConfigType.LOG_LEVEL),

	// App DateTimeUtils
	LOG_TIMESTAMP_FORMAT(ModulesEnum.AppUtils, "LOG_TIMESTAMP_FORMAT", "yyyyMMdd HH:mm:ss.SSS z", ConfigType.STRING),
	LOG_TIME_FORMAT(ModulesEnum.AppUtils, "LOG_TIME_FORMAT", "HH:mm:ss.SSS z", ConfigType.STRING),
	LOG_DATE_FORMAT(ModulesEnum.AppUtils, "LOG_DATE_FORMAT", "yyyyMMdd", ConfigType.STRING),
	LOG_LEVEL_UTILS(ModulesEnum.AppUtils, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),

	// Number validation
	ALLOW_INTERNATOINAL_SMS(ModulesEnum.AppUtils, "ALLOW_INTERNATOINAL_SMS", true, ConfigType.BOOLEAN),
	MSISDN_CC(ModulesEnum.AppUtils, "MSISDN_CC", "20", ConfigType.STRING),
	NDC_REGEX(ModulesEnum.AppUtils, "NDC_REGEX", "(10|11|12|15)", ConfigType.STRING),
	MSISDN_SN_LEN(ModulesEnum.AppUtils, "MISDN_SN_LEN", 8, ConfigType.INTEGER),
	MSISDN_NDC_LEN(ModulesEnum.AppUtils, "MISDN_SN_LEN", 2, ConfigType.INTEGER),
	MSISDN_NATIONAL_KEY(ModulesEnum.AppUtils, "MSISDN_NATIONAL_KEY", "0", ConfigType.STRING),
	SHORT_CODE_SENDER_LENGTH(ModulesEnum.AppUtils, "SHORT_CODE_SENDER_LENGTH", 7, ConfigType.INTEGER),
	ALPHANUM_SENDER_LENGTH(ModulesEnum.AppUtils, "ALPHANUM_SENDER_LENGTH", 11, ConfigType.INTEGER),
	FILES_PATH(ModulesEnum.AppUtils, "FILES_PATH", "files/", ConfigType.STRING),

	// AppUtils
	LOG_DIR_NAME(ModulesEnum.AppUtils, "LOG_DIR_NAME", "logs/", ConfigType.STRING),
	LOG_LAYOUT_INFO(
			ModulesEnum.AppUtils, "LOG_LAYOUT_INFO", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n",
			ConfigType.STRING),
	LOG_LAYOUT_DEBUG(
			ModulesEnum.AppUtils, "LOG_LAYOUT_DEBUG", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n",
			ConfigType.STRING),
	WS_CLIENT_CONNECT_TIMEOUT(ModulesEnum.AppUtils, "WS_CLIENT_CONNECT_TIMEOUT", 1000, ConfigType.INTEGER),
	WS_CLIENT_REQUEST_TIMEOUT(ModulesEnum.AppUtils, "WS_CLIENT_REQUEST_TIMEOUT", 5000, ConfigType.INTEGER),
	ALARMING_IDENTIFIER(ModulesEnum.AppUtils, "ALARMING_IDENTIFIER", ConfigType.STRING),
	SMSAPI_ADVANCED_ALARMING_IDENTIFIER(ModulesEnum.AppUtils, "SMSAPI_ADVANCED_ALARMING_IDENTIFIER", ConfigType.STRING),
	APP_REPORTING_ALARMING_IDENTIFIER(ModulesEnum.AppUtils, "APP_REPORTING_ALARMING_IDENTIFIER", ConfigType.STRING),
	ALARM_PATH(ModulesEnum.AppUtils, "ALARM_PATH", "alarms/", ConfigType.STRING),

	//Reporting new server
	ReportingServerBaseDir(ModulesEnum.AppUtils, "ReportingServerBaseDir", "web2sms-appReporting/", ConfigType.STRING),
	AppServerBaseDir(ModulesEnum.AppUtils, "AppServerBaseDir", "web2sms-app/", ConfigType.STRING),

	//AdvancedAPI server
	AppSMSAPIServerBaseDir(ModulesEnum.AppUtils, "AppSMSAPIServerBaseDir", "web2sms-appsmsapi/", ConfigType.STRING),

	// Template Management
	LOG_LEVEL_TEMPLATE(ModulesEnum.TemplateManagement, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),

	// Admin Management
	LOG_LEVEL_ADMIN(ModulesEnum.AdminManagement, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
	LAST_LOGIN_ACTIVE_WINDOW(ModulesEnum.AdminManagement, "LAST_LOGIN_ACTIVE_WINDOW", 30, ConfigType.INTEGER), // min
	ADMIN_RESET_PSW_PERIOD(ModulesEnum.AdminManagement, "ADMIN_REST_PSW_PERIOD", 30, ConfigType.INTEGER), // min
	CORE_CONFIG_WS_URL(
			ModulesEnum.AdminManagement, "CORE_CONFIG_WS_URL",
			"http://localhost:7101/CoreConfigsManagerServiceImpl/CoreConfigsManagerService", ConfigType.STRING),
	APP_CONFIG_WS_URL(
			ModulesEnum.AdminManagement, "APP_CONFIG_WS_URL",
			"http://localhost:7101/AppConfigsManagerServiceImpl/AppConfigsManagerService", ConfigType.STRING),
	UI_CONFIG_WS_URL(
			ModulesEnum.AdminManagement, "UI_CONFIG_WS_URL",
			"http://localhost:7101/UIConfigsManagerServiceImpl/UIConfigsManagerService", ConfigType.STRING),
	SMSAPI_CONFIG_WS_URL(
			ModulesEnum.AdminManagement, "SMSAPI_CONFIG_WS_URL",
			"http://localhost:7101/SMSAPIConfigsManagerServiceImpl/SMSAPIConfigsManagerService", ConfigType.STRING),
	ACCOUNT_MANAGEMENT_CONFIG_WS_URL(
			ModulesEnum.AdminManagement, "ACCOUNT_MANAGEMENT_CONFIG_WS_URL",
			"http://localhost:5001/AccManagConfigsManagerServiceImpl/AccManagConfigsManagerService", ConfigType.STRING),
	REPORTING_MANAGEMENT_CONFIG_WS_URL(
			ModulesEnum.AdminManagement, "REPORTING_MANAGEMENT_CONFIG_WS_URL",
			"http://localhost:5001/ReportingConfigsManagerServiceImpl/ReportingConfigsManagerService",
			ConfigType.STRING),
        PROV_CONFIG_WS_URL(ModulesEnum.AdminManagement, "PROV_CONFIG_WS_URL","http://localhost:6001/ProvConfigsManagerServiceImpl/ProvConfigsManagerService?WSDL", ConfigType.STRING),
	//	SMSGW_CONFIG_WS_URL(ModulesEnum.AdminManagement, "SMSGW_CONFIG_WS_URL","http://localhost:7101/SMSGWConfigsManagerServiceImpl/SMSGWConfigsManagerService", ConfigType.STRING), 
	DOWNLOAD_FILE_SERVER_LINK(ModulesEnum.AdminManagement, "DOWNLOAD_FILE_SERVER_LINK", ConfigType.STRING),
	DOWNLOAD_REPORT_SERVER_LINK(ModulesEnum.AdminManagement, "DOWNLOAD_REPORT_SERVER_LINK","http://localhost:7101/Web2SMS_Reporting_War/reporting/files/getUserReport", ConfigType.STRING),

	//SMS API Management
	MIN_PASSWORD_LENGTH(ModulesEnum.SMSAPIManagement, "MIN_PASSWORD_LENGTH", 6, ConfigType.INTEGER),
	SECURITY_FILES_PATH(ModulesEnum.SMSAPIManagement, "SECURITY_FILES_PATH", "security/", ConfigType.STRING),
	CORE_PROVIDER_URL(ModulesEnum.SMSAPIManagement, "CORE_PROVIDER_URL", "t3://localhost:3574", ConfigType.STRING),
	SUBMIT_SMS_BEAN_JNDI(
			ModulesEnum.SMSAPIManagement, "SUBMIT_SMS_BEAN_JNDI",
			"java:global/Web2SMS-Core/Web2SMS-Core_Main/SubmitSMSBean!com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote",
			ConfigType.STRING),
	LOG_LEVEL_SMSAPI_MANAGEMENT(ModulesEnum.SMSAPIManagement, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
	SUBMIT_SMS_BEAN_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.SMSAPIManagement, "SUBMIT_SMS_BEAN_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	SUBMIT_SMS_BEAN_REMOTE_POOL_MAXIDLE(
			ModulesEnum.SMSAPIManagement, "SUBMIT_SMS_BEAN_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),
	REGESTERED_DELIVERY(ModulesEnum.SMSAPIManagement, "REGESTERED_DELIVERY", true, ConfigType.BOOLEAN),
	MAX_RETURNED_SMSs(ModulesEnum.SMSAPIManagement, "MAX_RETURNED_SMSs", 100, ConfigType.INTEGER),
	SMSAPI_REPORT_TIME_INTERVAL(ModulesEnum.SMSAPIManagement, "SMSAPI_REPORT_TIME_INTERVAL", 60000, ConfigType.INTEGER),
	SMSAPI_ENABLE_REPORT_TIME_INTERVAL(
			ModulesEnum.SMSAPIManagement, "SMSAPI_ENABLE_REPORT_TIME_INTERVAL", false, ConfigType.BOOLEAN),
	SMSAPI_INQUIRE_MONTHES(ModulesEnum.SMSAPIManagement, "SMSAPI_INQUIRE_MONTHES", 6, ConfigType.INTEGER),
	SMSAPI_JPA_TIMEOUT(ModulesEnum.SMSAPIManagement, "SMSAPI_JPA_TIMEOUT", 5000, ConfigType.INTEGER),

	ACCOUNT_MANAGEMENT_PROVIDER(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_MANAGEMENT_PROVIDER", "t3://localhost:5001", ConfigType.STRING),
	ACCOUNT_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_MANAGEMENT_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountManegementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	ACCOUNT_ADMIN_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_ADMIN_MANAGEMENT_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountManegementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	//ACCOUNT_CONVERSION_REMOTE_JNDI(ModulesEnum.ConnectAccoutManag, "ACCOUNT_CONVERSION_REMOTE_JNDI", "java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountConversionBean!com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountConversionBeanRemote", ConfigType.STRING),
	ACCOUNT_CONVERSION_REMOTE_JNDI(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_CONVERSION_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountConversionBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	ACCOUNT_USER_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_USER_MANAGEMENT_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/UserManagementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	ACCOUNT_QUOTA_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_QUOTA_MANAGEMENT_REMOTE",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountQuotaManagementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.ConnectAccoutManag, "ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),
	ACCOUNT_MANAGEMENT_FACING(ModulesEnum.ConnectAccoutManag, "ACCOUNT_MANAGEMENT_FACING", "REMOTE", ConfigType.STRING),

	WS_REQUEST_TIMEOUT(ModulesEnum.AdminManagement, "WS_REQUEST_TIMEOUT", 15000, ConfigType.INTEGER),
	WS_CONNECT_TIMEOUT(ModulesEnum.AdminManagement, "WS_CONNECT_TIMEOUT", 1000, ConfigType.INTEGER),
	REPORT_WEBSERVICE_URL(ModulesEnum.AdminManagement, "REPORT_WEBSERVICE_URL", ConfigType.STRING),

	API_WS_REQUEST_TIMEOUT(ModulesEnum.SMSAPIManagement, "WS_REQUEST_TIMEOUT", 15000, ConfigType.INTEGER),
	API_WS_CONNECT_TIMEOUT(ModulesEnum.SMSAPIManagement, "WS_CONNECT_TIMEOUT", 1000, ConfigType.INTEGER),
	API_REPORT_WEBSERVICE_URL(ModulesEnum.SMSAPIManagement, "REPORT_WEBSERVICE_URL", ConfigType.STRING);

	private static Map<ModulesEnum, Map<String, Configs>> modulesConfigs;

	public static Collection<Configs> getModuleConfigs(ModulesEnum module) {
		if (modulesConfigs == null) {
			modulesConfigs = new HashMap<ModulesEnum, Map<String, Configs>>();
		}
		Map<String, Configs> moduleConfigs = modulesConfigs.get(module);
		if (moduleConfigs == null)
			moduleConfigs = new HashMap<String, Configs>();
		modulesConfigs.put(module, moduleConfigs);
		return moduleConfigs.values();
	}

	public static Map<String, Configs> getModuleConfigsMap(ModulesEnum module) {
		if (modulesConfigs == null) {
			modulesConfigs = new HashMap<ModulesEnum, Map<String, Configs>>();
		}
		Map<String, Configs> moduleConfigs = modulesConfigs.get(module);
		if (moduleConfigs == null)
			moduleConfigs = new HashMap<String, Configs>();
		modulesConfigs.put(module, moduleConfigs);
		return moduleConfigs;
	}

	public static Set<ModulesEnum> getModules() {
		return modulesConfigs.keySet();
	}

	public static Configs getConfig(ModulesEnum module, String property) {
		return getModuleConfigsMap(module).get(property);
	}

	private final String property;
	private final boolean hasDefault;
	private Object value;
	private Object oldValue;
	private ConfigType type;
	private ModulesEnum module;

	Configs(ModulesEnum module, String property, ConfigType type) {
		this.property = property;
		this.hasDefault = false;
		this.type = type;
		this.module = module;
		Configs.getModuleConfigsMap(module).put(property, this);
	}

	Configs(ModulesEnum module, String property, Object value, ConfigType type) throws InvalidConfigValue {
		this.property = property;
		this.hasDefault = true;
		this.type = type;
		setValue(value);
		Configs.getModuleConfigsMap(module).put(property, this);
	}

	public boolean isHasDefault() {
		return hasDefault;
	}

	public void setValue(Object value) throws InvalidConfigValue {
		if (!type.validateType(value))
			throw new InvalidConfigValue(module, property, value, type);
		this.oldValue = this.value;
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public Object getValue() throws ConfigValueNotSetException {
		if (value == null)
			throw new ConfigValueNotSetException(module, property);
		else if (type.equals(ConfigType.LIST))
			return Arrays.asList(((String) value).split(type.getAcceptedValues()[0]));

		return value;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public ConfigType getType() {
		return type;
	}

	public boolean isValidValue(Object value) {
		return type.validateType(value);
	}

	public ModulesEnum getModule() {
		return module;
	}

	public void setModule(ModulesEnum module) {
		this.module = module;
	}

}
