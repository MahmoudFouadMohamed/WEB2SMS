package com.edafa.web2sms.utils.configs.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edafa.web2sms.utils.configs.exception.ConfigValueNotSetException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigValue;

public enum Configs {
	LOG_LEVEL(ModulesEnum.Reporting, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
	LOG_DIR_NAME(ModulesEnum.Reporting, "LOG_DIR_NAME", "logs/", ConfigType.STRING),
	ALARMING_IDENTIFIER(ModulesEnum.Reporting, "ALARMING_IDENTIFIER", "112", ConfigType.STRING),
	ReportingServerBaseDir(ModulesEnum.Reporting, "ReportingServerBaseDir", "web2sms-appReporting/", ConfigType.STRING),

	REPORT_EXPORT_CHUNK_SIZE(ModulesEnum.Reporting, "REPORT_EXPORT_CHUNK_SIZE", 50000, ConfigType.INTEGER),
	EXCEL_WINDOW_SIZE(ModulesEnum.Reporting, "EXCEL_WINDOW_SIZE", 100000, ConfigType.INTEGER),
	EXCEL_SHEET_MAX_ROWS_NUM(ModulesEnum.Reporting, "EXCEL_SHEET_ROWS_NUM", 10000, ConfigType.INTEGER),
	REPORT_EXPORT_CHARSET(ModulesEnum.Reporting, "REPORT_EXPORT_CHARSET", "UTF-8", ConfigType.STRING),
	REPORT_EXPORT_PATH(ModulesEnum.Reporting, "REPORT_EXPORT_PATH", "files/exports/reports/", ConfigType.STRING),
	REPORT_EXPORT_FILE_DELIM(ModulesEnum.Reporting, "REPORT_EXPORT_FILE_DELIM", ",", ConfigType.STRING),
	REPORT_EXPORT_FILE_QUOTE_CHAR(ModulesEnum.Reporting, "REPORT_EXPORT_FILE_QUOTE_CHAR", "\"", ConfigType.STRING),
	REPORT_EXPORT_FILE_COMPRESSED(ModulesEnum.Reporting, "REPORT_EXPORT_FILE_COMPRESSED", true, ConfigType.BOOLEAN),

	OFFLINE_REPORTS_CONCURRENT_MAX_COUNT(
			ModulesEnum.Reporting, "OFFLINE_REPORTS_CONCURRENT_MAX_COUNT", 3, ConfigType.INTEGER),
	OFFLINE_REPORTS_MAX_RETRY_COUNT(ModulesEnum.Reporting, "OFFLINE_REPORTS_MAX_RETRY_COUNT", 20, ConfigType.INTEGER),

	// App DateTimeUtils
	LOG_TIMESTAMP_FORMAT(ModulesEnum.Reporting, "LOG_TIMESTAMP_FORMAT", "yyyyMMdd HH:mm:ss.SSS z", ConfigType.STRING),
	LOG_TIME_FORMAT(ModulesEnum.Reporting, "LOG_TIME_FORMAT", "HH:mm:ss.SSS z", ConfigType.STRING),
	LOG_DATE_FORMAT(ModulesEnum.Reporting, "LOG_DATE_FORMAT", "yyyyMMdd", ConfigType.STRING),
	LOG_LAYOUT_INFO(
			ModulesEnum.Reporting, "LOG_LAYOUT_INFO", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n",
			ConfigType.STRING),
	LOG_LAYOUT_DEBUG(
			ModulesEnum.Reporting, "LOG_LAYOUT_DEBUG", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n",
			ConfigType.STRING),
	ALARM_PATH(ModulesEnum.Reporting, "ALARM_PATH", "alarms/", ConfigType.STRING),

	ACCOUNT_MANAGEMENT_PROVIDER(
			ModulesEnum.Reporting, "ACCOUNT_MANAGEMENT_PROVIDER", "t3://localhost:5001", ConfigType.STRING),
	ACCOUNT_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.Reporting, "ACCOUNT_MANAGEMENT_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountManegementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.Reporting, "ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.Reporting, "ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	ACCOUNT_ADMIN_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.Reporting, "ACCOUNT_ADMIN_MANAGEMENT_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountManegementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.Reporting, "ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.Reporting, "ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	ACCOUNT_CONVERSION_REMOTE_JNDI(
			ModulesEnum.Reporting, "ACCOUNT_CONVERSION_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountConversionBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.Reporting, "ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE(
			ModulesEnum.Reporting, "ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	ACCOUNT_USER_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.Reporting, "ACCOUNT_USER_MANAGEMENT_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/UserManagementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.Reporting, "ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.Reporting, "ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),

	ACCOUNT_QUOTA_MANAGEMENT_REMOTE_JNDI(
			ModulesEnum.Reporting, "ACCOUNT_QUOTA_MANAGEMENT_REMOTE_JNDI",
			"java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountQuotaManagementBeanWrapper",
			ConfigType.STRING),
	ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXTOTAL(
			ModulesEnum.Reporting, "ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
	ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXIDLE(
			ModulesEnum.Reporting, "ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),
	ACCOUNT_MANAGEMENT_FACING(ModulesEnum.Reporting, "ACCOUNT_MANAGEMENT_FACING", "REMOTE", ConfigType.STRING),;

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
