package com.edafa.web2sms.alarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.jee.apperr.AppError;
import com.edafa.jee.apperr.ErrorDefinition;
import com.edafa.jee.apperr.Severity;
import com.edafa.jee.apperr.exceptions.InvalidErrorDefinition;
import com.edafa.jee.apperr.monitor.AppErrMonitor;
import com.edafa.jee.apperr.monitor.AppErrorManager;
import com.edafa.web2sms.utils.XmlHttpClient;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Session Bean implementation class AppErrorManagerAdapter
 */
@Singleton
@Startup
@LocalBean
@DependsOn({"LoggingManagerBean", "ConfigsManagerBean"})
public class AppErrorManagerAdapter implements AppErrorManagerLocal, ConfigsListener {

    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

    @EJB
    ConfigsManagerBeanLocal configsManagerBean;

    @EJB
    AppErrorManager alarmHandler;

    @EJB
    AlarmRasingBean alarmRasingBean;

    @EJB
    XmlHttpClient httpClient;

    List<ErrorDefinitionRemote> dbAlarmDefinitions;
    boolean alarmsLoaded = false;

    /**
     * Default constructor.
     */
    public AppErrorManagerAdapter() {
        dbAlarmDefinitions = new ArrayList<ErrorDefinitionRemote>();
    }

    @PostConstruct
    public void init() {
        configsManagerBean.registerConfigsListener(ModulesEnum.SMSAPIUtils, this);
        boolean configsLoaded = initAlarmHandler();
        this.alarmsLoaded = configsLoaded;
        if (configsLoaded) {
            appLogger.debug("Alarms loaded successfully");
        } else {
            appLogger.error("Error while loading Alarms");
        }
    }

    public boolean checkAlarmsLoading() {
        return alarmsLoaded;
    }

    public boolean loadAlarmDefinitions() {

        appLogger.debug("Retrieving alarm definitions from database");
        boolean loadingStatus = false;
        try {
            String errorsConfigServiceURI = (String) Configs.ERRORS_CONFIG_SERVICE_URI.getValue();
            ClientResponse cr = httpClient.sendHttpXmlRequest(errorsConfigServiceURI, null, "ERROR");
            if (cr != null) {
                if (cr.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
                    RemoteAlarmsConfig remoteConfig = cr.getEntity(RemoteAlarmsConfig.class);
                    if (remoteConfig != null) {
                        dbAlarmDefinitions = remoteConfig.getErrorsDefinition();
                        if (dbAlarmDefinitions != null && !dbAlarmDefinitions.isEmpty()) {
                            appLogger.info("Alarms config: " + Arrays.toString(dbAlarmDefinitions.toArray()));
                            loadingStatus = true;
                        }
                    } else {
                        appLogger.error("Alarms config result response is null");
                    }
                } else {
                    appLogger.error("Alarms config retrieving status " + cr.getStatus());
                }
            } else {
                appLogger.error("Alarms config service calling response is null");
            }
        } catch (Exception e) {
            appLogger.error("Unhandled exception while retrieving alarms config ", e);
        }

        return loadingStatus;
    }

    public boolean initAlarmHandler() {

        boolean initAlarmStatus = false;
        try {
            if (!configsManagerBean.isConfigurationLoaded(ModulesEnum.SMSAPIUtils)) {
                appLogger.warn("Alarms local configs is not loadded, cannot initalize AppErrorEntity Handler");
                return false;
            }

            Logger alarmLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
            String alarmPID = (String) Configs.ALARMING_IDENTIFIER.getValue();

            alarmHandler.init(alarmLogger, alarmPID, alarmRasingBean);

            appLogger.info("Initializing AlarmHandler with alarmIdentifer= " + alarmPID);

            boolean configsLoaded = loadAlarmDefinitions();
            if (configsLoaded) {
                setAppErrorsIds();
                try {
                    List<ErrorDefinitionRemote> dbAlarmDefinitionsList = dbAlarmDefinitions;
                    if (dbAlarmDefinitionsList != null && !dbAlarmDefinitionsList.isEmpty()) {
                        registerErrorDefinitions(dbAlarmDefinitionsList);
                    }
                    initAlarmStatus = true;
                } catch (Exception e) {
                    appLogger.error("Failed to register error definitions", e);
                }
            }
        } catch (Exception e) {
            appLogger.error("Failed to init Alarm Handler", e);
        }
        return initAlarmStatus;
    }

    private void setAppErrorsIds() {
        appLogger.info("Set app error IDs");
        for (AppErrors appError : AppErrors.values()) {
            for (ErrorDefinitionRemote errorDef : dbAlarmDefinitions) {
                if (errorDef.getAlarm().getName().equals(appError.toString())) {
                    appError.setId(errorDef.getAlarm().getAppErrorId());
                    appLogger.info("App error name: " + appError.name() + ", id=" + appError.getId());
                    break;
                }
            }

        }
    }

    private void registerErrorDefinitions(List<ErrorDefinitionRemote> dbAlarmDefinitionsList) {
        for (ErrorDefinitionRemote dbAlarmDefinition : dbAlarmDefinitionsList) {
            int alarmId = dbAlarmDefinition.getId().getAppErrorId();

            for (AppErrors appError : AppErrors.values()) {
                if (alarmId == appError.id) {
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
                        appLogger.error("Failed to registered alarm definition: (" + alarmDefinition + "), Reason: "
                                + e.getMessage());
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean refreshAlarmDefinitions() {
        appLogger.debug("Will unregister all existing alarm definitions");
        if (dbAlarmDefinitions != null) {
            for (ErrorDefinitionRemote errorDefinitionEntity : dbAlarmDefinitions) {
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

    @Lock(LockType.READ)
    public void raiseError(String logId, String trxId, AppErrors error, String msg) {
        try {
            if (((Boolean) Configs.ERRORS_RAISING.getValue())) {
                AppError appError = new AppError(error.getId(), ErrorsSource.WEB_SMSAPI.getSourceName(), msg);
                appLogger.debug(logId + trxId + " | Raising error: " + appError);
                AppErrMonitor appErrMonitor = alarmHandler.handleAppError(appError);

                if (appErrMonitor != null) {
                    appLogger.debug(logId + trxId + " | Error raised successfully");
                } else {
                    synchronized (this) {
                        if (!checkAlarmsLoading()) {
                            appLogger.error(logId + trxId + " | Alarms not loaded");
                            appLogger.info(logId + trxId + " | Loading alarms");
                            if (initAlarmHandler()) {
                                appLogger.info(logId + trxId + " | Alarms loaded successfully");
                                appErrMonitor = alarmHandler.handleAppError(appError);
                                if (appErrMonitor != null) {
                                    appLogger.debug(logId + trxId + " | Error raised successfully");
                                } else {
                                    appLogger.debug(logId + trxId + " | Failed to raise error: " + appError);
                                }
                            } else {
                                appLogger.error(logId + trxId + " | Error while loading Alarms");
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            appLogger.error(logId + trxId + " | Unhandled Exception while raising error ");
            appLogger.error(logId + trxId + " | Unhandled Exception while raising error ", e);
        }
    }

}
