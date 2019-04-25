package com.edafa.web2sms.utils.logging.interfaces;

import java.util.Map;

import javax.ejb.Local;

import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Local
public interface LoggingManagerLocal extends LoggingManager {

	void resetLoggerFiles();

	void refreshLoggers();

	void updateLoggersConfigs(Map<LoggersEnum, String> newLogLevels);
}
