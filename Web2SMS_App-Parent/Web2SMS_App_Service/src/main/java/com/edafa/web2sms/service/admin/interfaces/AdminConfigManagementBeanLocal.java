package com.edafa.web2sms.service.admin.interfaces;

import java.util.List;

import javax.ejb.Local;

//import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.service.admin.exception.ConfigsServiceConnectionException;
import com.edafa.ws.utils.configs.DBException_Exception;
import com.edafa.ws.utils.configs.FailedToReadConfigsException_Exception;
import com.edafa.ws.utils.configs.FailedToSaveConfigsException_Exception;
import com.edafa.ws.utils.configs.InvalidConfigsException_Exception;
import com.edafa.ws.utils.configs.model.ModuleConfigs;
import com.edafa.ws.utils.configs.SendingRateLimiter;


@Local
public interface AdminConfigManagementBeanLocal {
	List<ModuleConfigs> readConfigs() throws FailedToReadConfigsException_Exception;

	ModuleConfigs readModuleConfigs(String module) throws FailedToReadConfigsException_Exception, ConfigsServiceConnectionException;

	void refreshModuleConfigs(String module) throws FailedToReadConfigsException_Exception,
			InvalidConfigsException_Exception, ConfigsServiceConnectionException, FailedToSaveConfigsException_Exception;

	void refreshAllModuleConfigs() throws InvalidConfigsException_Exception, FailedToReadConfigsException_Exception, ConfigsServiceConnectionException;

	void saveConfigs(ModuleConfigs moduleConfigs) throws FailedToSaveConfigsException_Exception,
			InvalidConfigsException_Exception, ConfigsServiceConnectionException;
	
	
	List<SendingRateLimiter> readLimiters() throws FailedToReadConfigsException_Exception;
	
	void saveLimiter(SendingRateLimiter limiter) throws FailedToSaveConfigsException_Exception;
	
	List<SendingRateLimiter> getRunningLimiters() throws FailedToReadConfigsException_Exception;
	
	void refreshLimiters(List<SendingRateLimiter> limiters) throws FailedToSaveConfigsException_Exception;
	
	void refreshLimiter(SendingRateLimiter limiter) throws FailedToSaveConfigsException_Exception;
	
	void addLimiter(SendingRateLimiter limiter) throws FailedToSaveConfigsException_Exception;

	void saveLimiters(List<SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException_Exception;

	void deleteLimiterAndRefresh(SendingRateLimiter limiter) throws DBException_Exception,FailedToSaveConfigsException_Exception;

	void createLimiter(SendingRateLimiter limiter) throws	FailedToSaveConfigsException_Exception;

//	void RemoveLimiter(SendingRateLimiter limiter)
//			throws FailedToSaveConfigsException_Exception;
}
