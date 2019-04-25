package com.edafa.web2sms.acc_manag.utils.configs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.utils.configs.enums.AppSettings;
import com.edafa.web2sms.acc_manag.utils.configs.enums.Configs;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.InvalidConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.acc_manag.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.edafa.web2sms.acc_manag.utils.configs.interfaces.ConfigsSource;
import com.edafa.web2sms.acc_manag.utils.configs.model.Config;
import com.edafa.web2sms.acc_manag.utils.configs.model.ModuleConfigs;

/**
 * Session Bean implementation class AccManagConfigsManagerBean
 */
@Singleton
@LocalBean
@Startup
public class AccManagConfigsManagerBean implements ConfigsManagerBeanLocal {
	Logger appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
	java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

	@Resource(name = "java:app/env/basedir")
	String baseDir;

	@EJB(lookup = "java:module/DefaultConfigsSource")
	ConfigsSource defaultConfigsSource;

	private Map<AccountManagModulesEnum, List<ConfigsListener>> configsListenersMap;
	private List<ConfigsListener> configsListenersList;

	public int maxSendingRate = 120;

	private Map<AccountManagModulesEnum, Boolean> configuratoinLoadedFlags;

	
//	private RateController rateController;
	// @EJB
	// ConfigDaoLocal configDao;

	public AccManagConfigsManagerBean() {
		configuratoinLoadedFlags = new HashMap<AccountManagModulesEnum, Boolean>();
		configsListenersMap = new HashMap<AccountManagModulesEnum, List<ConfigsListener>>();
		configsListenersList = new ArrayList<ConfigsListener>();
		logger.setLevel(Level.ALL);
	}

	@PostConstruct
	void init() {
		AppSettings.BaseDir.setEnvEntryValue(baseDir);

		appLogger.info("Will load modules configurations");
		logger.info("Will load modules configurations");
		for (Iterator<AccountManagModulesEnum> it = Configs.getModules().iterator(); it.hasNext();) {
			AccountManagModulesEnum module = it.next();
			appLogger.info("Will load " + module.getName() + " configurations");

			logger.info("Will load " + module.getName() + " configurations");
			try {
				ModuleConfigs moduleConfigs = readModuleConfigs(module);
				applyConfigs(moduleConfigs);
				setConfiguratoinLoaded(module, true);
			} catch (FailedToReadConfigsException e) {
				appLogger.error("Cannot retrieve configuration", e);
				logger.log(Level.SEVERE, "Cannot retrieve configuration: ", e);
				setConfiguratoinLoaded(module, false);
			} catch (InvalidConfigsException e) {
				appLogger.error("Invalid configs: " + e.getMessage());
				logger.log(Level.SEVERE, "Invalid configs: " + e.getMessage());
				setConfiguratoinLoaded(module, false);
			}
		}
	}

	@Override
	@Lock(LockType.WRITE)
	public void registerConfigsListener(AccountManagModulesEnum module, ConfigsListener listener) {
		appLogger.info("Registering new ConfigsListener " + listener + " for module " + module.name());
		logger.info("Registering new ConfigsListener " + listener + " for module " + module.name());
		List<ConfigsListener> configListeners = configsListenersMap.get(module);
		if (configListeners == null) {
			configsListenersMap.put(module, new ArrayList<ConfigsListener>());
		}
		configsListenersMap.get(module).add(listener);
	}

	@Override
	@Lock(LockType.WRITE)
	public void registerConfigsListener(ConfigsListener listener) {
		appLogger.info("Registering new ConfigsListener " + listener + " for all modules ");
		logger.info("Registering new ConfigsListener " + listener + " for all modules ");
		configsListenersList.add(listener);
	}

	@Override
	public boolean isConfigurationLoaded(AccountManagModulesEnum module) {
		Boolean result1 = configuratoinLoadedFlags.get(AccountManagModulesEnum.AccountManagUtils);
		Boolean result2 = configuratoinLoadedFlags.get(module);
		return result1 != null && result2 != null ? (result1 && result2) : false;
	}

	private void setConfiguratoinLoaded(AccountManagModulesEnum module, boolean configuratoinLoaded) {
		this.configuratoinLoadedFlags.put(module, configuratoinLoaded);
	}

	// @Override
	// public Map<String, Config> readConfigsMap(AccountManagModulesEnum module) throws
	// FailedToReadConfigsException {
	// try {
	// List<Configuration> configsList =
	// configDao.findByModuleName(module.getName());
	// return conversionBean.getConfigsMap(configsList);
	// } catch (Exception e) {
	// appLogger.error("Failed to read configuration ", e);
	// logger.info("Failed to read configuration " , e);
	// throw new FailedToReadConfigsException(e);
	// }
	// }

