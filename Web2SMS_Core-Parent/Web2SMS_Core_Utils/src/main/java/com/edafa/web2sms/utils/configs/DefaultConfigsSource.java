package com.edafa.web2sms.utils.configs;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.ConfigDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Configuration;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsSource;
import com.edafa.web2sms.utils.configs.model.Config;

/**
 * Session Bean implementation class DefaultConfigsSource
 */
@Stateless
@LocalBean
public class DefaultConfigsSource implements ConfigsSource {

	@EJB
	ConfigDaoLocal configDao;

	@EJB(beanName = "Web2SMS-Core_Utils/ConfigsConversionBean")
	ConfigsConversionBean conversionBean;

	public DefaultConfigsSource() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Config> readConfigs(ModulesEnum module) throws FailedToReadConfigsException {
		List<Configuration> configsList;
		try {
			configsList = configDao.findEditableByModuleName(module.name());
		} catch (DBException e) {
			throw new FailedToReadConfigsException(e);
		}
		List<Config> configs = conversionBean.getConfigsModel(configsList);
		return configs;
	}

	@Override
	public void saveConfigs(ModulesEnum module, List<Config> configs) throws FailedToSaveConfigsException {
		try {
			configDao.edit(conversionBean.getConfigs(configs, module));
		} catch (DBException e) {
			throw new FailedToSaveConfigsException("Failed to persist configs to database", e);
		}

	}

}
