package com.edafa.web2sms.acc_manag.utils.configs.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edafa.web2sms.acc_manag.utils.configs.exception.ConfigValueNotSetException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.InvalidConfigValue;

public enum Configs {

    // Account Management
// Account Management
    LOG_LEVEL_ACCT(AccountManagModulesEnum.AccountManagement, "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
    
    PASSWORD_EXPIRE_PERIOD(AccountManagModulesEnum.AccountManagement, "PASSWORD_EXPIRE_PERIOD", 60, ConfigType.INTEGER), // in days
    MAX_FAILED_LOGINS(AccountManagModulesEnum.AccountManagement, "MAX_FAILED_LOGINS", 5, ConfigType.INTEGER),
    MAX_FAILED_TEMP_PASSWORD(AccountManagModulesEnum.AccountManagement, "MAX_FAILED_TEMP_PASSWORD", 5, ConfigType.INTEGER),
    TEMP_PASSWORD_EXPIRE_PERIOD(AccountManagModulesEnum.AccountManagement, "TEMP_PASSWORD_EXPIRE_PERIOD", 10, ConfigType.INTEGER), // in minutes    
    KEEP_OLD_PASSWORD_COUNT(AccountManagModulesEnum.AccountManagement, "KEEP_OLD_PASSWORD_COUNT", 3, ConfigType.INTEGER),
    ENABLE_DIRECT_LOGIN(AccountManagModulesEnum.AccountManagement, "ENABLE_DIRECT_LOGIN", false, ConfigType.BOOLEAN),
    
    CORE_PROVIDER_URL(AccountManagModulesEnum.AccountManagUtils, "CORE_PROVIDER_URL", ConfigType.STRING),
    SUBMIT_SMS_BEAN_JNDI(AccountManagModulesEnum.AccountManagUtils, "SUBMIT_SMS_BEAN_JNDI", ConfigType.STRING),
    TEMP_PASS_SMS_BODY_DEFAULT_LANG(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_SMS_BODY_DEFAULT_LANG", "en", ConfigType.STRING),
    TEMP_PASS_SMS_BODY_AR(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_SMS_BODY_AR", ConfigType.STRING),
    TEMP_PASS_SMS_BODY_EN(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_SMS_BODY_EN", ConfigType.STRING),
    TEMP_PASS_GENERATION_PATTERNS(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_GENERATION_PATTERNS", "digit|lower", ConfigType.LIST),
    TEMP_PASS_LENGTH(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_LENGTH", 8, ConfigType.INTEGER),
    TEMP_PASS_SMS_SENDER(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_SMS_SENDER", ConfigType.STRING),
    TEMP_PASS_SMS_ID_PRIFIX(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_SMS_ID_PRIFIX", ConfigType.STRING),
    TEMP_PASS_SMS_ACC_ID(AccountManagModulesEnum.AccountManagUtils, "TEMP_PASS_SMS_ACC_ID", "web2sms", ConfigType.STRING),
    // App DateTimeUtils
    LOG_TIMESTAMP_FORMAT(AccountManagModulesEnum.AccountManagUtils, "LOG_TIMESTAMP_FORMAT", "yyyyMMdd HH:mm:ss.SSS z", ConfigType.STRING), LOG_TIME_FORMAT(
            AccountManagModulesEnum.AccountManagUtils, "LOG_TIME_FORMAT", "HH:mm:ss.SSS z", ConfigType.STRING), LOG_DATE_FORMAT(
                    AccountManagModulesEnum.AccountManagUtils, "LOG_DATE_FORMAT", "yyyyMMdd", ConfigType.STRING), LOG_LEVEL_UTILS(AccountManagModulesEnum.AccountManagUtils,
                    "LOG_LEVEL", "trace", ConfigType.LOG_LEVEL),
    // Number validation
    ALLOW_INTERNATOINAL_SMS(AccountManagModulesEnum.AccountManagUtils, "ALLOW_INTERNATOINAL_SMS", true, ConfigType.BOOLEAN),
    MSISDN_CC(AccountManagModulesEnum.AccountManagUtils, "MSISDN_CC", "20", ConfigType.STRING),
    NDC_REGEX(AccountManagModulesEnum.AccountManagUtils, "NDC_REGEX", "(10|11|12|15)", ConfigType.STRING),
    MSISDN_SN_LEN(AccountManagModulesEnum.AccountManagUtils, "MISDN_SN_LEN", 8, ConfigType.INTEGER),
    MSISDN_NDC_LEN(AccountManagModulesEnum.AccountManagUtils, "MISDN_SN_LEN", 2, ConfigType.INTEGER),
    MSISDN_NATIONAL_KEY(AccountManagModulesEnum.AccountManagUtils, "MSISDN_NATIONAL_KEY", "0", ConfigType.STRING),
    SHORT_CODE_SENDER_LENGTH(AccountManagModulesEnum.AccountManagUtils, "SHORT_CODE_SENDER_LENGTH", 7, ConfigType.INTEGER),
    ALPHANUM_SENDER_LENGTH(AccountManagModulesEnum.AccountManagUtils, "ALPHANUM_SENDER_LENGTH", 11, ConfigType.INTEGER),
    FILES_PATH(AccountManagModulesEnum.AccountManagUtils, "FILES_PATH", "files/", ConfigType.STRING),
    // AppUtils
    LOG_DIR_NAME(AccountManagModulesEnum.AccountManagUtils, "LOG_DIR_NAME", "logs/", ConfigType.STRING), LOG_LAYOUT_INFO(AccountManagModulesEnum.AccountManagUtils,
            "LOG_LAYOUT_INFO", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING), LOG_LAYOUT_DEBUG(
                    AccountManagModulesEnum.AccountManagUtils, "LOG_LAYOUT_DEBUG", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n",
                    ConfigType.STRING),
    ACCOUNT_MANAGEMENT_ALARMING_IDENTIFIER(AccountManagModulesEnum.AccountManagUtils, "ACCOUNT_MANAGEMENT_ALARMING_IDENTIFIER", ConfigType.STRING),
    ALARM_PATH(AccountManagModulesEnum.AccountManagUtils, "ALARM_PATH", "alarms/", ConfigType.STRING),
    AppServerBaseDir(AccountManagModulesEnum.AccountManagUtils, "AppServerBaseDir", "web2sms-account-manag/", ConfigType.STRING),
    SECURITY_FILES_PATH(AccountManagModulesEnum.AccountManagUtils, "SECURITY_FILES_PATH", "security/", ConfigType.STRING);
    private static Map<AccountManagModulesEnum, Map<String, Configs>> modulesConfigs;

    public static Collection<Configs> getModuleConfigs(AccountManagModulesEnum module) {
        if (modulesConfigs == null) {
            modulesConfigs = new HashMap<AccountManagModulesEnum, Map<String, Configs>>();
        }
        Map<String, Configs> moduleConfigs = modulesConfigs.get(module);
        if (moduleConfigs == null) {
            moduleConfigs = new HashMap<String, Configs>();
        }
        modulesConfigs.put(module, moduleConfigs);
        return moduleConfigs.values();
    }

    public static Map<String, Configs> getModuleConfigsMap(AccountManagModulesEnum module) {
        if (modulesConfigs == null) {
            modulesConfigs = new HashMap<AccountManagModulesEnum, Map<String, Configs>>();
        }
        Map<String, Configs> moduleConfigs = modulesConfigs.get(module);
        if (moduleConfigs == null) {
            moduleConfigs = new HashMap<String, Configs>();
        }
        modulesConfigs.put(module, moduleConfigs);
        return moduleConfigs;
    }

    public static Set<AccountManagModulesEnum> getModules() {
        return modulesConfigs.keySet();
    }

    public static Configs getConfig(AccountManagModulesEnum module, String property) {
        return getModuleConfigsMap(module).get(property);
    }

    private final String property;
    private final boolean hasDefault;
    private Object value;
    private Object oldValue;
    private ConfigType type;
    private AccountManagModulesEnum module;

    Configs(AccountManagModulesEnum module, String property, ConfigType type) {
        this.property = property;
        this.hasDefault = false;
        this.type = type;
        this.module = module;
        Configs.getModuleConfigsMap(module).put(property, this);
    }

    Configs(AccountManagModulesEnum module, String property, Object value, ConfigType type) throws InvalidConfigValue {
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

    public AccountManagModulesEnum getModule() {
        return module;
    }

    public void setModule(AccountManagModulesEnum module) {
        this.module = module;
    }

}
