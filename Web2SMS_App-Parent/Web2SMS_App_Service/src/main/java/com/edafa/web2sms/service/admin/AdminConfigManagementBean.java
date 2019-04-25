package com.edafa.web2sms.service.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.ws.WebServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.ConfigDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ModuleDaoLocal;
import com.edafa.web2sms.service.admin.exception.ConfigsServiceConnectionException;
import com.edafa.web2sms.service.admin.interfaces.AdminConfigClientsBeanLocal;
import com.edafa.web2sms.service.admin.interfaces.AdminConfigManagementBeanLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.ws.utils.configs.DBException_Exception;
import com.edafa.ws.utils.configs.FailedToReadConfigsException;
import com.edafa.ws.utils.configs.FailedToReadConfigsException_Exception;
import com.edafa.ws.utils.configs.FailedToSaveConfigsException_Exception;
import com.edafa.ws.utils.configs.InvalidConfigsException_Exception;
import com.edafa.ws.utils.configs.SendingRateLimiter;
import com.edafa.ws.utils.configs.model.ModuleConfigs;

/**
 * Session Bean implementation class AdminConfigManagementBean
 */
@Stateless
@LocalBean
public class AdminConfigManagementBean
		implements
			AdminConfigManagementBeanLocal {
	Logger appLogger = LogManager.getLogger(LoggersEnum.ADMIN_MNGMT.name());
	@EJB
	ConfigDaoLocal configDao;

	@EJB
	ModuleDaoLocal moduleDao;

	@EJB
	AdminConfigClientsBeanLocal adminConfigClientsBean;
	
	List<String> appModules;
	List<String> coreModules;
	List<String> provModules;
	List<String> uiModules;
	List<String> smsgwModules;
	List<String> smsApiModules;
	List<String> accountManagementModules;
	List<String> reportingModules;
	
	// Constructor--------------------------------------------------

	// Action-------------------------------------------------------

	@PostConstruct
	void init() {
		adminConfigClientsBean.readURLs();

		initModulesLists();
	}

	private void initModulesLists() {
		try {
			appLogger.info("Initalizing App modules list");
			appModules = adminConfigClientsBean.getAppConfigsManagerService().getModules();
		} catch (Exception e) {
			appLogger.error("Failed to init App modules list", e);
			appModules = new ArrayList<>();
		}

		try {
			appLogger.info("Initalizing Core modules list");
			coreModules = adminConfigClientsBean.getCoreConfigsManagerService().getModules();
		} catch (Exception e) {
			appLogger.error("Failed to init Core modules list", e);
			coreModules = new ArrayList<>();
		}

		try {
			appLogger.info("Initalizing UI modules list");
			uiModules = adminConfigClientsBean.getUiConfigsManagerService().getModules();
		} catch (Exception e) {
			appLogger.error("Failed to init UI modules list", e);
			uiModules = new ArrayList<>();
		}

		try {
			appLogger.info("Initalizing SMS API modules list");
			smsApiModules = adminConfigClientsBean.getSmsApiConfigsManagerService().getModules();
		} catch (Exception e) {
			appLogger.error("Failed to init SMS API modules list", e);
			smsApiModules = new ArrayList<>();
		}

		try {
			appLogger.info("Initalizing Account Management modules list");
			accountManagementModules = adminConfigClientsBean
					.getAccountManagementConfigsManagerService().getModules();
		} catch (Exception e) {
			appLogger.error("Failed to init Account Management modules list", e);
			accountManagementModules = new ArrayList<>();
		}
		try {
			appLogger.info("Initalizing Provisioning modules list");
			provModules = adminConfigClientsBean
					.getProvConfigsManagerService().getModules();
		} catch (Exception e) {
			appLogger.error("Failed to init Provisioning modules list", e);
			provModules = new ArrayList<>();
		}
		// try {
		// appLogger.info("Initalizing SMSGW modules list");
		// coreModules = smsgwConfigsManagerService.getModules();
		// } catch (Exception e) {
		// appLogger.error("Failed to init SMSGW modules list", e);
		// smsgwModules = new ArrayList<>();
		// }

		try {
			appLogger.info("Initalizing Reporting modules list");
			reportingModules = adminConfigClientsBean.getReportingConfigsManagerService().getModules();
		} catch (Exception e) {
			appLogger.error("Failed to init Reporting modules list", e);
			reportingModules = new ArrayList<>();
		}
	}

	@Override
	public List<ModuleConfigs> readConfigs()
			throws FailedToReadConfigsException_Exception {
		List<ModuleConfigs> moduleConfigsList = new ArrayList<ModuleConfigs>();
			initModulesLists();

		try {
			appLogger.info("Read modules configs for Core");
			moduleConfigsList.addAll(adminConfigClientsBean.getCoreConfigsManagerService().readConfigs());

		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read Core configs "
					+ e.getFaultInfo().getMessage(), e);
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read Core configs ", e);
		}

		try {
			appLogger.info("Read modules configs for App");
			moduleConfigsList.addAll(adminConfigClientsBean.getAppConfigsManagerService().readConfigs());

		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read App configs "
					+ e.getFaultInfo().getMessage(), e);
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read App configs ", e);
		}

		try {
			appLogger.info("Read modules configs for UI");
			moduleConfigsList.addAll(adminConfigClientsBean.getUiConfigsManagerService().readConfigs());

		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read UI configs "
					+ e.getFaultInfo().getMessage(), e);
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read UI configs ", e);
		}

		try {
			appLogger.info("Read modules configs for smsAPI");
			moduleConfigsList.addAll(adminConfigClientsBean.getSmsApiConfigsManagerService().readConfigs());

		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read smsAPI configs "
					+ e.getFaultInfo().getMessage(), e);
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read smsAPI configs ", e);
		}
		
		try {
			appLogger.info("Read modules configs for Account Management");
			moduleConfigsList.addAll(adminConfigClientsBean.getAccountManagementConfigsManagerService().readConfigs());

		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read Account Management configs "
					+ e.getFaultInfo().getMessage(), e);
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to Account Management configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read Account Management configs ", e);
		}

		try {
			appLogger.info("Read modules configs for Provisioning");
			moduleConfigsList.addAll(adminConfigClientsBean.getProvConfigsManagerService().readConfigs());

		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read Provisioning configs "
					+ e.getFaultInfo().getMessage(), e);
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to Provisioning configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read Provisioning configs ", e);
		}


		// try {
		// appLogger.info("Read modules configs for SMSGW");
		// moduleConfigsList.addAll(smsgwConfigsManagerService.readConfigs());
		// } catch (FailedToReadConfigsException_Exception e) {
		// appLogger.error("Failed to read SMSGW configs " +
		// e.getFaultInfo().getMessage(), e);
		// } catch (WebServiceException e) {
		// appLogger.error("Failed to connect to configs service", e);
		// } catch (Exception e) {
		// appLogger.error("Failed to read SMSGW configs ", e);
		// }

		try {
			appLogger.info("Read modules configs for Reporting");
			moduleConfigsList.addAll(adminConfigClientsBean.getReportingConfigsManagerService().readConfigs());
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read Reporting configs " + e.getFaultInfo().getMessage(), e);
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to Reporting configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read Reporting configs ", e);
		}

		if (moduleConfigsList.isEmpty()) {
			throw new FailedToReadConfigsException_Exception(
					"Failed to read any configuration", null);
		}

		return moduleConfigsList;
	}

	@Override
	public ModuleConfigs readModuleConfigs(String module)
			throws FailedToReadConfigsException_Exception,
			ConfigsServiceConnectionException {
		appLogger
				.info("Check which EAR the module [" + module + "] belongs to");
		ModuleConfigs moduleConfigs = null;

		try {
			if (appModules.contains(module)) {
				appLogger
						.info("The module [" + module + "] belongs to App EAR");
				appLogger.info("Reading configs for module [" + module + "]");
				moduleConfigs = adminConfigClientsBean.getAppConfigsManagerService()
						.readModuleConfigs(module);

			} else if (coreModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to Core EAR");
				appLogger.info("Reading configs for module [" + module + "]");

				moduleConfigs = adminConfigClientsBean.getCoreConfigsManagerService()
						.readModuleConfigs(module);

			} else if (uiModules.contains(module)) {
				appLogger.info("The module [" + module + "] belongs to UI EAR");
				appLogger.info("Reading configs for module [" + module + "]");

				moduleConfigs = adminConfigClientsBean.getUiConfigsManagerService()
						.readModuleConfigs(module);

			} else if (smsApiModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to SMSAPI EAR");
				appLogger.info("Reading configs for module [" + module + "]");

				moduleConfigs = adminConfigClientsBean.getSmsApiConfigsManagerService()
						.readModuleConfigs(module);
			} else if (accountManagementModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to account management EAR");
				appLogger.info("Reading configs for module [" + module + "]");

				moduleConfigs = adminConfigClientsBean.getAccountManagementConfigsManagerService()
						.readModuleConfigs(module);
			} else if (reportingModules.contains(module)) {
				appLogger.info("The module [" + module + "] belongs to Reporting EAR");
				appLogger.info("Reading configs for module [" + module + "]");

				moduleConfigs = adminConfigClientsBean.getReportingConfigsManagerService().readModuleConfigs(module);
			}else if (provModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to provisioning EAR");
				appLogger.info("Reading configs for module [" + module + "]");

				moduleConfigs = adminConfigClientsBean.getProvConfigsManagerService()
						.readModuleConfigs(module);
			}
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for module [" + module
					+ "] " + e.getMessage());
			throw e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			throw new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Failed to read configs for module [" + module
					+ "]", e);
			throw new FailedToReadConfigsException_Exception(
					"Failed to read configs for module [" + module + "]",
					new FailedToReadConfigsException());
		}

		if (moduleConfigs == null)
			throw new FailedToReadConfigsException_Exception("No such module",
					new FailedToReadConfigsException());

		return moduleConfigs;
	}

	@Override
	public void refreshModuleConfigs(String module)
			throws FailedToReadConfigsException_Exception,
			InvalidConfigsException_Exception,
			ConfigsServiceConnectionException,
			FailedToSaveConfigsException_Exception {
		appLogger.info("Refresh module configs, module [" + module + "]");
		appLogger
				.info("Check which EAR the module [" + module + "] belongs to");
		try {
			if (appModules.contains(module)) {
				appLogger
						.info("The module [" + module + "] belongs to App EAR");
//				adminConfigClientsBean.readURLs();
				adminConfigClientsBean.getAppConfigsManagerService().refreshModuleConfigs(module);
				adminConfigClientsBean.readURLs();

			} else if (coreModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to Core EAR");
				adminConfigClientsBean.getCoreConfigsManagerService().refreshModuleConfigs(module);

			} else if (uiModules.contains(module)) {
				appLogger.info("The module [" + module + "] belongs to UI EAR");
				adminConfigClientsBean.getUiConfigsManagerService().refreshModuleConfigs(module);
			} else if (smsApiModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to SMSAPI EAR");
				adminConfigClientsBean.getSmsApiConfigsManagerService().refreshModuleConfigs(module);
			} else if (accountManagementModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to account management EAR");
				adminConfigClientsBean.getAccountManagementConfigsManagerService().refreshModuleConfigs(module);
			} else if (reportingModules.contains(module)) {
				appLogger.info("The module [" + module + "] belongs to Reporting EAR");
				adminConfigClientsBean.getReportingConfigsManagerService().refreshModuleConfigs(module);
			} else if (provModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to provisioning EAR");
				adminConfigClientsBean.getProvConfigsManagerService().refreshModuleConfigs(module);

			} else {
				throw new FailedToReadConfigsException_Exception(
						"No such module", null);
			}
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for module [" + module
					+ "] " + e.getMessage());
			throw e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			throw new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		}
	}

	@Override
	public void refreshAllModuleConfigs()
			throws FailedToReadConfigsException_Exception,
			InvalidConfigsException_Exception,
			ConfigsServiceConnectionException {
		appLogger.info("Refresh all modules configs for App EAR");
		FailedToReadConfigsException_Exception er = null;
		InvalidConfigsException_Exception ec = null;
		ConfigsServiceConnectionException csce = null;

		try {
			appLogger.info("Refresh all modules configs for App EAR");
//			adminConfigClientsBean.readURLs();
			adminConfigClientsBean.getAppConfigsManagerService().refreshAllModuleConfigs();
			adminConfigClientsBean.readURLs();
			appLogger
					.info("Configuration refreshed for all modules configs for App EAR");
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for App EAR ", e);
			er = e;
		} catch (InvalidConfigsException_Exception e) {
			appLogger.error("Invalid Configs for App EAR ", e);
			ec = e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			csce = new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		}

		try {
			appLogger.info("Refresh all modules configs for Core EAR");
			adminConfigClientsBean.getCoreConfigsManagerService().refreshAllModuleConfigs();
			appLogger
					.info("Configuration refreshed for all modules configs for Core EAR");
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for Core EAR ", e);
			er = e;
		} catch (InvalidConfigsException_Exception e) {
			appLogger.error("Invalid Configs for Core EAR ", e);
			ec = e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			csce = new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		}

		try {
			appLogger.info("Refresh all modules configs for UI EAR");
			adminConfigClientsBean.getUiConfigsManagerService().refreshAllModuleConfigs();
			appLogger
					.info("Configuration refreshed for all modules configs for UI EAR");
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for UI EAR ", e);
			er = e;
		} catch (InvalidConfigsException_Exception e) {
			appLogger.error("Invalid Configs for UI EAR ", e);
			ec = e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			csce = new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Exception", e);

			// e.printStackTrace();
		}

		try {
			appLogger.info("Refresh all modules configs for SMSAPI EAR");
			adminConfigClientsBean.getSmsApiConfigsManagerService().refreshAllModuleConfigs();
			appLogger
					.info("Configuration refreshed for all modules configs for SMSAPI EAR");
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for SMSAPI EAR ", e);
			er = e;
		} catch (InvalidConfigsException_Exception e) {
			appLogger.error("Invalid Configs for SMSAPI EAR ", e);
			ec = e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			csce = new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Exception", e);
			// e.printStackTrace();
		}
		
		try {
			appLogger.info("Refresh all modules configs for account management EAR");
			adminConfigClientsBean.getAccountManagementConfigsManagerService().refreshAllModuleConfigs();
			appLogger
					.info("Configuration refreshed for all modules configs for account management EAR");
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for  account management EAR ", e);
			er = e;
		} catch (InvalidConfigsException_Exception e) {
			appLogger.error("Invalid Configs for  account management EAR ", e);
			ec = e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			csce = new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Exception", e);
			// e.printStackTrace();
		}

		try {
			appLogger.info("Refresh all modules configs for Reporting EAR");
			adminConfigClientsBean.getReportingConfigsManagerService().refreshAllModuleConfigs();
			appLogger.info("Configuration refreshed for all modules configs for Reporting EAR");
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for  Reporting EAR ", e);
			er = e;
		} catch (InvalidConfigsException_Exception e) {
			appLogger.error("Invalid Configs for  Reporting EAR ", e);
			ec = e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			csce = new ConfigsServiceConnectionException("Failed to connect to configs service", e);
		}
		try{
			appLogger.info("Refresh all modules configs for provisioning EAR");
			adminConfigClientsBean.getProvConfigsManagerService().refreshAllModuleConfigs();
			appLogger
					.info("Configuration refreshed for all modules configs for provisioning EAR");
		} catch (FailedToReadConfigsException_Exception e) {
			appLogger.error("Failed to read configs for provisioning EAR ", e);
			er = e;
		} catch (InvalidConfigsException_Exception e) {
			appLogger.error("Invalid Configs for provisioning EAR ", e);
			ec = e;
		} catch (WebServiceException e) {
			appLogger.error("Failed to connect to configs service", e);
			csce = new ConfigsServiceConnectionException(
					"Failed to connect to configs service", e);
		} catch (Exception e) {
			appLogger.error("Exception", e);
			// e.printStackTrace();
		}

		if (ec != null) {
			throw ec;
		}
		if (er != null) {
			throw er;
		}
		if (csce != null)
			throw csce;
	}

	@Override
	public void saveConfigs(ModuleConfigs moduleConfigs)
			throws FailedToSaveConfigsException_Exception,
			InvalidConfigsException_Exception,
			ConfigsServiceConnectionException {
		String module = moduleConfigs.getModule();
		FailedToSaveConfigsException_Exception es = null;
		InvalidConfigsException_Exception ec = null;
		ConfigsServiceConnectionException csce = null;

		appLogger
				.info("Check which EAR the module [" + module + "] belongs to");
		if (appModules.contains(module)) {
			appLogger.info("The module [" + module + "] belongs to App EAR");
			try {
				appLogger.info("Save configs for App EAR, module [" + module
						+ "]");
				adminConfigClientsBean.getAppConfigsManagerService().saveConfigs(moduleConfigs);
				appLogger.info("Configs saved successfully for App EAR");
			} catch (FailedToSaveConfigsException_Exception e) {
				es = e;
			} catch (InvalidConfigsException_Exception e) {
				ec = e;
			} catch (WebServiceException e) {
				appLogger.error("Failed to connect to configs service", e);
				csce = new ConfigsServiceConnectionException(
						"Failed to connect to configs service", e);
			} catch (Exception e) {
				appLogger.error("Failed to save App EAR configs", e);
			}
		} else {
			if (coreModules.contains(module)) {
				appLogger.info("The module [" + module
						+ "] belongs to Core EAR");
				appLogger.info("Save configs for Core EAR, module [" + module
						+ "]");
				try {
					adminConfigClientsBean.getCoreConfigsManagerService().saveConfigs(moduleConfigs);
					appLogger.info("Configs saved successfully for Core EAR");
				} catch (FailedToSaveConfigsException_Exception e) {
					es = e;
				} catch (InvalidConfigsException_Exception e) {
					ec = e;
				} catch (WebServiceException e) {
					appLogger.error("Failed to connect to configs service", e);
					csce = new ConfigsServiceConnectionException(
							"Failed to connect to configs service", e);
				} catch (Exception e) {
					appLogger.error("Failed to save App EAR configs", e);
				}
			} else if (uiModules.contains(module)) {
				appLogger.info("The module [" + module + "] belongs to UI EAR");
				appLogger.info("Save configs for UI EAR, module [" + module
						+ "]");
				try {
					adminConfigClientsBean.getUiConfigsManagerService().saveConfigs(moduleConfigs);
					appLogger.info("Configs saved successfully for UI EAR");
				} catch (FailedToSaveConfigsException_Exception e) {
					es = e;
				} catch (InvalidConfigsException_Exception e) {
					ec = e;
				} catch (WebServiceException e) {
					appLogger.error("Failed to connect to configs service", e);
					csce = new ConfigsServiceConnectionException(
							"Failed to connect to configs service", e);
				} catch (Exception e) {
					appLogger.error("Failed to save App EAR configs", e);
				}
			} else if (smsApiModules.contains(module)) {

				appLogger.info("The module [" + module
						+ "] belongs to SMSAPI EAR");
				appLogger.info("Save configs for SMSAPI EAR, module [" + module
						+ "]");
				try {
					adminConfigClientsBean.getSmsApiConfigsManagerService().saveConfigs(moduleConfigs);
					appLogger.info("Configs saved successfully for SMSAPI EAR");
				} catch (FailedToSaveConfigsException_Exception e) {
					es = e;
				} catch (InvalidConfigsException_Exception e) {
					ec = e;
				} catch (WebServiceException e) {
					appLogger.error("Failed to connect to configs service", e);
					csce = new ConfigsServiceConnectionException(
							"Failed to connect to configs service", e);
				} catch (Exception e) {
					appLogger.error("Failed to save SMSAPI EAR configs", e);
				}

			} else if (accountManagementModules.contains(module)) {

				appLogger.info("The module [" + module
						+ "] belongs to account management EAR");
				appLogger.info("Save configs for account management EAR, module [" + module
						+ "]");
				try {
					adminConfigClientsBean.getAccountManagementConfigsManagerService().saveConfigs(moduleConfigs);
					appLogger.info("Configs saved successfully for account management EAR");
				} catch (FailedToSaveConfigsException_Exception e) {
					es = e;
				} catch (InvalidConfigsException_Exception e) {
					ec = e;
				} catch (WebServiceException e) {
					appLogger.error("Failed to connect to configs service", e);
					csce = new ConfigsServiceConnectionException(
							"Failed to connect to configs service", e);
				} catch (Exception e) {
					appLogger.error("Failed to save account management EAR configs", e);
				}

			} else if (provModules.contains(module)) {

				appLogger.info("The module [" + module
						+ "] belongs to provisioning EAR");
				appLogger.info("Save configs for provisioning EAR, module [" + module
						+ "]");
				try {
					adminConfigClientsBean.getProvConfigsManagerService().saveConfigs(moduleConfigs);
					appLogger.info("Configs saved successfully for provisioning EAR");
				} catch (FailedToSaveConfigsException_Exception e) {
					es = e;
				} catch (InvalidConfigsException_Exception e) {
					ec = e;
				} catch (WebServiceException e) {
					appLogger.error("Failed to connect to configs service", e);
					csce = new ConfigsServiceConnectionException(
							"Failed to connect to configs service", e);
				} catch (Exception e) {
					appLogger.error("Failed to save provisioning EAR configs", e);
				}

			} else if (reportingModules.contains(module)) {

				appLogger.info("The module [" + module + "] belongs to Reporting EAR");
				appLogger.info("Save configs for Reporting EAR, module [" + module + "]");
				try {
					adminConfigClientsBean.getReportingConfigsManagerService().saveConfigs(moduleConfigs);
					appLogger.info("Configs saved successfully for Reporting EAR");
				} catch (FailedToSaveConfigsException_Exception e) {
					es = e;
				} catch (InvalidConfigsException_Exception e) {
					ec = e;
				} catch (WebServiceException e) {
					appLogger.error("Failed to connect to configs service", e);
					csce = new ConfigsServiceConnectionException("Failed to connect to configs service", e);
				} catch (Exception e) {
					appLogger.error("Failed to save Reporting EAR configs", e);
				}

			} else {
				appLogger.info(module
						+ " NOT found in module lists, appModules="
						+ appModules + ", coreModules=" + coreModules
						+ ", smsgwModules=" + smsgwModules + ", uiModules="
						+ uiModules + ", smsApiModules=" + smsApiModules
						+ ", provModules=" + provModules
						+ ", accountManagement modules="
						+ accountManagementModules);
	throw new FailedToSaveConfigsException_Exception("No such module : " + module, null);
			}
		}

		if (ec != null) {
			appLogger.info("Failed to save configs: "
					+ ec.getFaultInfo().getMessage());
			throw ec;
		}
		if (es != null) {
			appLogger.info("Invalid configuration: "
					+ es.getFaultInfo().getMessage());
			throw es;
		}
		if (csce != null)
			throw csce;
	}

	@Override
	public List<com.edafa.ws.utils.configs.SendingRateLimiter> readLimiters()
			throws FailedToReadConfigsException_Exception {

		List<com.edafa.ws.utils.configs.SendingRateLimiter> limiters = null;
		try {
			limiters = adminConfigClientsBean.getCoreConfigsManagerService().readLimiters();
		} catch (FailedToReadConfigsException_Exception ex) {
			appLogger.error("FailedToReadConfigsException_Exception ", ex);

			throw new FailedToReadConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}
		return limiters;
	}

	@Override
	public void saveLimiter(
			com.edafa.ws.utils.configs.SendingRateLimiter limiter)
			throws FailedToSaveConfigsException_Exception {
		try {
			adminConfigClientsBean.getCoreConfigsManagerService().saveLimiter(limiter);
		} catch (FailedToSaveConfigsException_Exception ex) {
			appLogger.error("FailedToSaveConfigsException_Exception ", ex);

			throw new FailedToSaveConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}
	}

	@Override
	public List<com.edafa.ws.utils.configs.SendingRateLimiter> getRunningLimiters()
			throws FailedToReadConfigsException_Exception {
		List<com.edafa.ws.utils.configs.SendingRateLimiter> limiters = null;
		try {
			limiters = adminConfigClientsBean.getCoreConfigsManagerService().getRunningLimiters();
		} catch (FailedToReadConfigsException_Exception ex) {
			appLogger.error("FailedToReadConfigsException_Exception ", ex);

			throw new FailedToReadConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}
		return limiters;
	}

	@Override
	public void refreshLimiters(
			List<com.edafa.ws.utils.configs.SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException_Exception {
		try {
			adminConfigClientsBean.getCoreConfigsManagerService().refreshLimiters(limiters);
		} catch (FailedToSaveConfigsException_Exception ex) {
			appLogger.error("FailedToSaveConfigsException_Exception ", ex);

			throw new FailedToSaveConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}
	}

	@Override
	public void refreshLimiter(
			com.edafa.ws.utils.configs.SendingRateLimiter limiter)
			throws FailedToSaveConfigsException_Exception {
		try {
			adminConfigClientsBean.getCoreConfigsManagerService().refreshLimiter(limiter);
		} catch (FailedToSaveConfigsException_Exception ex) {
			appLogger.error("FailedToSaveConfigsException_Exception ", ex);

			throw new FailedToSaveConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}
	}

	@Override
	public void addLimiter(com.edafa.ws.utils.configs.SendingRateLimiter limiter)
			throws FailedToSaveConfigsException_Exception {

		try {
			adminConfigClientsBean.getCoreConfigsManagerService().addLimiter(limiter);
		} catch (FailedToSaveConfigsException_Exception ex) {
			appLogger.error("FailedToSaveConfigsException_Exception ", ex);

			throw new FailedToSaveConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}
	}

	@Override
	public void saveLimiters(
			List<com.edafa.ws.utils.configs.SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException_Exception {
		try {
			adminConfigClientsBean.getCoreConfigsManagerService().saveLimiters(limiters);
		} catch (FailedToSaveConfigsException_Exception ex) {
			appLogger.error("FailedToSaveConfigsException_Exception ", ex);

			throw new FailedToSaveConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteLimiterAndRefresh(
			com.edafa.ws.utils.configs.SendingRateLimiter limiter)
			throws DBException_Exception,
			FailedToSaveConfigsException_Exception {

		try {
			appLogger.info("Deleting limiter" + limiter.getLimiterId()
					+ " from DB");
			adminConfigClientsBean.getCoreConfigsManagerService().deleteLimiter(limiter);

			appLogger.info("limiter deleted successfuly, removing limiter"
					+ limiter.getLimiterId() + " from rate controller");
			adminConfigClientsBean.getCoreConfigsManagerService().removeLimiter(limiter);

			appLogger.info("limiter " + limiter.getLimiterId()
					+ "  removed successfully from rate controller");
		} catch (DBException_Exception ex) {
			appLogger.error("DBException_Exception ", ex);
			throw new DBException_Exception(ex.getMessage(), ex.getFaultInfo(),
					ex);
		} catch (FailedToSaveConfigsException_Exception ex) {
			appLogger.error("FailedToSaveConfigsException_Exception ", ex);

			throw new FailedToSaveConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		}

	}

	@Override
	public void createLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException_Exception {

		try {
			appLogger.info("Creating limiter into from DB");
			limiter = adminConfigClientsBean.getCoreConfigsManagerService().createLimiter(limiter);

			appLogger.info("limiter is created successfuly, create limiter"
					+ limiter.getLimiterId() + " on rate controller");
			adminConfigClientsBean.getCoreConfigsManagerService().addLimiter(limiter);

			appLogger.info("limiter " + limiter.getLimiterId()
					+ "  created successfully on rate controller");
		} catch (FailedToSaveConfigsException_Exception ex) {
			appLogger.error("FailedToSaveConfigsException_Exception ", ex);

			throw new FailedToSaveConfigsException_Exception(ex.getMessage(),
					ex.getFaultInfo(), ex);

		} catch (Exception ex) {
			appLogger.error("Exception ", ex);
		}
	}

}
