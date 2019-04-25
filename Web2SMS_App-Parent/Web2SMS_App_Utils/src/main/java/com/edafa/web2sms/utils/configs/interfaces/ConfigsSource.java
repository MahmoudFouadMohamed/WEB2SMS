package com.edafa.web2sms.utils.configs.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.model.Config;

@Local
public interface ConfigsSource {

	public List<Config> readConfigs(String module) throws FailedToReadConfigsException;

	public void saveConfigs(String module, List<Config> configs) throws FailedToSaveConfigsException;

}
