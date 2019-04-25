package com.edafa.web2sms.utils.configs.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.utils.rate.controller.Limiter;
import com.edafa.utils.rate.controller.exceptions.DuplicateLimiterException;
import com.edafa.utils.rate.controller.exceptions.InvalidParameterException;
import com.edafa.utils.rate.controller.exceptions.NoSuchLimiterException;
import com.edafa.utils.rate.controller.interfaces.RateController;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.exception.FailedToReadConfigsException;
import com.edafa.web2sms.utils.configs.exception.FailedToSaveConfigsException;
import com.edafa.web2sms.utils.configs.exception.InvalidConfigsException;
import com.edafa.web2sms.utils.configs.model.ModuleConfigs;

@Local
public interface ConfigsManagerBeanLocal {

	void registerConfigsListener(ModulesEnum module, ConfigsListener listener);

	void registerConfigsListener(ConfigsListener listener);

	boolean isConfigurationLoaded(ModulesEnum module);

	void refreshModuleConfigs(String module) throws FailedToReadConfigsException, InvalidConfigsException;

	void refreshModuleConfigs(ModulesEnum module) throws FailedToReadConfigsException, InvalidConfigsException;

	void refreshAllModuleConfigs() throws FailedToReadConfigsException, InvalidConfigsException;

	public ModuleConfigs readModuleConfigs(String module) throws FailedToReadConfigsException;

	public ModuleConfigs readModuleConfigs(ModulesEnum module) throws FailedToReadConfigsException;

	public List<ModuleConfigs> readConfigs() throws FailedToReadConfigsException;

	void saveConfigs(ModuleConfigs moduleConfigs) throws FailedToSaveConfigsException, InvalidConfigsException;

	void refreshModuleConfigs(ModuleConfigs moduleConfigs) throws InvalidConfigsException;

    void initSendingRateController(RateController rateController) throws DuplicateLimiterException, InvalidParameterException, NoSuchLimiterException;
        
	void registeredLimiterController(RateController rateController);
	
	List<SendingRateLimiter> readLimiters() throws FailedToReadConfigsException;
	
	void saveLimiter(SendingRateLimiter limiter) throws FailedToSaveConfigsException;
	
	List<SendingRateLimiter> getRunningLimiters() throws FailedToReadConfigsException;
	
	void refreshLimiters(List<SendingRateLimiter> limiters) throws FailedToSaveConfigsException;
	
	void refreshLimiter(SendingRateLimiter limiter) throws FailedToSaveConfigsException;

        void refreshLimiter(String limiterId, int limiterMaxPermits) throws FailedToSaveConfigsException;
	
	void addLimiter(SendingRateLimiter limiter) throws FailedToSaveConfigsException;

	void saveLimiters(List<SendingRateLimiter> limiters)
			throws FailedToSaveConfigsException;

	void deleteLimiter(SendingRateLimiter limiter) throws DBException;

	void RemoveLimiter(SendingRateLimiter limiter) throws FailedToSaveConfigsException;

	SendingRateLimiter createLimiter(SendingRateLimiter limiter)
			throws FailedToSaveConfigsException;
}
