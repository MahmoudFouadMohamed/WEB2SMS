package com.edafa.web2sms.utils.configs.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edafa.web2sms.utils.configs.exception.ConfigValueNotSetException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigValue;

public enum Configs {
	// Logging
        LOG_DIR_NAME(ModulesEnum.SMSAPIUtils, "LOG_DIR_NAME", "logs/", ConfigType.STRING),
	LOG_LEVEL_SMS_API(ModulesEnum.SMSAPI, "LOG_LEVEL", "debug", ConfigType.LOG_LEVEL),
	LOG_LEVEL_UTILES(ModulesEnum.SMSAPIUtils, "LOG_LEVEL", "debug", ConfigType.LOG_LEVEL),
	LOG_LAYOUT_INFO(ModulesEnum.SMSAPIUtils, "LOG_LAYOUT_INFO", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING),
	LOG_LAYOUT_DEBUG(ModulesEnum.SMSAPIUtils, "LOG_LAYOUT_DEBUG", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING),
	WS_CLIENT_CONNECT_TIMEOUT(ModulesEnum.SMSAPIUtils, "WS_CLIENT_CONNECT_TIMEOUT", 1000, ConfigType.INTEGER),
	WS_CLIENT_REQUEST_TIMEOUT(ModulesEnum.SMSAPIUtils, "WS_CLIENT_REQUEST_TIMEOUT", 5000, ConfigType.INTEGER),

	ERRORS_RAISING(ModulesEnum.SMSAPIUtils, "ERRORS_RAISING", false, ConfigType.BOOLEAN),
	ALARMING_IDENTIFIER(ModulesEnum.SMSAPIUtils, "ALARMING_IDENTIFIER", ConfigType.STRING),
	ERRORS_RAISING_SERVICE_URI(ModulesEnum.SMSAPIUtils, "ERRORS_RAISING_SERVICE_URI", ConfigType.STRING),
	ERRORS_CONFIG_SERVICE_URI(ModulesEnum.SMSAPIUtils, "ERRORS_CONFIG_SERVICE_URI", ConfigType.STRING),

	SMS_API_SERVICE_BASE_URI(ModulesEnum.SMSAPI, "SMS_API_SERVICE_BASE_URI", ConfigType.STRING),
	SMS_API_SUBMIT_SMS_SERVICE_PATH(ModulesEnum.SMSAPI, "SMS_API_SUBMIT_SMS_SERVICE_PATH", ConfigType.STRING),
	WEB_METHOD_TO_GET_SMSID_PATH(ModulesEnum.SMSAPI, "WEB_METHOD_TO_GET_SMSID_PATH", ConfigType.STRING),

	SMS_API_SUBMIT_CAMPAIGN_SERVICE_PATH(ModulesEnum.SMSAPI, "SMS_API_SUBMIT_CAMPAIGN_SERVICE_PATH", ConfigType.STRING),
	SMS_API_CAMPAIGN_INQUIRY_SERVICE_URI(ModulesEnum.SMSAPI, "SMS_API_CAMPAIGN_INQUIRY_SERVICE_URI", ConfigType.STRING),

	WEB_METHOD_TO_GET_SMS_BY_ID_LIST(ModulesEnum.SMSAPI, "WEB_METHOD_TO_GET_SMS_BY_ID_LIST", ConfigType.STRING),
	WEB_METHOD_TO_GET_SMSDates_URI(ModulesEnum.SMSAPI, "WEB_METHOD_TO_GET_SMSDates_URI", ConfigType.STRING),
	WEB_METHOD_TO_GET_SMSDatesWithMSISDN_URI(ModulesEnum.SMSAPI, "WEB_METHOD_TO_GET_SMSDatesWithMSISDN_URI", ConfigType.STRING),
	WEB_METHOD_TO_GET_SMSWithinRange_URI(ModulesEnum.SMSAPI, "WEB_METHOD_TO_GET_SMSWithinRange_URI", ConfigType.STRING),
	MAX_OCCURS_FOR_SMS(ModulesEnum.SMSAPI, "MAX_OCCURS_FOR_SMS", 1000, ConfigType.INTEGER),
	MAX_OCCURS_FOR_SMS_IN_CAMP(ModulesEnum.SMSAPI, "MAX_OCCURS_FOR_SMS_IN_CAMP", 1000, ConfigType.INTEGER),
        MAX_OCCURS_FOR_SMS_INQUIRE(ModulesEnum.SMSAPI, "MAX_OCCURS_FOR_SMS_INQUIRE", 250, ConfigType.INTEGER),
	SMS_ID_PREFIX(ModulesEnum.SMSAPI, "SMS_ID_PREFIX", "S", ConfigType.STRING),

