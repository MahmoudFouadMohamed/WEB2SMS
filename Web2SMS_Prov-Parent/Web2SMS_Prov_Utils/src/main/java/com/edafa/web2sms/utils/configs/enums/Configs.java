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
	LOG_DIR_NAME(ModulesEnum.ProvUtils, "LOG_DIR_NAME", "logs/", ConfigType.STRING), 
	LOG_LAYOUT_INFO(ModulesEnum.ProvUtils,"LOG_LAYOUT_INFO", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n", ConfigType.STRING), 
	LOG_LAYOUT_DEBUG(ModulesEnum.ProvUtils, "LOG_LAYOUT_DEBUG", "%d{yyyyMMdd HH:mm:ss.SSS z} | %p | %.30t | %m%n",
			ConfigType.STRING),
	WS_CLIENT_CONNECT_TIMEOUT(ModulesEnum.ProvUtils, "WS_CLIENT_CONNECT_TIMEOUT", 1000,ConfigType.INTEGER),
	WS_CLIENT_REQUEST_TIMEOUT(ModulesEnum.ProvUtils, "WS_CLIENT_REQUEST_TIMEOUT", 5000,ConfigType.INTEGER),

	LOG_LEVEL_UTILES(ModulesEnum.ProvUtils, "LOG_LEVEL", "debug", ConfigType.LOG_LEVEL), 
	LOG_LEVEL_CLOUD_PROV(ModulesEnum.CloudProvisioning, "LOG_LEVEL", "debug", ConfigType.LOG_LEVEL), 
	CLOUD_PROV_SERVICE_URI(ModulesEnum.CloudProvisioning, "CLOUD_PROV_SERVICE_URI", ConfigType.STRING), 
//	LOG_LEVEL_SMS_API(ModulesEnum.SMSAPI, "LOG_LEVEL", "debug", ConfigType.LOG_LEVEL), 
//	SMS_API_SERVICE_URI(ModulesEnum.SMSAPI, "SMS_API_SERVICE_URI", ConfigType.STRING),
	ERRORS_RAISING(ModulesEnum.ProvUtils, "ERRORS_RAISING", false, ConfigType.BOOLEAN),
        ALARMING_IDENTIFIER(ModulesEnum.ProvUtils, "ALARMING_IDENTIFIER", ConfigType.STRING),
        ERRORS_RAISING_SERVICE_URI(ModulesEnum.ProvUtils, "ERRORS_RAISING_SERVICE_URI", ConfigType.STRING),
        ERRORS_CONFIG_SERVICE_URI(ModulesEnum.ProvUtils, "ERRORS_CONFIG_SERVICE_URI", ConfigType.STRING)

	;

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