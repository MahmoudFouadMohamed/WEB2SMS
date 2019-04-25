package com.edafa.web2sms.alarm;

import com.edafa.jee.apperr.AppError;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.jee.apperr.ErrorDefinition;
import com.edafa.jee.apperr.Severity;
import com.edafa.jee.apperr.exceptions.InvalidErrorDefinition;
import com.edafa.jee.apperr.monitor.AppErrMonitor;
import com.edafa.jee.apperr.monitor.AppErrorManager;
import com.edafa.web2sms.alarm.AlarmRasingBean;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.dalayer.dao.interfaces.AppErrorDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ErrorDefinitionDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AppErrorEntity;
import com.edafa.web2sms.dalayer.model.ErrorDefinitionEntity;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Session Bean implementation class AppErrorManagerAdapter
 */
@Singleton
@Startup
@LocalBean
@DependsOn({ "LoggingManagerBean", "AppErrorDao", "ConfigsManagerBean" })
public class AppErrorManagerAdapter implements AppErrorManagerLocal, ConfigsListener {

	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	@EJB
	ConfigsManagerBeanLocal configsManagerBean;

	@EJB
	ErrorDefinitionDaoLocal alarmDefinitionDao;

	@EJB
	AppErrorManager alarmHandler;

	@EJB
	AppErrorDaoLocal appErrorDao;

	@EJB
	AlarmRasingBean alarmRasingBean;

	List<ErrorDefinitionEntity> dbAlarmDefinitions;

	/**
	 * Default constructor.
	 */
	public AppErrorManagerAdapter() {
		dbAlarmDefinitions = new ArrayList<ErrorDefinitionEntity>();
	}

	@PostConstruct
	public void init() {
		configsManagerBean.registerConfigsListener(ModulesEnum.AppUtils, this);
		setAppErrorsIds();
		initAlarmHandler();
	}

	public void loadAlarmDefinitions() throws DBException {
		appLogger.debug("Retrieving alarm definitions from database");
		dbAlarmDefinitions = alarmDefinitionDao.findAll();
		appLogger.debug("AppErrorEntity definitions are retrieved from database, found:" + dbAlarmDefinitions.size());
	}

	public boolean initAlarmHandler() {
		if (!configsManagerBean.isConfigurationLoaded(ModulesEnum.AppUtils)) {
			appLogger.warn("Alarms configs is not loadded, cannot initalize AppErrorEntity Handler");
			return false;
		}

		Logger alarmLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
            String alarmPID;
            if (System.getProperty("weblogic.Name").equals("Web2SMS-SMS-API_mServer")) {
                alarmPID = (String) Configs.SMSAPI_ADVANCED_ALARMING_IDENTIFIER.getValue();
            } else if (System.getProperty("weblogic.Name").equals("Web2SMS-APP_Reporting_mServer")) {
                alarmPID = (String) Configs.APP_REPORTING_ALARMING_IDENTIFIER.getValue();
            } else {
                alarmPID = (String) Configs.ALARMING_IDENTIFIER.getValue();
            }

		alarmHandler.init(alarmLogger, alarmPID, alarmRasingBean);

		appLogger.info("Initializing AlarmHandler with alarmIdentifer= " + alarmPID);

		try {
			loadAlarmDefinitions();
		} catch (DBException e) {
			appLogger.error("Failed to load definitions, database exception", e);
			return false;
		}

		try {
			List<ErrorDefinitionEntity> dbAlarmDefinitionsList = dbAlarmDefinitions;
			if (dbAlarmDefinitionsList != null && !dbAlarmDefinitionsList.isEmpty()) {
				registerErrorDefinitions(dbAlarmDefinitionsList);
			}

		} catch (Exception e) {
			appLogger.error("Failed to register error definitions", e);
			return false;
		}
		return true;
	}

	private void setAppErrorsIds() {
		appLogger.info("Set app error IDs");
		for (AppErrors appError : AppErrors.values()) {
			AppErrorEntity appErrorEntity = appErrorDao.getCachedObjectByName(appError.name());
			if (appErrorEntity != null) {
				appError.setId(appErrorEntity.getAppErrorId());
				appLogger.info("App error name: " + appError.name() + ", id=" + appError.getId());
			}
		}
	}

	private void registerErrorDefinitions(List<ErrorDefinitionEntity> dbAlarmDefinitionsList) {
		for (ErrorDefinitionEntity dbAlarmDefinition : dbAlarmDefinitionsList) {
			int alarmId = dbAlarmDefinition.getId().getAppErrorId();
			ErrorDefinition alarmDefinition = new ErrorDefinition();
			alarmDefinition.setErrorId(alarmId);
			alarmDefinition.setDescription(dbAlarmDefinition.getAlarm().getDescription());
			alarmDefinition.setPeriodUnit(TimeUnit.MINUTES);
			alarmDefinition.addSeverity(Severity.getSeverity(dbAlarmDefinition.getId().getSeverity()),
					dbAlarmDefinition.getThreshold(), dbAlarmDefinition.getMonitorPeriod(),
					dbAlarmDefinition.getRaisingPeriod());
			try {
				alarmHandler.registerAppError(alarmDefinition);
				appLogger.info("Registered alarm definition: " + alarmDefinition);
			} catch (InvalidErrorDefinition e) {
				appLogger.info("Failed to registered alarm definition: (" + alarmDefinition + "), Reason: "
						+ e.getMessage());
			}

		}
	}

	@Override
	public boolean refreshAlarmDefinitions() {
		appLogger.debug("Will unregister all existing alarm definitions");
		if (dbAlarmDefinitions != null) {
			for (ErrorDefinitionEntity errorDefinitionEntity : dbAlarmDefinitions) {
				alarmHandler.unregisterAppError(errorDefinitionEntity.getId().getAppErrorId());
			}
		}
		appLogger
				.info("All existing alarm definitions are unregistered from AppErrorEntity Handler, will register the new list");
		return initAlarmHandler();
	}

	@Override
	public void configurationRefreshed() {
		refreshAlarmDefinitions();
	}

	@Override
	public void configurationRefreshed(ModulesEnum module) {
		refreshAlarmDefinitions();
	}

    public List<ErrorDefinitionEntity> getDbAlarmDefinitions() {
        return dbAlarmDefinitions;
    }
    
	@Lock(LockType.READ)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AppErrMonitor reportAppError(AppErrors error, String msg,
			ErrorsSource errorSource) {
		AppError appError = null;
		AppErrMonitor appErrMonitor = null;
		try {
			appError = new AppError(error.getId(), errorSource, msg);
			appErrMonitor = alarmHandler.handleAppError(appError);
		} catch (Exception e) {
			appLogger.error("Failed to raise alarm", e);
		}
		return appErrMonitor;
	}
        
        
}
