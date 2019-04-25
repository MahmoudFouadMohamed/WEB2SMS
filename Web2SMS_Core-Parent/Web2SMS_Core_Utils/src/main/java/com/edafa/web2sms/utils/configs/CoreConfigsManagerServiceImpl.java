package com.edafa.web2sms.utils.configs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.utils.configs.enums.ConfigType;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.GenericLimiters;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigsException;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerService;
import com.edafa.web2sms.utils.configs.model.Config;
import com.edafa.web2sms.utils.configs.model.ModuleConfigs;

@Stateless
@LocalBean
@WebService(name = "CoreConfigsManagerService", serviceName = "CoreConfigsManagerService", targetNamespace = "http://www.edafa.com/ws/utils/configs", endpointInterface = "com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerService")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class CoreConfigsManagerServiceImpl implements ConfigsManagerService {

	Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS
			.name());

	@EJB(beanName = "Web2SMS-Core_Utils/ConfigsManagerBean")
	ConfigsManagerBeanLocal coreConfigsManagerBean;

	@EJB(beanName = "SMSGW_Utils/ConfigsManagerBean")
	com.edafa.smsgw.utils.configs.interfaces.ConfigsManagerBeanLocal smsgwConfigsManagerBean;

	List<String> coreModules = null;
	List<String> smsgwModules = null;

	/**
	 * Default constructor.
	 */
	public CoreConfigsManagerServiceImpl() {
	}

	@PostConstruct
	void init() {
		coreModules = new ArrayList<String>();
		smsgwModules = new ArrayList<String>();
		for (ModulesEnum module : ModulesEnum.values()) {
			coreModules.add(module.name());
		}

		for (com.edafa.smsgw.utils.configs.enums.ModulesEnum module : com.edafa.smsgw.utils.configs.enums.ModulesEnum
				.values()) {
			smsgwModules.add(module.name());
		}
	}

	@Override
	public List<String> getModules() {
		List<String> modules = new ArrayList<>();
		modules.addAll(coreModules);
		modules.addAll(smsgwModules);
		return modules;
	}

	@Override
	public List<ModuleConfigs> readConfigs()
			throws FailedToReadConfigsException {
		List<ModuleConfigs> moduleConfigsList = new ArrayList<>();
		appLogger.info("Read modules configs for Core");
		moduleConfigsList.addAll(coreConfigsManagerBean.readConfigs());
		appLogger.info("Configuration are read, found:"
				+ moduleConfigsList.size());
		appLogger.info("Read modules configs for SMSGW");
		try {
			System.out.println("REFRESH ... readConfig");
			moduleConfigsList
					.addAll(getWeb2SMSModuleConfig(smsgwConfigsManagerBean
							.readConfigs()));
		} catch (com.edafa.smsgw.utils.configs.exception.FailedToReadConfigsException ex) {
			throw new FailedToReadConfigsException(ex);
		} catch (Exception e) {
			appLogger.error("Unhandled Exception during read smsgwConfigs");
		}
		appLogger.info("All Configuration are read, found:"
				+ moduleConfigsList.size());
		return moduleConfigsList;
	}

	@Override
	public ModuleConfigs readModuleConfigs(String module)
			throws FailedToReadConfigsException {
		ModuleConfigs moduleConfigs = null;
		if (coreModules.contains(module))
			moduleConfigs = coreConfigsManagerBean.readModuleConfigs(module);
		else if (smsgwModules.contains(module))
			try {
				System.out.println("REFRESH ... readModule");
				moduleConfigs = getWeb2SMSModuleConfig(smsgwConfigsManagerBean
						.readModuleConfigs(module));
			} catch (com.edafa.smsgw.utils.configs.exception.FailedToReadConfigsException ex) {
				throw new FailedToReadConfigsException(ex);
			}
		else
			throw new FailedToReadConfigsException("No such module");

		return moduleConfigs;
	}

	@Override
	public void refreshModuleConfigs(String module)
			throws FailedToReadConfigsException, InvalidConfigsException,
			FailedToSaveConfigsException {
		if (coreModules.contains(module)) {
			coreConfigsManagerBean.refreshModuleConfigs(module);
			if (module.equals(ModulesEnum.CampaignEngine.name())) {
				coreConfigsManagerBean.refreshLimiter(
						GenericLimiters.CampSystemLimiter.name(),
						(int) Configs.SMS_CAMPAIGN_SENDING_RATE.getValue());
                                coreConfigsManagerBean.refreshLimiter(
						GenericLimiters.CampDefaultLimiter.name(),
						(int) Configs.CAMPAIGN_DEFAULT_LIMITER.getValue());
			} else if (module.equals(ModulesEnum.SMSAPIEngine.name())) {
				coreConfigsManagerBean.refreshLimiter(
						GenericLimiters.SmsapiSystemLimiter.name(),
						(int) Configs.SMS_SMSAPI_SENDING_RATE.getValue());
                                coreConfigsManagerBean.refreshLimiter(
						GenericLimiters.SmsapiDefaultLimiter.name(),
						(int) Configs.SMSAPI_DEFAULT_LIMITER.getValue());
			}
		} else if (smsgwModules.contains(module))
			try {
				System.out.println("REFRESH ... refreshModule");
				smsgwConfigsManagerBean.refreshModuleConfigs(module);
				coreConfigsManagerBean.refreshLimiter(
						GenericLimiters.SystemLimiter.name(),
						smsgwConfigsManagerBean.getSystemSendingRate());
			} catch (com.edafa.smsgw.utils.configs.exception.FailedToReadConfigsException ex) {
				throw new FailedToReadConfigsException(ex);
			} catch (com.edafa.smsgw.utils.configs.exception.InvalidConfigsException ex) {
				List<com.edafa.smsgw.utils.configs.model.Config> tempInvalidConfigs = ex
						.getInvalidConfigs();
				List<Config> invalidConfigs = new ArrayList<>(
						tempInvalidConfigs.size());
				for (com.edafa.smsgw.utils.configs.model.Config tempInvalidConfig : tempInvalidConfigs) {
					invalidConfigs.add(getWeb2SMSConfig(tempInvalidConfig));
				}
				throw new InvalidConfigsException(ex.getModule(),
						invalidConfigs);
			}
		else
			throw new FailedToReadConfigsException("No such module");
	}

	@Override
	public void refreshAllModuleConfigs() throws FailedToReadConfigsException,
			InvalidConfigsException {
		coreConfigsManagerBean.refreshAllModuleConfigs();
		try {
			System.out.println("REFRESH ... refreshAll ... START");
			smsgwConfigsManagerBean.refreshAllModuleConfigs();
			System.out.println("REFRESH ... refreshAll ... END");
		} catch (com.edafa.smsgw.utils.configs.exception.FailedToReadConfigsException ex) {
			throw new FailedToReadConfigsException(ex);
		} catch (com.edafa.smsgw.utils.configs.exception.InvalidConfigsException ex) {
			List<com.edafa.smsgw.utils.configs.model.Config> tempInvalidConfigs = ex
					.getInvalidConfigs();
			List<Config> invalidConfigs = new ArrayList<>(
					tempInvalidConfigs.size());
			for (com.edafa.smsgw.utils.configs.model.Config tempInvalidConfig : tempInvalidConfigs) {
				invalidConfigs.add(getWeb2SMSConfig(tempInvalidConfig));
			}
			throw new InvalidConfigsException(ex.getModule(), invalidConfigs);
		}
	}

	@Override
	public void saveConfigs(ModuleConfigs moduleConfigs)
			throws FailedToSaveConfigsException, InvalidConfigsException {
		if (coreModules.contains(moduleConfigs.getModule())) {
                int campSendingRate = 0, smsapiSendingRate = 0, defaultCampSendingRate = 0, defaultSmsapiSendingRate = 0;
                if (moduleConfigs.getModule().equals(ModulesEnum.CampaignEngine.name()) || moduleConfigs.getModule().equals(ModulesEnum.SMSAPIEngine.name())) {
                   for (Config config :moduleConfigs.getConfigList()) {
                       if (config.getKey().equals(Configs.SMS_CAMPAIGN_SENDING_RATE.getProperty())) {
                           campSendingRate = Integer.parseInt(config.getValue());
                       }
                       
                       if (config.getKey().equals(Configs.CAMPAIGN_DEFAULT_LIMITER.getProperty())) {
                           defaultCampSendingRate = Integer.parseInt(config.getValue());
                       }
   
                       if (config.getKey().equals(Configs.SMS_SMSAPI_SENDING_RATE.getProperty())) {
                           smsapiSendingRate = Integer.parseInt(config.getValue());
                       }    
                       
                       if (config.getKey().equals(Configs.SMSAPI_DEFAULT_LIMITER.getProperty())) {
                           defaultSmsapiSendingRate = Integer.parseInt(config.getValue());
                       }  

                   }
                    if (campSendingRate == 0) {
                        campSendingRate = (int) Configs.SMS_CAMPAIGN_SENDING_RATE.getValue();
                    }
                    if (smsapiSendingRate == 0) {
                        smsapiSendingRate = (int) Configs.SMS_SMSAPI_SENDING_RATE.getValue();
                    }
                    
                    if (defaultCampSendingRate == 0) {
                        defaultCampSendingRate = (int) Configs.CAMPAIGN_DEFAULT_LIMITER.getValue();
                    }
                    
                    if (defaultSmsapiSendingRate == 0) {
                        defaultSmsapiSendingRate = (int) Configs.SMSAPI_DEFAULT_LIMITER.getValue();
                    }
                    
                    if (campSendingRate + smsapiSendingRate > smsgwConfigsManagerBean.getSystemSendingRate()) {
					throw new FailedToSaveConfigsException(
							"Invalid value: Campaign sending rate plus Smsapi sending rate exceed the system sending rate"
									+ ", campSendingRate=" + campSendingRate + ", smsapiSendingRate="
									+ smsapiSendingRate + ", systemSendingRate(Core)="
									+ smsgwConfigsManagerBean.getSystemSendingRate());
				}

                    if (defaultCampSendingRate > campSendingRate) {
                        throw new FailedToSaveConfigsException("Invalid value: Default Campaign sending rate exceed the campaign sending rate");

                    }

                    if (defaultSmsapiSendingRate > smsapiSendingRate) {
                        throw new FailedToSaveConfigsException("Invalid value: Default Smsapi sending rate exceed the smsapi sending rate");

                    }
                    
                }
			coreConfigsManagerBean.saveConfigs(moduleConfigs);
                } else if (smsgwModules.contains(moduleConfigs.getModule())) {
                    int systemSendingRate = -1;
			try {
				if (moduleConfigs.getModule().equals("SMSEngine")) {
					for (Config config : moduleConfigs.getConfigList()) {
						if (config.getKey().equals("SENDING_RATE")) {
							systemSendingRate = Integer.parseInt(config.getValue());
						}
					}
					
				} else {
					systemSendingRate = smsgwConfigsManagerBean.getSystemSendingRate();
				}
				
				if ((int) Configs.SMS_CAMPAIGN_SENDING_RATE.getValue()
						+ (int) Configs.SMS_SMSAPI_SENDING_RATE.getValue() > systemSendingRate) {
					throw new FailedToSaveConfigsException(
							"Invalid value: Campaign sending rate plus Smsapi sending rate exceed the system sending rate"
									+ ", campSendingRate=" + (int) Configs.SMS_CAMPAIGN_SENDING_RATE.getValue()
									+ ", smsapiSendingRate=" + (int) Configs.SMS_SMSAPI_SENDING_RATE.getValue()
									+ ", systemSendingRate(SMSGW)=" + systemSendingRate);
				}
                            
				System.out.println("REFRESH ... save");
				smsgwConfigsManagerBean
						.saveConfigs(getSMSGWModuleConfig(moduleConfigs));
			} catch (com.edafa.smsgw.utils.configs.exception.FailedToSaveConfigsException ex) {
				throw new FailedToSaveConfigsException(ex);
			} catch (com.edafa.smsgw.utils.configs.exception.InvalidConfigsException ex) {
				List<com.edafa.smsgw.utils.configs.model.Config> tempInvalidConfigs = ex
						.getInvalidConfigs();
				List<Config> invalidConfigs = new ArrayList<>(
						tempInvalidConfigs.size());
				for (com.edafa.smsgw.utils.configs.model.Config tempInvalidConfig : tempInvalidConfigs) {
					invalidConfigs.add(getWeb2SMSConfig(tempInvalidConfig));
				}
				throw new InvalidConfigsException(ex.getModule(),
						invalidConfigs);
			} 
            }
		else
			throw new FailedToSaveConfigsException("No such module");
	}

	private List<ModuleConfigs> getWeb2SMSModuleConfig(
			List<com.edafa.smsgw.utils.configs.model.ModuleConfigs> mcList) {
		List<ModuleConfigs> moduleConfigsList = new ArrayList<ModuleConfigs>();
		for (com.edafa.smsgw.utils.configs.model.ModuleConfigs mc : mcList) {
			moduleConfigsList.add(getWeb2SMSModuleConfig(mc));
		}
		return moduleConfigsList;
	}

	private List<com.edafa.smsgw.utils.configs.model.ModuleConfigs> getSMSGWModuleConfig(
			List<ModuleConfigs> mcList) {
		List<com.edafa.smsgw.utils.configs.model.ModuleConfigs> moduleConfigsList = new ArrayList<com.edafa.smsgw.utils.configs.model.ModuleConfigs>();
		for (ModuleConfigs mc : mcList) {
			moduleConfigsList.add(getSMSGWModuleConfig(mc));
		}
		return moduleConfigsList;
	}

	private ModuleConfigs getWeb2SMSModuleConfig(
			com.edafa.smsgw.utils.configs.model.ModuleConfigs mc) {
		ModuleConfigs moduleConfigs = new ModuleConfigs();
		List<Config> configs = new ArrayList<>();
		moduleConfigs.setConfigsApplied(mc.isConfigsApplied());
		moduleConfigs.setModule(mc.getModule());
		moduleConfigs.setConfigList(configs);
		for (com.edafa.smsgw.utils.configs.model.Config c : mc.getConfigList()) {
			configs.add(getWeb2SMSConfig(c));
		}
		return moduleConfigs;
	}

	private com.edafa.smsgw.utils.configs.model.ModuleConfigs getSMSGWModuleConfig(
			ModuleConfigs mc) {
		com.edafa.smsgw.utils.configs.model.ModuleConfigs moduleConfigs = new com.edafa.smsgw.utils.configs.model.ModuleConfigs();
		List<com.edafa.smsgw.utils.configs.model.Config> configs = new ArrayList<>();
		moduleConfigs.setConfigsApplied(mc.isConfigsApplied());
		moduleConfigs.setModule(mc.getModule());
		moduleConfigs.setConfigList(configs);
		for (Config c : mc.getConfigList()) {
			configs.add(getSMSGWConfig(c));
		}
		return moduleConfigs;
	}

	private Config getWeb2SMSConfig(com.edafa.smsgw.utils.configs.model.Config c) {
		Config config = new Config();
		config.setId(c.getId());
		config.setKey(c.getKey());
		config.setValue(c.getValue());
		config.setModule(c.getModule());
		config.setConfigType(ConfigType.valueOf(c.getConfigType().name()));
		config.getConfigType().setAcceptedValues(
				c.getConfigType().getAcceptedValues());
		config.setRunningConfig(c.getRunningConfig());
		config.setDescription(c.getDescription());
		return config;
	}

	private com.edafa.smsgw.utils.configs.model.Config getSMSGWConfig(Config c) {
		com.edafa.smsgw.utils.configs.model.Config config = new com.edafa.smsgw.utils.configs.model.Config();
		config.setId(c.getId());
		config.setKey(c.getKey());
		config.setValue(c.getValue());
		config.setModule(c.getModule());
		config.setConfigType(com.edafa.smsgw.utils.configs.enums.ConfigType
				.valueOf(c.getConfigType().name()));
		config.getConfigType().setAcceptedValues(
				c.getConfigType().getAcceptedValues());
		config.setRunningConfig(c.getRunningConfig());
		config.setDescription(c.getDescription());
		return config;
	}

	@Override
	public List<SendingRateLimiter> readLimiters()
			throws FailedToReadConfigsException {
		try {
			List<SendingRateLimiter> limiters = coreConfigsManagerBean
					.readLimiters();
//			if (limiters != null)
//				System.out.println("limiters from db size in coreConfigs :"
//						+ limiters.size());
			return limiters;
		} catch (Exception e) {
			appLogger.error("Failed to read limiters from DB ", e);
			throw new FailedToReadConfigsException(e);
		}
	}

	@Override
	public void saveLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		try {
			coreConfigsManagerBean.saveLimiter(limiter);
		} catch (Exception e) {
			appLogger.error("Failed to save limiters into DB ", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	@Override
	public List<SendingRateLimiter> getRunningLimiters()
			throws FailedToReadConfigsException {
		try {
			List<SendingRateLimiter> runningLimiters = coreConfigsManagerBean
					.getRunningLimiters();

			return runningLimiters;
		} catch (Exception e) {
			appLogger.error("Failed to read running limiters ", e);
			throw new FailedToReadConfigsException(e);
		}
	}

	@Override
	public void refreshLimiters(List<SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException {
		try {
			coreConfigsManagerBean.refreshLimiters(limiters);
		} catch (Exception e) {
			appLogger.error("Failed to refresh limiters ", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	@Override
	public void refreshLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		try {
			coreConfigsManagerBean.refreshLimiter(limiter);
		} catch (Exception e) {
			appLogger.error("Failed to refresh limiter ", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	@Override
	public void addLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		try {
			coreConfigsManagerBean.addLimiter(limiter);
		} catch (Exception e) {
			appLogger.error("Failed to add limiter to rate controller", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	@Override
	public void saveLimiters(List<SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException {
		try {
			coreConfigsManagerBean.saveLimiters(limiters);
		} catch (Exception e) {
			appLogger.error("Failed to save limiter into DB", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	@Override
	public void deleteLimiter(SendingRateLimiter limiter) throws DBException {
		try {
			coreConfigsManagerBean.deleteLimiter(limiter);
		} catch (Exception e) {
			appLogger.error("Failed to delete limiter from DB", e);
			throw new DBException(e);
		}
	}

	@Override
	public void RemoveLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		try {
			coreConfigsManagerBean.RemoveLimiter(limiter);
		} catch (Exception e) {
			appLogger
					.error("Failed to Remove limiter from rate Controller ", e);
			throw new FailedToSaveConfigsException(e);
		}
	}

	@Override
	public SendingRateLimiter createLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException {
		try {
			coreConfigsManagerBean.createLimiter(limiter);
			return limiter;
		} catch (Exception e) {
			appLogger.error("Failed to create limiters into DB ", e);
			throw new FailedToSaveConfigsException(e);
		}
	}


}
