package com.edafa.web2sms.utils.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.ModuleDaoLocal;
import com.edafa.web2sms.dalayer.model.Configuration;
import com.edafa.web2sms.dalayer.model.Module;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.model.Config;

/**
 * Session Bean implementation class ConfigsConversionBean
 */
@Stateless
@LocalBean
public class ConfigsConversionBean {

	@EJB
	ModuleDaoLocal moduleDao;

	public Config getConfigModel(Configuration conf) {
		Config config = new Config();
		config.setId(conf.getId());
		config.setKey(conf.getKey());
		config.setValue(conf.getValue());
		config.setDescription(conf.getDescription());
		config.setModule(conf.getModuleId().getName());

		try {
			Configs enumConfig = Configs.getConfig(ModulesEnum.valueOf(conf.getModuleId().getName()), conf.getKey());
			if (enumConfig != null) {
				config.setConfigType(enumConfig.getType());
				config.setRunningConfig(enumConfig.getValue().toString());
			}
		} catch (Exception e) {
		}
		return config;
	}

	public Configuration getConfig(Config conf, Module mod) {
		Configuration configuration = new Configuration();
		configuration.setDescription(conf.getDescription());
		configuration.setEditFlag(true);
		configuration.setId(conf.getId());
		configuration.setKey(conf.getKey());
		configuration.setValue(conf.getValue().toString());
		configuration.setModuleId(mod);
		return configuration;
	}

	public List<Config> getConfigsModel(List<Configuration> configurations) {
		List<Config> mappedConfigs = new ArrayList<Config>();
		for (Configuration conf : configurations) {
			mappedConfigs.add(getConfigModel(conf));
		}
		return mappedConfigs;
	}

	public Map<String, Config> getConfigsMap(List<Configuration> configurations) {
		Map<String, Config> mappedConfigs = new HashMap<String, Config>();
		for (Configuration conf : configurations) {
			mappedConfigs.put(conf.getKey(), getConfigModel(conf));
		}
		return mappedConfigs;
	}

	public List<Configuration> getConfigs(List<Config> configs, ModulesEnum module) {
		List<Configuration> mappedConfigs = new ArrayList<Configuration>();
		Module mod = moduleDao.getCachedObjectByName(module.name());
		for (Config conf : configs) {
			mappedConfigs.add(getConfig(conf, mod));
		}
		return mappedConfigs;
	}
}