        //Validation
        SMSAPI_INQUIRE_YEARS(ModulesEnum.SMSAPIUtils, "SMSAPI_INQUIRE_YEARS", 1, ConfigType.INTEGER),
        
	// MSISDN Validation
	ALLOW_INTERNATOINAL_SMS(ModulesEnum.SMSAPIUtils, "ALLOW_INTERNATOINAL_SMS", true, ConfigType.BOOLEAN),
	MSISDN_CC(ModulesEnum.SMSAPIUtils, "MSISDN_CC", "20", ConfigType.STRING),
	NDC_REGEX(ModulesEnum.SMSAPIUtils, "NDC_REGEX", "(10|11|12|15)", ConfigType.STRING),
	MSISDN_SN_LEN(ModulesEnum.SMSAPIUtils, "MISDN_SN_LEN", 8, ConfigType.INTEGER),
	MSISDN_NDC_LEN(ModulesEnum.SMSAPIUtils, "MISDN_SN_LEN", 2, ConfigType.INTEGER),
	MSISDN_NATIONAL_KEY(ModulesEnum.SMSAPIUtils, "MSISDN_NATIONAL_KEY", "0", ConfigType.STRING),
	SHORT_CODE_SENDER_LENGTH(ModulesEnum.SMSAPIUtils, "SHORT_CODE_SENDER_LENGTH", 7, ConfigType.INTEGER),
	ALPHANUM_SENDER_LENGTH(ModulesEnum.SMSAPIUtils, "ALPHANUM_SENDER_LENGTH", 11, ConfigType.INTEGER),

	//Caching
	CACHE_REQUEST(ModulesEnum.SMSAPI, "CACHE_REQUEST", true, ConfigType.BOOLEAN),
	CACHE_REQUEST_DIRECT(ModulesEnum.SMSAPI, "CACHE_REQUEST_DIRECT", false, ConfigType.BOOLEAN),

	EXPIRED_RECORDS_BASE_DIR(ModulesEnum.SMSAPI, "EXPIRED_RECORDS_BASE_DIR", "", ConfigType.STRING),
	EXPIRED_RECORDS_FILE_NAME(ModulesEnum.SMSAPI, "EXPIRED_RECORDS_FILE_NAME", "expired", ConfigType.STRING),
	EXPIRED_RECORDS_FILE_EXTENTION(ModulesEnum.SMSAPI, "EXPIRED_RECORDS_FILE_EXTENTION", "bin", ConfigType.STRING),
	EXPIRED_RECORDS_FILE_MAX_COUNT(
			ModulesEnum.SMSAPI, "EXPIRED_RECORDS_FILE_MAX_COUNT", 1000 * 1000, ConfigType.INTEGER),
	EXPIRED_RECORDS_FILE_WRITE_CHUNK_COUNT(
			ModulesEnum.SMSAPI, "EXPIRED_RECORDS_FILE_WRITE_CHUNK_COUNT", 1000, ConfigType.INTEGER),
	EXPIRED_RECORDS_FILE_READ_CHUNK_COUNT(
			ModulesEnum.SMSAPI, "EXPIRED_RECORDS_FILE_READ_CHUNK_COUNT", 1000, ConfigType.INTEGER),

	CACHE_RECORDS_BASE_DIR(ModulesEnum.SMSAPI, "CACHE_RECORDS_BASE_DIR", "", ConfigType.STRING),
	CACHE_RECORDS_FILE_NAME(ModulesEnum.SMSAPI, "CACHE_RECORDS_FILE_NAME", "cache", ConfigType.STRING),
	CACHE_RECORDS_FILE_EXTENTION(ModulesEnum.SMSAPI, "CACHE_RECORDS_FILE_EXTENTION", "bin", ConfigType.STRING),
	CACHE_RECORDS_FILE_MAX_COUNT(ModulesEnum.SMSAPI, "CACHE_RECORDS_FILE_MAX_COUNT", 1000 * 1000, ConfigType.INTEGER),
	CACHE_RECORDS_FILE_WRITE_CHUNK_COUNT(
			ModulesEnum.SMSAPI, "CACHE_RECORDS_FILE_WRITE_CHUNK_COUNT", 1000, ConfigType.INTEGER),
	CACHE_RECORDS_FILE_READ_CHUNK_COUNT(
			ModulesEnum.SMSAPI, "CACHE_RECORDS_FILE_READ_CHUNK_COUNT", 1000, ConfigType.INTEGER),
	CACHE_RECORDS_MAX_COUNT(ModulesEnum.SMSAPI, "CACHE_RECORDS_MAX_COUNT", 1000 * 1000, ConfigType.INTEGER),

