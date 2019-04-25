package com.edafa.web2sms.utils.configs.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edafa.web2sms.utils.configs.exception.ConfigValueNotSetException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigValue;

public enum Configs {
	// Campaign Engine

    // App CoreUtils
    LOG_LEVEL_UTILS(ModulesEnum.CoreUtils, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
    THREAD_EXCEPTION_SLEEP_TIME(ModulesEnum.CoreUtils, "THREAD_EXCEPTION_SLEEP_TIME", 2000, ConfigType.INTEGER),
    ALARMING_IDENTIFIER(ModulesEnum.CoreUtils, "ALARMING_IDENTIFIER", ConfigType.STRING),
    ACTIVATE_SCHEDULER(ModulesEnum.CoreUtils, "ACTIVATE_SCHEDULER", true, ConfigType.BOOLEAN),
    ALARM_PATH(ModulesEnum.CoreUtils, "ALARM_PATH", "alarms/", ConfigType.STRING),
    // Number validation
    ALLOW_INTERNATOINAL_SMS(ModulesEnum.CoreUtils, "ALLOW_INTERNATOINAL_SMS", false, ConfigType.BOOLEAN),
    NDC_REGEX(ModulesEnum.CoreUtils, "NDC_REGEX", "(10|11|12|15)", ConfigType.STRING),
    MSISDN_SN_LEN(ModulesEnum.CoreUtils, "MISDN_SN_LEN", 8, ConfigType.INTEGER),
    MSISDN_NDC_LEN(ModulesEnum.CoreUtils, "MISDN_SN_LEN", 2, ConfigType.INTEGER),
    // Logging
    LOG_DIR_NAME(ModulesEnum.CoreUtils, "LOG_DIR_NAME", "logs/", ConfigType.STRING),
    LOG_LAYOUT_INFO(ModulesEnum.CoreUtils, "LOG_LAYOUT_INFO", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING),
    LOG_LAYOUT_DEBUG(ModulesEnum.CoreUtils, "LOG_LAYOUT_DEBUG", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING),
    LOG_TIMESTAMP_FORMAT(ModulesEnum.CoreUtils, "LOG_TIMESTAMP_FORMAT", "yyyyMMdd HH:mm:ss.SSS z", ConfigType.STRING),
    LOG_TIME_FORMAT(ModulesEnum.CoreUtils, "LOG_TIME_FORMAT", "HH:mm:ss.SSS z", ConfigType.STRING),
    LOG_DATE_FORMAT(ModulesEnum.CoreUtils, "LOG_DATE_FORMAT", "yyyyMMdd", ConfigType.STRING),
    // Campaign Engine
    LOG_LEVEL_CAMP_ENG(ModulesEnum.CampaignEngine, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
    CAMP_DISPACHER_CHEKING_PERIOD(ModulesEnum.CampaignEngine, "CAMP_DISPACHER_CHEKING_PERIOD", 10000, ConfigType.INTEGER),
    CAMP_UPDATE_CHUNK_SIZE(ModulesEnum.CampaignEngine, "CAMP_UPDATE_CHUNK_SIZE", 50, ConfigType.INTEGER),
    CAMP_EXE_VALIDITY_PERIOD(ModulesEnum.CampaignEngine, "CAMP_EXECUTION_VALIDITY_PERIOD", 1, ConfigType.INTEGER), // hour
    CAMP_APPROVAL_VALIDITY_PERIOD(ModulesEnum.CampaignEngine, "CAMP_APPROVAL_VALIDITY_PERIOD", 1, ConfigType.INTEGER), // hour
    CAMP_SEND_AT_APPROVAL(ModulesEnum.CampaignEngine, "CAMP_SEND_AT_APPROVAL", true, ConfigType.BOOLEAN),
    CAMP_VALIDITY_PERIOD(ModulesEnum.CampaignEngine, "CAMP_VALIDITY_PERIOD", 168, ConfigType.INTEGER), // hour
    CAMPAIGNS_HANDLER_WAITING_TIME(ModulesEnum.CampaignEngine, "CAMPAIGNS_HANDLER_WAITING_TIME", 3000, ConfigType.INTEGER),
    CAMPAIGNS_HANDLER_THREAD_COUNT(ModulesEnum.CampaignEngine, "CAMPAIGNS_HANDLER_THREAD_COUNT", 1, ConfigType.INTEGER),
    BULK_SMS_SEND_COUNT(ModulesEnum.CampaignEngine, "BULK_SMS_SEND_COUNT", 100, ConfigType.INTEGER),
    CONTACTS_BUFFER_SIZE(ModulesEnum.CampaignEngine, "CONTACTS_BUFFER_SIZE", 1000, ConfigType.INTEGER),
    CAMP_HANDLER_WAITING_TIMEOUT(ModulesEnum.CampaignEngine, "CAMP_HANDLER_WAITING_TIMEOUT", 5000, ConfigType.INTEGER),
    SMPP_MODULE_READINESS_WAITING_TIME(ModulesEnum.CampaignEngine, "SMPP_MODULE_READINESS_WAITING_TIME", 5000, ConfigType.INTEGER),
    SMS_CAMPAIGN_SENDING_RATE(ModulesEnum.CampaignEngine,"CAMPAIGN_SENDING_RATE", 1, ConfigType.INTEGER),
    CAMPAIGN_DEFAULT_LIMITER(ModulesEnum.CampaignEngine, "CAMPAIGN_DEFAULT_LIMITER", 1, ConfigType.INTEGER),
    // CDRs YYYYMMDD_HH24-MI
    AGG_CDR_FILENAME_PREFIX(ModulesEnum.CampaignEngine, "AGG_CDR_FILENAME_PREFIX", "web2sms_agg_cdr_", ConfigType.STRING),
    AGG_CDR_FILENAME_TIMESTAMP_FORMAT(ModulesEnum.CampaignEngine, "AGG_CDR_FILENAME_TIMESTAMP_FORMAT", "yyyyMMdd_HH-mm", ConfigType.STRING),
    AGG_CDR_PATH(ModulesEnum.CampaignEngine, "AGG_CDR_PATH", "cdrs/agg_cdr/", ConfigType.STRING),
    AGG_CDR_ON_NET_PREFIX(ModulesEnum.CampaignEngine, "AGG_CDR_ON_NET_PREFIX", "2010", ConfigType.LIST),
    AGG_CDR_MOB_NET_PREFIX(ModulesEnum.CampaignEngine, "AGG_CDR_MOB_NET_PREFIX", "2012", ConfigType.LIST),
    AGG_CDR_ETI_NET_PREFIX(ModulesEnum.CampaignEngine, "AGG_CDR_ETI_NET_PREFIX", "2011", ConfigType.LIST),
    AGG_CDR_MASR_NET_PREFIX(ModulesEnum.CampaignEngine, "AGG_CDR_MASR_NET_PREFIX", "2015", ConfigType.LIST),
    AGG_CDR_ON_NET_STR(ModulesEnum.CampaignEngine, "AGG_CDR_ON_NET_STR", "WEB2SMSONNET", ConfigType.STRING),
    AGG_CDR_MOB_NET_STR(ModulesEnum.CampaignEngine, "AGG_CDR_MOB_NET_STR", "WEB2SMSMOB", ConfigType.STRING),
    AGG_CDR_ETI_NET_STR(ModulesEnum.CampaignEngine, "AGG_CDR_ETI_NET_STR", "WEB2SMSET", ConfigType.STRING),
    AGG_CDR_MASR_NET_STR(ModulesEnum.CampaignEngine, "AGG_CDR_MASR_NET_STR", "WEB2SMSMASR", ConfigType.STRING),
    AGG_CDR_TIMESTAMP_FORMAT(ModulesEnum.CampaignEngine, "AGG_CDR_TIMESTAMP_FORMAT", "yyyyMMddHHmmss", ConfigType.STRING),
    AGG_CDR_CHUNK_SIZE(ModulesEnum.CampaignEngine, "AGG_CDR_CHUNK_SIZE", 1000, ConfigType.INTEGER),
    AGG_CDR_DELIMITER(ModulesEnum.CampaignEngine, "AGG_CDR_DELIMITER", ",", ConfigType.STRING),
    AGG_CDR_EOF_NEW_LINE(ModulesEnum.CampaignEngine, "AGG_CDR_EOF_NEW_LINE", true, ConfigType.BOOLEAN),
    AGG_CDR_EXTENTION(ModulesEnum.CampaignEngine, "AGG_CDR_EXTENTION", ".cdr", ConfigType.STRING),
    DETAILED_CDR_FILENAME_PREFIX(ModulesEnum.CampaignEngine, "DETAILED_CDR_FILENAME_PREFIX", "web2sms_detailed_cdr_", ConfigType.STRING),
    DETAILED_CDR_FILENAME_TIMESTAMP_FORMAT(ModulesEnum.CampaignEngine, "DETAILED_CDR_FILENAME_TIMESTAMP_FORMAT", "yyyyMMdd_HH-mm", ConfigType.STRING),
    DETAILED_CDR_PATH(ModulesEnum.CampaignEngine, "DETAILED_CDR_PATH", "cdrs/detailed_cdr/", ConfigType.STRING),
    DETAILED_CDR_TIMESTAMP_FORMAT(ModulesEnum.CampaignEngine, "DETAILED_CDR_TIMESTAMP_FORMAT", "yyyyMMddHHmmss", ConfigType.STRING),
    DETAILED_CDR_CHUNK_SIZE(ModulesEnum.CampaignEngine, "DETAILED_CDR_CHUNK_SIZE", 50000, ConfigType.INTEGER),
    DETAILED_CDR_DELIMITER(ModulesEnum.CampaignEngine, "DETAILED_CDR_DELIMITER", ",", ConfigType.STRING),
    DETAILED_CDR_EOF_NEW_LINE(ModulesEnum.CampaignEngine, "DETAILED_CDR_EOF_NEW_LINE", true, ConfigType.BOOLEAN),
    DETAILED_CDR_EXTENTION(ModulesEnum.CampaignEngine, "DETAILED_CDR_EXTENTION", ".csv", ConfigType.STRING),
    CDR_TASK_MON_TIMER_PERIOD(ModulesEnum.CoreUtils, "CDR_TASK_MON_TIMER_PERIOD", 600000, ConfigType.INTEGER),
    SUBMIT_TO_SMPP_WAITING_PERIOD(ModulesEnum.SMSAPIEngine, "SUBMIT_TO_SMPP_WAITING_PERIOD", 5000, ConfigType.INTEGER),
    SUBMIT_SMS_WAITING_TIME(ModulesEnum.SMSAPIEngine, "SUBMIT_SMS_WAITING_TIME", 5, ConfigType.INTEGER),
    SMSAPI_REGESTERED_DELIVERY(ModulesEnum.SMSAPIEngine, "SMSAPI_REGESTERED_DELIVERY", true, ConfigType.BOOLEAN),
    SMSAPI_QUEUE_SIZE(ModulesEnum.SMSAPIEngine, "SMSAPI_QUEUE_SIZE", 5000, ConfigType.INTEGER),
    SMSAPI_PENDING_LIST_SIZE(ModulesEnum.SMSAPIEngine, "SMSAPI_PENDING_LIST_SIZE", 500000, ConfigType.INTEGER),
    SMSAPI_QUEUE_POLL_TIMEOUT(ModulesEnum.SMSAPIEngine, "SMSAPI_QUEUE_POLL_TIMEOUT", 1, ConfigType.INTEGER),
    SMSAPI_QUEUE_OFFER_TIMEOUT(ModulesEnum.SMSAPIEngine, "SMSAPI_QUEUE_OFFER_TIMEOUT", 20, ConfigType.INTEGER),
    SMSAPI_ASYNCH_SENDING(ModulesEnum.SMSAPIEngine, "SMSAPI_ASYNCH_SENDING", false, ConfigType.BOOLEAN),
    LOG_LEVEL_SMSAPI_ENGINE(ModulesEnum.SMSAPIEngine, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
    SMSAPI_MAX_CONCURRENT_PROCESSING_SMS_PER_ACCOUNT(ModulesEnum.SMSAPIEngine, "SMSAPI_MAX_CONCURRENT_PROCESSING_SMS_PER_ACCOUNT", 1000, ConfigType.INTEGER),
    SMS_SMSAPI_SENDING_RATE(ModulesEnum.SMSAPIEngine,"SMSAPI_SENDING_RATE", 1, ConfigType.INTEGER),
    SMSAPI_DEFAULT_LIMITER(ModulesEnum.SMSAPIEngine, "SMSAPI_DEFAULT_LIMITER", 1, ConfigType.INTEGER),
    BILLNG_MSISDN_SEQ_NUM(ModulesEnum.AggCDRConfig, "BILLNG_MSISDN_SEQ_NUM", 0, ConfigType.INTEGER),
    DUMMY_6020_SEQ_NUM(ModulesEnum.AggCDRConfig, "DUMMY_6020_SEQ_NUM", 1, ConfigType.INTEGER),
    COMPANY_ID_SEQ_NUM(ModulesEnum.AggCDRConfig, "COMPANY_ID_SEQ_NUM", 2, ConfigType.INTEGER),
    SENDER_SEQ_NUM(ModulesEnum.AggCDRConfig, "SENDER_SEQ_NUM", 3, ConfigType.INTEGER),
    START_TIME_SEQ_NUM(ModulesEnum.AggCDRConfig, "START_TIME_SEQ_NUM", 4, ConfigType.INTEGER),
    MSISDN_TYPE_SEQ_NUM(ModulesEnum.AggCDRConfig, "MSISDN_TYPE_SEQ_NUM", 5, ConfigType.INTEGER),
    DUMMY_13_SEQ_NUM(ModulesEnum.AggCDRConfig, "DUMMY_13_SEQ_NUM", 13, ConfigType.INTEGER),
    AGG_SMS_SEG_COUNT_SEQ_NUM(ModulesEnum.AggCDRConfig, "AGG_SMS_SEG_COUNT_SEQ_NUM", 15, ConfigType.INTEGER),
    CAMPAIGN_NAME_SEQ_NUM(ModulesEnum.DetailedCDRConfig, "CAMPAIGN_NAME_SEQ_NUM", 0, ConfigType.INTEGER),
    DETAILED_BILLING_MSISDN_SEQ_NUM(ModulesEnum.DetailedCDRConfig, "DETAILED_BILLING_MSISDN_SEQ_NUM", 1, ConfigType.INTEGER),
    RECEIVER_ID_SEQ_NUM(ModulesEnum.DetailedCDRConfig, "RECEIVER_ID_SEQ_NUM", 2, ConfigType.INTEGER),
    DETAILED_SENDER_SEQ_NUM(ModulesEnum.DetailedCDRConfig, "DETAILED_SENDER_SEQ_NUM", 3, ConfigType.INTEGER),
    PROCESSING_DATE_SEQ_NUM(ModulesEnum.DetailedCDRConfig, "PROCESSING_DATE_SEQ_NUM", 4, ConfigType.INTEGER),
    STATUS_SEQ_NUM(ModulesEnum.DetailedCDRConfig, "STATUS_SEQ_NUM", 5, ConfigType.INTEGER),
    SEG_COUNT_SEQ_NUM(ModulesEnum.DetailedCDRConfig, "SEG_COUNT_SEQ_NUM", 6, ConfigType.INTEGER);
    
    private static Map<ModulesEnum, Map<String, Configs>> modulesConfigs;

    public static Collection<Configs> getModuleConfigs(ModulesEnum module) {
        if (modulesConfigs == null) {
            modulesConfigs = new HashMap<ModulesEnum, Map<String, Configs>>();
        }
        Map<String, Configs> moduleConfigs = modulesConfigs.get(module);
        if (moduleConfigs == null) {
            moduleConfigs = new HashMap<String, Configs>();
        }
        modulesConfigs.put(module, moduleConfigs);
        return moduleConfigs.values();
    }

    public static Map<String, Configs> getModuleConfigsMap(ModulesEnum module) {
        if (modulesConfigs == null) {
            modulesConfigs = new HashMap<ModulesEnum, Map<String, Configs>>();
        }
        Map<String, Configs> moduleConfigs = modulesConfigs.get(module);
        if (moduleConfigs == null) {
            moduleConfigs = new HashMap<String, Configs>();
        }
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
        if (!type.validateType(value)) {
            throw new InvalidConfigValue(module, property, value, type);
        }
        this.oldValue = this.value;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public Object getValue() throws ConfigValueNotSetException {
        if (value == null) {
            throw new ConfigValueNotSetException(module, property);
        } else if (type.equals(ConfigType.LIST)) {
            return Arrays.asList(((String) value).split(type.getAcceptedValues()[0]));
        }

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
