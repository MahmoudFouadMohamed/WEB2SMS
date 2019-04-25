package com.edafa.web2sms.acc_manag.utils.configs;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.logging.log4j.LogManager; 
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.exception.InvalidConfigsException;
import com.edafa.web2sms.acc_manag.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.edafa.web2sms.acc_manag.utils.configs.interfaces.ConfigsManagerService;
import com.edafa.web2sms.acc_manag.utils.configs.model.ModuleConfigs;

/**
 * Session Bean implementation class AccManagConfigsManagerServiceImpl
 */
@Stateless(name = "AccManagConfigsManagerServiceImpl")
@LocalBean
@WebService(name = "AccManagConfigsManagerService", serviceName = "AccManagConfigsManagerService", targetNamespace = "http://www.edafa.com/ws/utils/configs", endpointInterface = "com.edafa.web2sms.acc_manag.utils.configs.interfaces.ConfigsManagerService")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class AccManagConfigsManagerServiceImpl implements ConfigsManagerService {

	Logger appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());

	@EJB
	ConfigsManagerBeanLocal configsManagerBean;

	/**
	 * Default constructor.
	 */
	public AccManagConfigsManagerServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getModules() {
		List<String> modules = new ArrayList();
		for (AccountManagModulesEnum module : AccountManagModulesEnum.values()) {
			modules.add(module.name());
		}
		return modules;
	}

	@Override
	public List<ModuleConfigs> readConfigs() throws FailedToReadConfigsException {
		appLogger.info("Read modules configs for AccoutManagment");
		return configsManagerBean.readConfigs();
	}

	@Override
	public ModuleConfigs readModuleConfigs(String module) throws FailedToReadConfigsException {
		return configsManagerBean.readModuleConfigs(module);
	}

	@Override
	public void refreshModuleConfigs(String module) throws FailedToReadConfigsException, InvalidConfigsException {
		configsManagerBean.refreshModuleConfigs(module);

	}

	@Override
	public void refreshAllModuleConfigs() throws FailedToReadConfigsException, InvalidConfigsException {
		configsManagerBean.refreshAllModuleConfigs();
	}

	@Override
	public void saveConfigs(ModuleConfigs moduleConfigs) throws FailedToSaveConfigsException, InvalidConfigsException {
		configsManagerBean.saveConfigs(moduleConfigs);
	}

}