	//Caching retry 
	CACHE_RETRY_INITIAL_DURATION(ModulesEnum.SMSAPI, "CACHE_RETRY_INITIAL_DURATION", 5000, ConfigType.INTEGER),
	CACHE_RETRY_INTERVAL_DURATION(ModulesEnum.SMSAPI, "CACHE_RETRY_INTERVAL_DURATION", 1000, ConfigType.INTEGER),
	CACHE_RETRY_COUNT(ModulesEnum.SMSAPI, "CACHE_RETRY_COUNT", 3, ConfigType.INTEGER),
	CACHE_REQUEST_VALIDITY_MINUTES(ModulesEnum.SMSAPI, "CACHE_REQUEST_VALIDITY_MINUTES", 10, ConfigType.INTEGER),
	CACHE_RETRY_EXPIRED_COUNT(ModulesEnum.SMSAPI, "CACHE_RETRY_EXPIRED_COUNT", 3, ConfigType.INTEGER),
	CACHE_RETRY_CHUNK_SIZE(ModulesEnum.SMSAPI, "CACHE_RETRY_CHUNK_SIZE", 100, ConfigType.INTEGER),
	CACHE_RETRY_THREAD_COUNT(ModulesEnum.SMSAPI, "CACHE_RETRY_THREAD_COUNT", 3, ConfigType.INTEGER),
	CACHE_RETRY_MINIMUM_SECONDS(ModulesEnum.SMSAPI, "CACHE_RETRY_MINIMUM_SECONDS", 60, ConfigType.INTEGER),

	//Load balancing bean config
	LB_INVALIDITY_PERIOD_MILLIS(ModulesEnum.SMSAPI, "LB_INVALIDITY_PERIOD_MILLIS", 1000, ConfigType.INTEGER),
	LB_MAXIMUM_ERROR_COUNTER(ModulesEnum.SMSAPI, "LB_MAXIMUM_ERROR_COUNTER", 20, ConfigType.INTEGER),
	LB_DEFAULT_WEIGHT(ModulesEnum.SMSAPI, "LB_DEFAULT_WEIGHT", 1, ConfigType.INTEGER),
	LB_INITIAL_CAPACITY(ModulesEnum.SMSAPI, "LB_INITIAL_CAPACITY", 1000, ConfigType.INTEGER),
	LB_MAXIMUM_THROUGHPUT(ModulesEnum.SMSAPI, "LB_MAXIMUM_THROUGHPUT", 0, ConfigType.INTEGER),

	// App load balancing group config
	SMS_API_SERVICE_LB_GROUP_ID(
			ModulesEnum.SMSAPI, "SMS_API_SERVICE_LB_GROUP_NAME", "sms-api-service", ConfigType.STRING),
	SMS_API_SERVICE_LB_GROUP_SEPARATOR(
			ModulesEnum.SMSAPI, "SMS_API_SERVICE_LB_GROUP_SEPARATOR", ",", ConfigType.STRING),
	SMS_API_SERVICE_LB_GROUP_ENTITES(ModulesEnum.SMSAPI, "APP_LB_GROUP_ENTITES", ConfigType.LIST),
	
	SMS_API_LOG_SMS_SERVICE_PATH(ModulesEnum.SMSAPI, "SMS_API_LOG_SMS_SERVICE_PATH", "",  ConfigType.STRING),
	SMS_API_LOG_CAMPAIN_SERVICE_PATH(ModulesEnum.SMSAPI, "SMS_API_LOG_CAMPAIN_SERVICE_PATH", "",  ConfigType.STRING),

    ENCYRPTION_FLAG(ModulesEnum.SMSAPI, "ENCYRPTION_FLAG", true,  ConfigType.BOOLEAN); 

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
