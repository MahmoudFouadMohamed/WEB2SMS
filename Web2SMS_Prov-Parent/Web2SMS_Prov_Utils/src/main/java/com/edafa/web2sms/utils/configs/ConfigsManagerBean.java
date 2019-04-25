package com.edafa.web2sms.utils.configs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.ini4j.Ini;

import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.edafa.web2sms.utils.logging.interfaces.LoggingManagerLocal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Session Bean implementation class ConfigsManagerBean
 */
@Singleton
@LocalBean
@Startup
public class ConfigsManagerBean implements ConfigsManagerBeanLocal {
	Logger appLogger;
	java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();
	@Resource(name = "java:app/env/basedir")
	String baseDir;

	@Resource(name = "java:app/env/baseconfigdir")
	String baseConfigDir;

	private Map<ModulesEnum, List<ConfigsListener>> configsListeners;
	private List<ConfigsListener> configsListenersList;

	private Ini iniConfigs;

	// Modules Configs
	Configs smsgwConfigs;

	private Map<ModulesEnum, Boolean> configuratoinLoadedFlags;
	// Temp
	@EJB
	LoggingManagerLocal loggingManager;

	public ConfigsManagerBean() {
		configuratoinLoadedFlags = new HashMap<ModulesEnum, Boolean>();
		configsListeners = new HashMap<ModulesEnum, List<ConfigsListener>>();
		configsListenersList = new ArrayList<ConfigsListener>();
	}

	@PostConstruct
	void init() {
        appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
		AppSettings.BaseDir.setEnvEntryValue(baseDir);

		appLogger.info("Will load modules configurations");
		System.out.println("Will load modules configurations");
		for (Iterator it = Configs.getModules().iterator(); it.hasNext();) {
			ModulesEnum module = (ModulesEnum) it.next();
			appLogger.info("Will load " + module.getName() + " configurations");
			System.out.println("Will load " + module.getName() + " configurations");
			try {
				Map<String, String> configsMap = loadConfigs(module);

				System.out.println("Configs loaded, count=" + configsMap.size());
				if (configsMap.size() > 0) {
					System.out.println("Will update configs");
					updateConfigs(module, configsMap);
                                        setConfiguratoinLoaded(module, true);
				}
			} catch (Exception e) {
				appLogger.error("Cannot load or update configurations from file", e);
				System.err.println("Cannot load or update configurations from file" + e.getMessage());
				configuratoinLoadedFlags.put(module, false);

			}
		}
	}

	@Override
	@Lock(LockType.WRITE)
	public void registerConfigsListener(ModulesEnum module, ConfigsListener listener) {
		List<ConfigsListener> configListeners = configsListeners.get(module);
		if (configListeners == null) {
			configsListeners.put(module, new ArrayList<ConfigsListener>());
		}
		configsListeners.get(module).add(listener);
	}

	@Override
	@Lock(LockType.WRITE)
	public void registerConfigsListener(ConfigsListener listener) {
		configsListenersList.add(listener);
	}

	@Override
	public void refreshAllModuleConfigs() throws Exception {
		for (Iterator it = Configs.getModules().iterator(); it.hasNext();) {
			ModulesEnum module = (ModulesEnum) it.next();
			if (refreshModuleConfigs(module)) {
				notifyConfigsListenres(module);
			}
		}

		loggingManager.refreshLoggers();
	}

	@Override
	public boolean refreshModuleConfigs(ModulesEnum module) throws Exception {
		try {
			Map<String, String> configsMap = loadConfigs(module);
			updateConfigs(module, configsMap);
			notifyConfigsListenres(module);

			loggingManager.refreshLoggers();
		} catch (Exception e) {
			appLogger.error("Cannot handle configuratoin file", e);
			System.err.println("Cannot handle configuratoin file" + e.getMessage());
			configuratoinLoadedFlags.put(module, false);
		}

		notifyConfigsListenres();
		return configuratoinLoadedFlags.get(module);
	}

	private void notifyConfigsListenres(ModulesEnum module) {
		List<ConfigsListener> configListeners = this.configsListeners.get(module);
		for (ConfigsListener configListener : configListeners) {
			try {
				configListener.configurationRefreshed();
			} catch (Exception e) {
				appLogger.error("Failure occured while notifying the configs listner (" + configListener
						+ ") with new configs ", e);
			}
		}
	}

	private void notifyConfigsListenres() {
		for (ConfigsListener configListener : configsListenersList) {
			try {
				configListener.configurationRefreshed();
			} catch (Exception e) {
				appLogger.error("Failure occured while notifying the configs listner (" + configListener
						+ ") with new configs ", e);
			}
		}
	}

	@Override
	public boolean isConfigurationLoaded(ModulesEnum module) {
            
            appLogger.info("Check configuration loading for module: " + module);
            Boolean result = configuratoinLoadedFlags.get(module);
            return result != null ? result : false;
	}

	private Map<String, String> loadConfigs(ModulesEnum module) throws IOException {
		if (iniConfigs == null)
			iniConfigs = new Ini(new File(baseConfigDir + "configs.ini"));
		return iniConfigs.get(module.getName());
	}

	private void updateConfigs(ModulesEnum module, Map<String, String> iniConfigs) {

		boolean validCongigs = true;
		Collection<Configs> moduleConfigs = Configs.getModuleConfigs(module);
		// Validate Configuration
		int c = 0;
		for (Iterator iterator = moduleConfigs.iterator(); iterator.hasNext();) {
			Configs config = (Configs) iterator.next();
			String conf = iniConfigs.get(config.getProperty());
			// Check if the configuration exist in the DB
			if (conf == null) {
				System.out.println("Loaded config for property: " + config.getProperty() + ": " + conf);
				if (!config.isHasDefault()) {
					validCongigs = false;
					appLogger.error("No default value for missing property: " + config.getProperty());
					System.err.println("No default value for missing property: " + config.getProperty());
				}

			} else if (!config.isValidValue(conf)) {
				validCongigs = false;
				appLogger.error("Invalid value for property: " + config.getProperty());
				System.err.println("Invalid value for property: " + config.getProperty());
				break;
			}
		}
		// Update the configuration
		if (validCongigs) {
			for (Iterator iterator = moduleConfigs.iterator(); iterator.hasNext();) {
				Configs config = (Configs) iterator.next();
				String conf = iniConfigs.get(config.getProperty());
				if (conf == null) {
					continue;
				}
				try {
					switch (config.getType()) {
					case BOOLEAN:
						config.setValue(Boolean.parseBoolean(conf));
						break;
					case INTEGER:
						config.setValue(Integer.parseInt(conf));
						break;
					default:
						config.setValue(conf);
					}
					appLogger.info("Applied Configuration: Property(" + config.getProperty() + "), Value("
							+ config.getValue() + ")");
					System.out.println("Applied Configuration: Property(" + config.getProperty() + "), Value("
							+ config.getValue() + ")");
				} catch (Exception e) {
					appLogger.error("Invalid value for property: " + config.getProperty());
					System.err.println("Invalid value for property: " + config.getProperty());
					appLogger.error("Configuration will not be updated");
					System.err.println("Configuration will not be updated");
					setConfiguratoinLoaded(module, false);
					break;
				}
			}
		} else {
			appLogger.error("Configuration will not be updated for " + module.getName() + " module");
			System.err.println("Configuration will not be updated for " + module.getName() + " module");
			setConfiguratoinLoaded(module, false);
		}

	}

	public void setConfiguratoinLoaded(ModulesEnum module, boolean configuratoinLoaded) {
		this.configuratoinLoadedFlags.put(module, configuratoinLoaded);
	}
}
