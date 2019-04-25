package com.edafa.web2sms.utils.configs;

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
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.utils.rate.controller.Limiter;
import com.edafa.utils.rate.controller.exceptions.DuplicateLimiterException;
import com.edafa.utils.rate.controller.exceptions.InvalidParameterException;
import com.edafa.utils.rate.controller.exceptions.NoSuchLimiterException;
import com.edafa.utils.rate.controller.interfaces.RateController;
import com.edafa.web2sms.dalayer.dao.interfaces.SendingRateLimiterDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.GenericLimiters;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigsException;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsSource;
import com.edafa.web2sms.utils.configs.model.Config;
import com.edafa.web2sms.utils.configs.model.ModuleConfigs;

/**
 * Session Bean implementation class ConfigsManagerBean
 */
@Singleton(name = "Web2SMS-Core_Utils/ConfigsManagerBean")
@Startup
public class ConfigsManagerBean implements ConfigsManagerBeanLocal {
	Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS
			.name());
	java.util.logging.Logger logger = java.util.logging.Logger
			.getAnonymousLogger();

	@Resource(name = "java:app/env/basedir")
	String baseDir;

	@EJB
	ConfigsSource defaultConfigsSource;

	private Map<ModulesEnum, List<ConfigsListener>> configsListenersMap;
	private List<ConfigsListener> configsListenersList;

	public int maxSendingRate = 120;

	private Map<ModulesEnum, Boolean> configuratoinLoadedFlags;

	@EJB
	private SendingRateLimiterDaoLocal sendingRateLimiterDao;

	@EJB(beanName = "SMSGW_Utils/ConfigsManagerBean")
	com.edafa.smsgw.utils.configs.interfaces.ConfigsManagerBeanLocal smsgwConfigsManagerBean;

	private RateController rateController;

	// @EJB
	// ConfigDaoLocal configDao;

	public ConfigsManagerBean() {
		configuratoinLoadedFlags = new HashMap<ModulesEnum, Boolean>();
		configsListenersMap = new HashMap<ModulesEnum, List<ConfigsListener>>();
		configsListenersList = new ArrayList<ConfigsListener>();
		logger.setLevel(Level.ALL);
	}

	@PostConstruct
	void init() {
		AppSettings.BaseDir.setEnvEntryValue(baseDir);

		appLogger.info("Will load modules configurations");
		logger.info("Will load modules configurations");
		for (Iterator<ModulesEnum> it = Configs.getModules().iterator(); it
				.hasNext();) {
			ModulesEnum module = it.next();
			appLogger.info("Will load " + module.getName() + " configurations");

			logger.info("Will load " + module.getName() + " configurations");
			try {
				ModuleConfigs moduleConfigs = readModuleConfigs(module);
				applyConfigs(moduleConfigs);
				setConfiguratoinLoaded(module, true);
			} catch (FailedToReadConfigsException e) {
				appLogger.error("Cannot retrieve configuration", e);
				logger.log(Level.SEVERE, "Cannot retrieve configuration: ", e);
				configuratoinLoadedFlags.put(module, false);
			} catch (InvalidConfigsException e) {
				appLogger.error("Invalid configs: " + e.getMessage());
				logger.log(Level.SEVERE, "Invalid configs: " + e.getMessage());
				setConfiguratoinLoaded(module, false);
			}
		}
	}

	@Override
	@Lock(LockType.WRITE)
	public void registerConfigsListener(ModulesEnum module,
			ConfigsListener listener) {
		appLogger.info("Registering new ConfigsListener " + listener
				+ " for module " + module.name());
		logger.info("Registering new ConfigsListener " + listener
				+ " for module " + module.name());
		List<ConfigsListener> configListeners = configsListenersMap.get(module);
		if (configListeners == null) {
			configsListenersMap.put(module, new ArrayList<ConfigsListener>());
		}
		configsListenersMap.get(module).add(listener);
	}

	@Override
	@Lock(LockType.WRITE)
	public void registerConfigsListener(ConfigsListener listener) {
		appLogger.info("Registering new ConfigsListener " + listener
				+ " for all modules ");
		logger.info("Registering new ConfigsListener " + listener
				+ " for all modules ");
		configsListenersList.add(listener);
	}

	@Override
	public boolean isConfigurationLoaded(ModulesEnum module) {
		Boolean result1 = configuratoinLoadedFlags.get(ModulesEnum.CoreUtils);
		Boolean result2 = configuratoinLoadedFlags.get(module);
		return result1 != null && result2 != null ? (result1 && result2)
				: false;
	}

	private void setConfiguratoinLoaded(ModulesEnum module,
			boolean configuratoinLoaded) {
		this.configuratoinLoadedFlags.put(module, configuratoinLoaded);
	}

	@Override
	public ModuleConfigs readModuleConfigs(String module)
			throws FailedToReadConfigsException {
		ModulesEnum moduleEnum;
		try {
			moduleEnum = ModulesEnum.valueOf(module);
		} catch (Exception e) {
			throw new FailedToReadConfigsException("No such module");
		}
		return readModuleConfigs(moduleEnum);
	}

	@Override
	public ModuleConfigs readModuleConfigs(ModulesEnum module)
			throws FailedToReadConfigsException {
		appLogger.info("Reading configuratoin for module [" + module + "]");
		if (module == null) {
			throw new FailedToReadConfigsException("No such application module");
		}

		try {
			module = ModulesEnum.valueOf(module.name());
		} catch (Exception e) {
			throw new FailedToReadConfigsException("No such application module");
		}

		ModuleConfigs moduleConfigs = new ModuleConfigs();
		moduleConfigs.setModule(module.name());
		moduleConfigs.setConfigsApplied(module.isConfigsApplied());
		try {
			List<Config> configs = defaultConfigsSource.readConfigs(module);
			moduleConfigs.setConfigList(configs);
		} catch (Exception e) {
			appLogger.error("Failed to read configuration for moudle: "
					+ module.name(), e);
			throw new FailedToReadConfigsException(e);
		}
		return moduleConfigs;
	}

	@Override
	public List<ModuleConfigs> readConfigs()
			throws FailedToReadConfigsException {
		List<ModuleConfigs> modulesConfigs = new ArrayList<>();
		try {
			appLogger.info("Reading configuratoin for all modules");
			for (ModulesEnum module : ModulesEnum.values()) {
				modulesConfigs.add(readModuleConfigs(module));
			}
		} catch (Exception e) {
			appLogger.error("Failed to read configuration ", e);
			throw new FailedToReadConfigsException(e);
		}

		return modulesConfigs;
	}

	public void validateModuleConfigs(ModuleConfigs moduleConfigs)
			throws InvalidConfigsException {
		// if (!moduleConfigs.isValid()) {
		// appLogger.error("Invalid configs for module: " + moduleConfigs);
		// throw new InvalidConfigsException("Invalid request");
		// }
		ModulesEnum module = ModulesEnum.valueOf(moduleConfigs.getModule());
		List<Config> configs = moduleConfigs.getConfigList();
		List<Config> invalidConfigs = new ArrayList<Config>();
		Collection<Configs> moduleEnumConfigs = Configs
				.getModuleConfigs(module);
		Map<String, Config> configsMap = new HashMap<>();

		for (Config conf : configs) {
			configsMap.put(conf.getKey(), conf);
		}

		// Validate Configuration
		for (Iterator<Configs> iterator = moduleEnumConfigs.iterator(); iterator
				.hasNext();) {
			Configs configEnum = iterator.next();
			Config conf = configsMap.get(configEnum.getProperty());
			// Check if the configuration exist in the DB
			if (conf == null) {
				if (!configEnum.isHasDefault()) {
					appLogger.error("No default value for property: "
							+ configEnum.getProperty());
					logger.log(Level.SEVERE, "No default value for property: "
							+ configEnum.getProperty());
					invalidConfigs.add(conf);
				}

			} else if (!configEnum.isValidValue(conf.getValue())) {
				invalidConfigs.add(conf);
				appLogger.error("Invalid value for property: "
						+ configEnum.getProperty());
				logger.log(Level.SEVERE, "Invalid value for property: "
						+ configEnum.getProperty());
				break;
			}
		}

		if (!invalidConfigs.isEmpty()) {
			throw new InvalidConfigsException(module.name(), invalidConfigs);
		}

	}

	@Override
	public void saveConfigs(ModuleConfigs moduleConfigs)
			throws FailedToSaveConfigsException, InvalidConfigsException {
		if (!moduleConfigs.isValid()) {
			appLogger
					.error("Invalid request to save configs: " + moduleConfigs);
			throw new FailedToSaveConfigsException("Invalid request");
		}
		appLogger
				.info("Saving configs for module " + moduleConfigs.getModule());
		appLogger
				.info("Saving configs for module " + moduleConfigs.getModule());
		appLogger.info("Validating the configs for");

		validateModuleConfigs(moduleConfigs);

		ModulesEnum module = ModulesEnum.valueOf(moduleConfigs.getModule());
		List<Config> configs = moduleConfigs.getConfigList();
		appLogger.debug("Set ConfigsApplied=" + false + " for module "
				+ module.name());
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
	public void applyConfigs(ModuleConfigs moduleConfigs)
			throws InvalidConfigsException {
		ModulesEnum module = ModulesEnum.valueOf(moduleConfigs.getModule());

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
				appLogger.info("Applied Configuration: Property("
						+ config.getProperty() + "), Value("
						+ config.getValue() + ")");
				logger.info("Applied Configuration: Property("
						+ config.getProperty() + "), Value("
						+ config.getValue() + ")");
			} catch (Exception e) {
				appLogger.error("Invalid value for property: "
						+ config.getProperty()
						+ ", Configuration will not be updated");
				logger.log(Level.SEVERE, "Invalid value for property: "
						+ config.getProperty()
						+ ", Configuration will not be updated");
				setConfiguratoinLoaded(module, false);
			}
		}

	}

	@Override
	public void refreshAllModuleConfigs() throws FailedToReadConfigsException,
			InvalidConfigsException {
		logger.info("Refresh all modules configs");
		appLogger.info("Refresh all modules configs");
		for (Iterator<ModulesEnum> it = Configs.getModules().iterator(); it
				.hasNext();) {
			ModulesEnum module = it.next();

			refreshModuleConfigs(module);
		}
		notifyConfigsListenres();
	}

	@Override
	public void refreshModuleConfigs(String module)
			throws FailedToReadConfigsException, InvalidConfigsException {
		ModulesEnum moduleEnum;
		try {
			moduleEnum = ModulesEnum.valueOf(module);
		} catch (Exception e) {
			throw new FailedToReadConfigsException("No such module");
		}

		refreshModuleConfigs(moduleEnum);
	}

	@Override
	public void refreshModuleConfigs(ModulesEnum module)
			throws FailedToReadConfigsException, InvalidConfigsException {
		try {
			appLogger.info("Reading configuratoin for module [" + module.name()
					+ "]");
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
	public void refreshModuleConfigs(ModuleConfigs moduleConfigs)
			throws InvalidConfigsException {
		ModulesEnum moduleEnum = ModulesEnum.valueOf(moduleConfigs.getModule());
		applyConfigs(moduleConfigs);
		notifyConfigsListenres(moduleEnum);
		notifyConfigsListenres();
	}

	private void notifyConfigsListenres(ModulesEnum module) {
		List<ConfigsListener> configListeners = this.configsListenersMap
				.get(module);
		if (configListeners != null) {
			for (ConfigsListener configListener : configListeners) {
				try {
					configListener.configurationRefreshed(module);
				} catch (Exception e) {
					appLogger.warn(
							"Failure occured while notifying the configs listner ("
									+ configListener
									+ ") with new configs in module ("
									+ module.getName() + ")", e);
				}
			}
		}
	}

	private void notifyConfigsListenres() {
		for (ConfigsListener configListener : configsListenersList) {
			try {
				configListener.configurationRefreshed();
			} catch (Exception e) {
				appLogger.warn(
						"Failure occured while notifying the configs listner ("
								+ configListener + ") with new configs ", e);
			}
		}
	}
        
    @Override
    public void initSendingRateController(RateController rateController) throws DuplicateLimiterException, InvalidParameterException, NoSuchLimiterException {
        if (appLogger.isDebugEnabled()) {
            appLogger.debug("Configure sending rate controller");
        }
        this.rateController = rateController;

        try {
            appLogger.info("Create system limiter, name: " + GenericLimiters.SystemLimiter.name() + ", maxPermits=" + smsgwConfigsManagerBean.getSystemSendingRate());
            rateController.addLimiter(GenericLimiters.SystemLimiter.name(), smsgwConfigsManagerBean.getSystemSendingRate());
            appLogger.info("Create campaigns system limiter, name: " + GenericLimiters.CampSystemLimiter.name() + ", maxPermits=" + (int) Configs.SMS_CAMPAIGN_SENDING_RATE.getValue());
            rateController.addLimiter(GenericLimiters.CampSystemLimiter.name(), (int) Configs.SMS_CAMPAIGN_SENDING_RATE.getValue());
            appLogger.info("Create campaigns default limiter, name: " + GenericLimiters.CampDefaultLimiter.name() + ", maxPermits=" + (int) Configs.CAMPAIGN_DEFAULT_LIMITER.getValue());
            rateController.addLimiter(GenericLimiters.CampDefaultLimiter.name(), (int) Configs.CAMPAIGN_DEFAULT_LIMITER.getValue());
            appLogger.info("Create smsapi system limiter, name: " + GenericLimiters.SmsapiSystemLimiter.name() + ", maxPermits=" + (int) Configs.SMS_SMSAPI_SENDING_RATE.getValue());
            rateController.addLimiter(GenericLimiters.SmsapiSystemLimiter.name(), (int) Configs.SMS_SMSAPI_SENDING_RATE.getValue());
            appLogger.info("Create smsapi default limiter, name: " + GenericLimiters.SmsapiDefaultLimiter.name() + ", maxPermits=" + (int) Configs.SMSAPI_DEFAULT_LIMITER.getValue());
            rateController.addLimiter(GenericLimiters.SmsapiDefaultLimiter.name(), (int) Configs.SMSAPI_DEFAULT_LIMITER.getValue());
            rateController.setSystemLimiter(GenericLimiters.SystemLimiter.name());
        } catch (DuplicateLimiterException e) {
            appLogger.warn("Limiter already exist " + e.getMessage());
            throw e;
        } catch (InvalidParameterException e) {
            appLogger.warn("Failed to create Limiter , invalied parameter " + e.getMessage());
            throw e;
        } catch (NoSuchLimiterException e) {
            appLogger.warn("Failed to set system Limiter " + e.getMessage());
            throw e;
        }

        List<SendingRateLimiter> sendingRateLimiters;
        try {
            sendingRateLimiters = sendingRateLimiterDao.findAll();
        } catch (DBException ex) {
            throw new Error("Failed to load Sending Rate Limiters configs", ex);
        }
        if (sendingRateLimiters != null && !sendingRateLimiters.isEmpty()) {
            if (appLogger.isDebugEnabled()) {
                appLogger.debug("limiters list : " + sendingRateLimiters);
            }

            for (SendingRateLimiter sendingRateLimiter : sendingRateLimiters) {
                if (sendingRateLimiter.isCampEnabled() || sendingRateLimiter.isSmsapiEnabled()) {
                    appLogger.info("Create limiter : " + sendingRateLimiter);
                    try {
                        rateController.addLimiter(sendingRateLimiter.getLimiterId(), sendingRateLimiter.getMaxPermits());
                    } catch (DuplicateLimiterException e) {
                        appLogger.warn("Limiter already exist " + e.getMessage());
                    } catch (InvalidParameterException e) {
                        appLogger.warn("Failed to create Limiter , invalied parameter " + e.getMessage());
                    }
                }
            }
        }
    }

	@Override
	public void registeredLimiterController(RateController rateController) {
		this.rateController = rateController;
	}

	/**
	 * this method reads all limiters from DB
	 * 
	 * @return list of limiters from DB
	 * @throws FailedToReadConfigsException
	 */
	@Override
	public List<SendingRateLimiter> readLimiters()
			throws FailedToReadConfigsException {
		List<SendingRateLimiter> limiters = new ArrayList<>();
		try {
			appLogger.info("Reading all sending rate limiters from DB");
			limiters = sendingRateLimiterDao.findAll();
//			for (SendingRateLimiter sendingRateLimiter : limiters) {
//				System.out.println("sendingRateLimiter object: "
//						+ sendingRateLimiter.toString());
//			}
			appLogger.info("returned with limiters list size: " + limiters.size());
			
			for (SendingRateLimiter sendingRateLimiter : limiters) {
				appLogger.trace("limiter " + sendingRateLimiter);
			}

		} catch (Exception e) {
			appLogger.error("Failed to read SendingRateLimiter ", e);
			throw new FailedToReadConfigsException(e);
		}

		return limiters;
	}

	/**
	 * this method update list of limiters into DB
	 * 
	 * @throws FailedToSaveConfigsException
	 */
	@Override
	public void saveLimiters(List<SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException {

		try {
			appLogger.info("update list of limiters with size" + limiters.size()+" into DB");
			sendingRateLimiterDao.edit(limiters);
		} catch (Exception e) {
			appLogger.error("FailedToSaveConfigsException", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	/**
	 * this method update limit into DB
	 * 
	 * @param limiter
	 * @throws FailedToSaveConfigsException
	 */
	@Override
	public void saveLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		try {
			appLogger.info("update limiter ["+ limiter+ " ] into DB");
			sendingRateLimiterDao.edit(limiter);
			appLogger.info("limiter is updated successfully");

		} catch (Exception e) {
			appLogger.error("FailedToSaveConfigsException", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	/**
	 * this method gets list of cached running limiters from rate controller
	 * 
	 * @return
	 * @throws FailedToReadConfigsException
	 */
	@Override
	public List<SendingRateLimiter> getRunningLimiters()
			throws FailedToReadConfigsException {
		List<SendingRateLimiter> runningSendingLimiters = new ArrayList<SendingRateLimiter>();
		try {
			List<Limiter> runningLimiters = new ArrayList<Limiter>();
			appLogger.info("Get running limiters from rate controller");
			runningLimiters = rateController.getLimiters();
//			for (Limiter limiter : runningLimiters) {
//				appLogger.trace("limiter: " + limiter);
//			}
			appLogger.debug("getting running size: [" + runningLimiters.size()
					+ "] convert limiters into SendingRateLimiter");
			runningSendingLimiters = limiterConversion(runningLimiters);
			for (SendingRateLimiter limiter : runningSendingLimiters) {
				appLogger.trace("converted limiter: " + limiter);
			}
		} catch (Exception e) {
			appLogger.error("Failed to get runnig Sending Rate Limiters ", e);
			throw new FailedToReadConfigsException(e);
		}
		return runningSendingLimiters;
	}

	private List<SendingRateLimiter> limiterConversion(List<Limiter> limiters) {
		List<SendingRateLimiter> sendingLimiters = new ArrayList<>();
		for (Limiter limiter : limiters) {
			SendingRateLimiter temp = new SendingRateLimiter(
					limiter.getLimiterId(), limiter.getMaxPermits().get());
			sendingLimiters.add(temp);
		}
		return sendingLimiters;
	}

	/**
	 * this methods apply all the updates in db into limiter controller too.
	 * 
	 * @throws FailedToSaveConfigsException
	 */
	@Override
	public void refreshLimiters(List<SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException {

		appLogger.info("update list of Limiters in rate controller");

		for (SendingRateLimiter sendingRateLimiter : limiters) {
			try {
				appLogger.trace("trying to update ["+sendingRateLimiter+ "] into rate controller.");
				rateController.updateLimiter(sendingRateLimiter.getLimiterId(),
						sendingRateLimiter.getMaxPermits());
			} catch (NoSuchLimiterException e) {
				appLogger.error("NoSuchLimiterException ", e);
				throw new FailedToSaveConfigsException(e);
			} catch (InvalidParameterException e) {
				appLogger.error("InvalidParameterException ", e);
				throw new FailedToSaveConfigsException(e);
			} catch (Exception e) {
				appLogger.error("Exception ", e);
				throw new FailedToSaveConfigsException(e);
			}
		}
	}

	/**
	 * this methods apply the updates in db into limiter controller too.
	 * 
	 * @throws FailedToSaveConfigsException
	 */
	@Override
	public void refreshLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		appLogger.info("refresh Limiter (" + limiter.getLimiterId()
				+ ") with maxPerims: " + limiter.getMaxPermits());

		try {
			appLogger.info("update Limiter [" + limiter + "] in rate controller");

			rateController.updateLimiter(limiter.getLimiterId(),
					limiter.getMaxPermits());
			appLogger.info("Limiter Limiter [" + limiter + "] is updated successfully");

		} catch (NoSuchLimiterException e) {
			appLogger.error("NoSuchLimiterException ", e);
			throw new FailedToSaveConfigsException(e);
		} catch (InvalidParameterException e) {
			appLogger.error("InvalidParameterException ", e);
			throw new FailedToSaveConfigsException(e);
		} catch (Exception e) {
			appLogger.error("Failed to update Sending Rate Limiter with id ["
					+ limiter.getLimiterId() + "].", e);
			throw new FailedToSaveConfigsException(e);

		}

	}

	@Override
	public void refreshLimiter(String limiterId, int limiterMaxPermits)
			throws FailedToSaveConfigsException {

		appLogger.info("update Limiter (" + limiterId + ") with maxPerims: "
				+ limiterMaxPermits +" in rate controller");

		try {
			rateController.updateLimiter(limiterId, limiterMaxPermits);
			appLogger.info("Limiter (" + limiterId + ") is updated successfully");

		} catch (NoSuchLimiterException e) {
			appLogger.error("NoSuchLimiterException ", e);
			throw new FailedToSaveConfigsException(e);
		} catch (InvalidParameterException e) {
			appLogger.error("InvalidParameterException ", e);
			throw new FailedToSaveConfigsException(e);
		} catch (Exception e) {
			appLogger.error("Failed to update Sending Rate Limiter with id ["
					+ limiterId + "].", e);
			throw new FailedToSaveConfigsException(e);

		}
	}

	@Override
	public void addLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		appLogger.info("add Limiter into rate controller [" + limiter +"].");

		try {
			rateController.addLimiter(limiter.getLimiterId(),
					limiter.getMaxPermits());
			appLogger.info("Limiter is added successfully to rate controller.");

		} catch (Exception e) {
			appLogger.error("Failed to add Sending Rate Limiter with id ["
					+ limiter.getLimiterId() + "].", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	/**
	 * this method delete limiter from DB
	 * 
	 * @param limiter
	 * @throws DBException
	 */

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteLimiter(SendingRateLimiter limiter) throws DBException {
		try {
			appLogger.info("delete Limiter ["+limiter+"] from DB");
			sendingRateLimiterDao.remove(limiter);
			appLogger.info("limiter removed successfully from DB");

		} catch (Exception e) {
			appLogger.error("Failed to delete Sending Rate Limiter with id ["
					+ limiter.getLimiterId() + "] from db", e);
			throw new DBException(e);
		}

	}

	/**
	 * this method removes the deleted limiter from the running ones in
	 * rateController
	 * 
	 * @param limiter
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void RemoveLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		try {
			appLogger.info("remove Limiter ["+limiter+"] from rate controller");
			rateController.removeLimiter(limiter.getLimiterId());
			appLogger.info("limiter removed successfully from rate controller");

		} catch (Exception e) {
			appLogger.error("Failed to remove Sending Rate Limiter with id ["
					+ limiter.getLimiterId() + "].", e);
			throw new FailedToSaveConfigsException(e);
		}
	}
	
	
	
	/**
	 * this method create limiter into DB
	 * 
	 * @throws FailedToSaveConfigsException
	 */
	@Override
	public SendingRateLimiter createLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {

		try {
			appLogger.info("Create limiter into DB : " + limiter);
			sendingRateLimiterDao.create(limiter);
			appLogger.info("limiter created successfully");
		} catch (Exception e) {
			appLogger.error("FailedToSaveConfigsException", e);
			throw new FailedToSaveConfigsException(e);
		}
		return limiter;
	}
}