	@Override
	public ModuleConfigs readModuleConfigs(String module) throws FailedToReadConfigsException {
		AccountManagModulesEnum moduleEnum;
		try {
			moduleEnum = AccountManagModulesEnum.valueOf(module);
		} catch (Exception e) {
			throw new FailedToReadConfigsException("No such module");
		}
		return readModuleConfigs(moduleEnum);
	}

	@Override
	public ModuleConfigs readModuleConfigs(AccountManagModulesEnum module) throws FailedToReadConfigsException {
		appLogger.info("Reading configs for module " + module);
		if (module == null) {
			throw new FailedToReadConfigsException("No such application module");
		}

		try {
			module = AccountManagModulesEnum.valueOf(module.name());
		} catch (Exception e) {
			throw new FailedToReadConfigsException("No such application module");
		}
		appLogger.info("Reading configuratoin for module [" + module.getName() + "]");
		ModuleConfigs moduleConfigs = new ModuleConfigs();
		moduleConfigs.setModule(module.name());
		moduleConfigs.setConfigsApplied(module.isConfigsApplied());
		try {
			List<Config> configs = defaultConfigsSource.readConfigs(module);
			moduleConfigs.setConfigList(configs);
		} catch (Exception e) {
			appLogger.error("Failed to read configuration for moudle: " + module.name(), e);
			throw new FailedToReadConfigsException(e);
		}
		return moduleConfigs;
	}

	@Override
	public List<ModuleConfigs> readConfigs() throws FailedToReadConfigsException {
		List<ModuleConfigs> modulesConfigs = new ArrayList();
		try {
			appLogger.info("Reading configuratoin for all modules");
			for (AccountManagModulesEnum module : AccountManagModulesEnum.values()) {
				modulesConfigs.add(readModuleConfigs(module));
			}
		} catch (Exception e) {
			appLogger.error("Failed to read configuration ", e);
			throw new FailedToReadConfigsException(e);
		}

		return modulesConfigs;
	}

	public void validateModuleConfigs(ModuleConfigs moduleConfigs) throws InvalidConfigsException {
		// if (!moduleConfigs.isValid()) {
		// appLogger.error("Invalid configs for module: " + moduleConfigs);
		// throw new InvalidConfigsException("Invalid request");
		// }
		AccountManagModulesEnum module = AccountManagModulesEnum.valueOf(moduleConfigs.getModule());
		List<Config> configs = moduleConfigs.getConfigList();
		List<Config> invalidConfigs = new ArrayList<Config>();
		Collection<Configs> moduleEnumConfigs = Configs.getModuleConfigs(module);
		Map<String, Config> configsMap = new HashMap();

		for (Config conf : configs) {
			configsMap.put(conf.getKey(), conf);
		}

		// Validate Configuration
		for (Iterator<Configs> iterator = moduleEnumConfigs.iterator(); iterator.hasNext();) {
			Configs configEnum = iterator.next();
			Config conf = configsMap.get(configEnum.getProperty());
			// Check if the configuration exist in the DB
			if (conf == null) {
				if (!configEnum.isHasDefault()) {
					appLogger.error("No default value for property: " + configEnum.getProperty());
					logger.log(Level.SEVERE, "No default value for property: " + configEnum.getProperty());
					invalidConfigs.add(conf);
				}

			} else if (!configEnum.isValidValue(conf.getValue())) {
				invalidConfigs.add(conf);
				appLogger.error("Invalid value for property: " + configEnum.getProperty());
				logger.log(Level.SEVERE, "Invalid value for property: " + configEnum.getProperty());
				break;
			}
		}

		if (!invalidConfigs.isEmpty()) {
			throw new InvalidConfigsException(module.name(), invalidConfigs);
		}

	}

