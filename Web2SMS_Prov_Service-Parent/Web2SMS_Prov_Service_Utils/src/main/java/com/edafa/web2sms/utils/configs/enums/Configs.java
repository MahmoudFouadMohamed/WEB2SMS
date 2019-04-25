package com.edafa.web2sms.utils.configs.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edafa.web2sms.utils.configs.exception.ConfigValueNotSetException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigValue;

public enum Configs {

    // Provisioning
    LOG_LEVEL_PROV(ModulesEnum.Provisioning, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
    CLOUD_CALL_BACK_SERVICE_URI(ModulesEnum.Provisioning, "CLOUD_CALL_BACK_SERVICE_URI", ConfigType.STRING),
    CLOUD_PROV_REQ_TIMEOUT(ModulesEnum.Provisioning, "CLOUD_PROV_REQ_TIMEOUT", 60000, ConfigType.INTEGER),
    CLOUD_CALL_BACK_BASE_URI(ModulesEnum.Provisioning, "CLOUD_CALL_BACK_BASE_URI", ConfigType.STRING),
    CLOUD_CALL_BACK_WATING_TIME(ModulesEnum.Provisioning, "CLOUD_CALL_BACK_WATING_TIME", 60000, ConfigType.INTEGER), // ms

    //Tibco Request parameters ,,  
    //Configs(ModulesEnum module, 	 String property, Object value, ConfigType type) 
    TIBCO_CALL_BILLING_USERNAME(ModulesEnum.Provisioning, "TIBCO_CALL_BILLING_USERNAME", ConfigType.STRING),
    TIBCO_CALL_BILLING_PASSWORD(ModulesEnum.Provisioning, "TIBCO_CALL_BILLING_PASSWORD", ConfigType.STRING),
    TIBCO_CALL_DAYS_TO_REACTIVATE(ModulesEnum.Provisioning, "TIBCO_CALL_DAYS_TO_REACTIVATE", ConfigType.STRING),
    TIBCO_CALL_SUSPEND_REASON_CODE(ModulesEnum.Provisioning, "TIBCO_CALL_SUSPEND_REASON_CODE", ConfigType.STRING),
    TIBCO_CALL_DEACTIVATE_REASON_CODE(ModulesEnum.Provisioning, "TIBCO_CALL_DEACTIVATE_REASON_CODE", ConfigType.STRING),
    TIBCO_CALL_URL_IP(ModulesEnum.Provisioning, "TIBCO_CALL_URL_IP", ConfigType.STRING),
    TIBCO_CALL_URL_PORT(ModulesEnum.Provisioning, "TIBCO_CALL_URL_PORT", ConfigType.STRING),
    TIBCO_CALL_URL_RESOURCE(ModulesEnum.Provisioning, "TIBCO_CALL_URL_RESOURCE", ConfigType.STRING),
    TIBCO_CALL_SUSPEND_STATUS(ModulesEnum.Provisioning, "TIBCO_CALL_SUSPEND_STATUS", ConfigType.STRING),
    TIBCO_CALL_DEACTIVATE_STATUS(ModulesEnum.Provisioning, "TIBCO_CALL_DEACTIVATE_STATUS", ConfigType.STRING),
    TIBCO_CALL_CONNECTION_TIMEOUT(ModulesEnum.Provisioning, "TIBCO_CALL_CONNECTION_TIMEOUT", 60000, ConfigType.INTEGER),
    TIBCO_CALL_REQUEST_TIMEOUT(ModulesEnum.Provisioning, "TIBCO_CALL_REQUEST_TIMEOUT", 50000, ConfigType.INTEGER),
    // Provisioning - SR
    PROV_SR_SERVICE_URI(ModulesEnum.Provisioning, "PROV_SR_SERVICE_URI", ConfigType.STRING),
    PROV_ACTIVATE_SR_CATEGORY(ModulesEnum.Provisioning, "PROV_ACTIVATE_SR_CATEGORY", ConfigType.STRING),
    PROV_ACTIVATE_SR_CASE(ModulesEnum.Provisioning, "PROV_ACTIVATE_SR_CASE", ConfigType.STRING),
    PROV_ACTIVATE_SR_TYPE(ModulesEnum.Provisioning, "PROV_ACTIVATE_SR_TYPE", ConfigType.STRING),
    PROV_ACTIVATE_SR_TEAM(ModulesEnum.Provisioning, "PROV_ACTIVATE_SR_OWNER_TEAM", ConfigType.STRING),
    PROV_ACTIVATE_SR_CUSTOMER_TYPE(ModulesEnum.Provisioning, "PROV_ACTIVATE_SR_CUSTOMER_TYPE", ConfigType.STRING),
    PROV_ROLL_BACK_SR_CATEGORY(ModulesEnum.Provisioning, "PROV_ROLL_BACK_SR_CATEGORY", ConfigType.STRING),
    PROV_ROLL_BACK_SR_CASE(ModulesEnum.Provisioning, "PROV_ROLL_BACK_SR_CASE", ConfigType.STRING),
    PROV_ROLL_BACK_SR_TYPE(ModulesEnum.Provisioning, "PROV_ROLL_BACK_SR_TYPE", ConfigType.STRING),
    PROV_ROLL_BACK_SR_TEAM(ModulesEnum.Provisioning, "PROV_ROLL_BACK_SR_OWNER_TEAM", ConfigType.STRING),
    PROV_ROLL_BACK_SR_CUSTOMER_TYPE(ModulesEnum.Provisioning, "PROV_ROLL_BACK_SR_CUSTOMER_TYPE", ConfigType.STRING),
    PROV_UPGRADE_SR_CATEGORY(ModulesEnum.Provisioning, "PROV_UPGRADE_SR_CATEGORY", ConfigType.STRING),
    PROV_UPGRADE_SR_CASE(ModulesEnum.Provisioning, "PROV_UPGRADE_SR_CASE", ConfigType.STRING),
    PROV_UPGRADE_SR_TYPE(ModulesEnum.Provisioning, "PROV_UPGRADE_SR_TYPE", ConfigType.STRING),
    PROV_UPGRADE_SR_TEAM(ModulesEnum.Provisioning, "PROV_UPGRADE_SR_OWNER_TEAM", ConfigType.STRING),
    PROV_UPGRADE_SR_CUSTOMER_TYPE(ModulesEnum.Provisioning, "PROV_UPGRADE_SR_CUSTOMER_TYPE", ConfigType.STRING),
    PROV_DOWNGRADE_SR_CATEGORY(ModulesEnum.Provisioning, "PROV_DOWNGRADE_SR_CATEGORY", ConfigType.STRING),
    PROV_DOWNGRADE_SR_CASE(ModulesEnum.Provisioning, "PROV_DOWNGRADE_SR_CASE", ConfigType.STRING),
    PROV_DOWNGRADE_SR_TYPE(ModulesEnum.Provisioning, "PROV_DOWNGRADE_SR_TYPE", ConfigType.STRING),
    PROV_DOWNGRADE_SR_TEAM(ModulesEnum.Provisioning, "PROV_DOWNGRADE_SR_OWNER_TEAM", ConfigType.STRING),
    PROV_DOWNGRADE_SR_CUSTOMER_TYPE(ModulesEnum.Provisioning, "PROV_DOWNGRADE_SR_CUSTOMER_TYPE", ConfigType.STRING),
    PROV_CHANGE_SENDER_SR_CATEGORY(ModulesEnum.Provisioning, "PROV_CHANGE_SENDER_SR_CATEGORY", ConfigType.STRING),
    PROV_CHANGE_SENDER_SR_CASE(ModulesEnum.Provisioning, "PROV_CHANGE_SENDER_SR_CASE", ConfigType.STRING),
    PROV_CHANGE_SENDER_SR_TYPE(ModulesEnum.Provisioning, "PROV_CHANGE_SENDER_SR_TYPE", ConfigType.STRING),
    PROV_CHANGE_SENDER_SR_TEAM(ModulesEnum.Provisioning, "PROV_CHANGE_SENDER_SR_OWNER_TEAM", ConfigType.STRING),
    PROV_CHANGE_SENDER_SR_CUSTOMER_TYPE(ModulesEnum.Provisioning, "PROV_CHANGE_SENDER_SR_CUSTOMER_TYPE", ConfigType.STRING),
    PROV_ADD_SENDER_SR_CATEGORY(ModulesEnum.Provisioning, "PROV_ADD_SENDER_SR_CATEGORY", ConfigType.STRING),
    PROV_ADD_SENDER_SR_CASE(ModulesEnum.Provisioning, "PROV_ADD_SENDER_SR_CASE", ConfigType.STRING),
    PROV_ADD_SENDER_SR_TYPE(ModulesEnum.Provisioning, "PROV_ADD_SENDER_SR_TYPE", ConfigType.STRING),
    PROV_ADD_SENDER_SR_TEAM(ModulesEnum.Provisioning, "PROV_ADD_SENDER_SR_OWNER_TEAM", ConfigType.STRING),
    PROV_ADD_SENDER_SR_CUSTOMER_TYPE(ModulesEnum.Provisioning, "PROV_ADD_SENDER_SR_CUSTOMER_TYPE", ConfigType.STRING),
    PROV_SMART_SCRIPT_MSISDN_FIELD_NUM(ModulesEnum.Provisioning, "PROV_SMART_SCRIPT_MSISDN_FIELD_NUM", ConfigType.INTEGER),
    PROV_SMART_SCRIPT_ACCTADMIN_FIELD_NUM(ModulesEnum.Provisioning, "PROV_SMART_SCRIPT_ACCTADMIN_FIELD_NUM", ConfigType.INTEGER),
    PROV_SMART_SCRIPT_COMPANY_FIELD_NUM(ModulesEnum.Provisioning, "PROV_SMART_SCRIPT_COMPANY_FIELD_NUM", ConfigType.INTEGER),
    
    // App
    APP_PROVIDER_URL(ModulesEnum.Provisioning, "APP_PROVIDER_URL", ConfigType.STRING),
    CAMPAIGN_MANAGEMENT_BEAN_JNDI(ModulesEnum.Provisioning, "CAMPAIGN_MANAGEMENT_BEAN_JNDI", "java:global/Web2SMS-App/Web2SMS_App_Service/CampaignManagementBean!com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanRemote", ConfigType.STRING),
    
    
    // Account Management
    ACCOUNT_MANAGEMENT_PROVIDER(ModulesEnum.Provisioning, "ACCOUNT_MANAGEMENT_PROVIDER", "t3://localhost:5001", ConfigType.STRING),
    ACCOUNT_MANAGEMENT_REMOTE_JNDI(ModulesEnum.Provisioning, "ACCOUNT_MANAGEMENT_REMOTE_JNDI", "java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountManegementBeanWrapper", ConfigType.STRING),
    ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL(ModulesEnum.Provisioning, "ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
    ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE(ModulesEnum.Provisioning, "ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),
    ACCOUNT_CONVERSION_REMOTE_JNDI(ModulesEnum.Provisioning, "ACCOUNT_CONVERSION_REMOTE_JNDI", "java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/AccountConversionBeanWrapper", ConfigType.STRING),
    ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL(ModulesEnum.Provisioning, "ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
    ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE(ModulesEnum.Provisioning, "ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),
    ACCOUNT_USER_MANAGEMENT_REMOTE_JNDI(ModulesEnum.Provisioning, "ACCOUNT_USER_MANAGEMENT_REMOTE_JNDI", "java:global/Web2SMS_Account_Management/Web2SMS_Account_Management_Main/UserManagementBeanWrapper", ConfigType.STRING),
    ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXTOTAL(ModulesEnum.Provisioning, "ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXTOTAL", 128, ConfigType.INTEGER),
    ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXIDLE(ModulesEnum.Provisioning, "ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXIDLE", 64, ConfigType.INTEGER),
    ACCOUNT_MANAGEMENT_FACING(ModulesEnum.Provisioning, "ACCOUNT_MANAGEMENT_FACING", "REMOTE", ConfigType.STRING),
    

    // Prov Utils
    // Log
    LOG_TIMESTAMP_FORMAT(ModulesEnum.ProvUtils, "LOG_TIMESTAMP_FORMAT", "yyyyMMdd HH:mm:ss.SSS z", ConfigType.STRING),
    LOG_TIME_FORMAT(ModulesEnum.ProvUtils, "LOG_TIME_FORMAT", "HH:mm:ss.SSS z", ConfigType.STRING), 
    LOG_DATE_FORMAT(ModulesEnum.ProvUtils, "LOG_DATE_FORMAT", "yyyyMMdd", ConfigType.STRING), 
    LOG_LEVEL_UTILS(ModulesEnum.ProvUtils, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
                    
    LOG_DIR_NAME(ModulesEnum.ProvUtils, "LOG_DIR_NAME", "logs/", ConfigType.STRING),
    LOG_LAYOUT_INFO(ModulesEnum.ProvUtils, "LOG_LAYOUT_INFO", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING),
    LOG_LAYOUT_DEBUG(ModulesEnum.ProvUtils, "LOG_LAYOUT_DEBUG", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING),
    
    // Number validation
    MSISDN_CC(ModulesEnum.ProvUtils, "MSISDN_CC", "20", ConfigType.STRING),
    NDC_REGEX(ModulesEnum.ProvUtils, "NDC_REGEX", "(10|11|12|15)", ConfigType.STRING),
    MSISDN_SN_LEN(ModulesEnum.ProvUtils, "MISDN_SN_LEN", 8, ConfigType.INTEGER),
    MSISDN_NDC_LEN(ModulesEnum.ProvUtils, "MISDN_SN_LEN", 2, ConfigType.INTEGER),
    MSISDN_NATIONAL_KEY(ModulesEnum.ProvUtils, "MSISDN_NATIONAL_KEY", "0", ConfigType.STRING),
    SHORT_CODE_SENDER_LENGTH(ModulesEnum.ProvUtils, "SHORT_CODE_SENDER_LENGTH", 7, ConfigType.INTEGER),
    ALPHANUM_SENDER_LENGTH(ModulesEnum.ProvUtils, "ALPHANUM_SENDER_LENGTH", 11, ConfigType.INTEGER),
    
    // WS clients
    WS_CLIENT_CONNECT_TIMEOUT(ModulesEnum.ProvUtils, "WS_CLIENT_CONNECT_TIMEOUT", 1000, ConfigType.INTEGER),
    WS_CLIENT_REQUEST_TIMEOUT(ModulesEnum.ProvUtils, "WS_CLIENT_REQUEST_TIMEOUT", 5000, ConfigType.INTEGER),
    
    //Alarming
    ALARMING_IDENTIFIER(ModulesEnum.ProvUtils, "ALARMING_IDENTIFIER", ConfigType.STRING),
    
    ALARM_PATH(ModulesEnum.ProvUtils, "ALARM_PATH", "alarms/", ConfigType.STRING),
    PROV_SERVICE_BASE_DIR(ModulesEnum.ProvUtils, "PROV_SERVICE_BASE_DIR", "web2sms-prov-service/", ConfigType.STRING);

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