	@Override
	public void saveConfigs(ModuleConfigs moduleConfigs) throws FailedToSaveConfigsException, InvalidConfigsException {
		if (!moduleConfigs.isValid()) {
			appLogger.error("Invalid request to save configs: " + moduleConfigs);
			throw new FailedToSaveConfigsException("Invalid request");
		}
		appLogger.info("Saving configs for module " + moduleConfigs.getModule());
		appLogger.info("Saving configs for module " + moduleConfigs.getModule());
		appLogger.info("Validating the configs for");

		validateModuleConfigs(moduleConfigs);

		AccountManagModulesEnum module = AccountManagModulesEnum.valueOf(moduleConfigs.getModule());
		List<Config> configs = moduleConfigs.getConfigList();
		appLogger.debug("Set ConfigsApplied=" + false + " for module " + module.name());
		module.setConfigsApplied(false);
		try {
			appLogger.info("Persisting new configs");
			logger.info("Persisting new configs");
			defaultConfigsSource.saveConfigs(module, configs);
			appLogger.info("Configs saved successfully");
			logger.info("Configs saved successfully");
		} catch (Exception e) {
			appLogger.error("Failed to save configuration", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	// @Override
	public void applyConfigs(ModuleConfigs moduleConfigs) throws InvalidConfigsException {
		AccountManagModulesEnum module = AccountManagModulesEnum.valueOf(moduleConfigs.getModule());

		List<Config> configs = moduleConfigs.getConfigList();

		validateModuleConfigs(moduleConfigs);

		// Update the configuration
		for (Iterator<Config> iterator = configs.iterator(); iterator.hasNext();) {
			Config conf = iterator.next();
			Configs config = Configs.getConfig(module, conf.getKey());

			if (config == null) {
				continue;
			}
			try {
				switch (config.getType()) {
				case BOOLEAN:
					config.setValue(Boolean.parseBoolean(conf.getValue()));
					break;
				case INTEGER:
					config.setValue(Integer.parseInt(conf.getValue()));
					break;
				default:
					config.setValue(conf.getValue());
				}
				appLogger.info("Applied Configuration: Property(" + config.getProperty() + "), Value("
						+ config.getValue() + ")");
				logger.info("Applied Configuration: Property(" + config.getProperty() + "), Value(" + config.getValue()
						+ ")");
			} catch (Exception e) {
				appLogger.error("Invalid value for property: " + config.getProperty()
						+ ", Configuration will not be updated");
				logger.log(Level.SEVERE, "Invalid value for property: " + config.getProperty()
						+ ", Configuration will not be updated");
				setConfiguratoinLoaded(module, false);
			}
		}

	}

	@Override
	public void refreshAllModuleConfigs() throws FailedToReadConfigsException, InvalidConfigsException {
		logger.info("Refresh all modules configs");
		appLogger.info("Refresh all modules configs");
		for (Iterator<AccountManagModulesEnum> it = Configs.getModules().iterator(); it.hasNext();) {
			AccountManagModulesEnum module = it.next();

			refreshModuleConfigs(module);
		}
		notifyConfigsListenres();
	}

	@Override
	public void refreshModuleConfigs(String module) throws FailedToReadConfigsException, InvalidConfigsException {
		AccountManagModulesEnum moduleEnum;
		try {
			moduleEnum = AccountManagModulesEnum.valueOf(module);
		} catch (Exception e) {
			throw new FailedToReadConfigsException("No such module");
		}

		refreshModuleConfigs(moduleEnum);
	}

	@Override
	public void refreshModuleConfigs(AccountManagModulesEnum module) throws FailedToReadConfigsException, InvalidConfigsException {
		try {
			appLogger.info("Reading configuratoin for module [" + module.name() + "]");
			ModuleConfigs moduleConfigs = readModuleConfigs(module);
			applyConfigs(moduleConfigs);
			module.setConfigsApplied(true);
			notifyConfigsListenres(module);
		} catch (FailedToReadConfigsException e) {
			appLogger.error("Cannot read configuration", e);
			logger.log(Level.SEVERE, "Cannot read configuration ", e);
			setConfiguratoinLoaded(module, false);
			throw e;
		}
		notifyConfigsListenres();
	}

	@Override
	public void refreshModuleConfigs(ModuleConfigs moduleConfigs) throws InvalidConfigsException {
		AccountManagModulesEnum moduleEnum = AccountManagModulesEnum.valueOf(moduleConfigs.getModule());
		applyConfigs(moduleConfigs);
		notifyConfigsListenres(moduleEnum);
		notifyConfigsListenres();
	}

	private void notifyConfigsListenres(AccountManagModulesEnum module) {
		List<ConfigsListener> configListeners = this.configsListenersMap.get(module);
		if (configListeners != null) {
			for (ConfigsListener configListener : configListeners) {
				try {
					configListener.configurationRefreshed(module);
				} catch (Exception e) {
					appLogger.warn("Failure occured while notifying the configs listner (" + configListener
							+ ") with new configs in module (" + module.getName() + ")", e);
				}
			}
		}
	}

	private void notifyConfigsListenres() {
		for (ConfigsListener configListener : configsListenersList) {
			try {
				configListener.configurationRefreshed();
			} catch (Exception e) {
				appLogger.warn("Failure occured while notifying the configs listner (" + configListener
						+ ") with new configs ", e);
			}
		}
	}

}