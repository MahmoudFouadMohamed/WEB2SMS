/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.web;

/**
 * REST Web Service
 *
 * @author mahmoud
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.edafa.jee.apperr.AppError;
import com.edafa.jee.apperr.Severity;
import com.edafa.web2sms.alarm.AlarmRasingBean;
import com.edafa.web2sms.alarm.RemoteAlarmResultStatus;
import com.edafa.web2sms.alarm.RemoteRaisedAlarm;
import com.edafa.web2sms.alarm.ErrorDefinitionRemote;
import com.edafa.web2sms.dalayer.model.ErrorDefinitionEntity;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.RemoteAlarmsConfig;
import javax.annotation.PostConstruct;

/**
 *
 * @author mahmoud
 */
@Stateless
@LocalBean
@Path("/alarms")
public class AlarmRasingService {

    @EJB
    AlarmRasingBean alarmRasingBean;

    @EJB
    AppErrorManagerAdapter appErrorManagerAdapter;

    Logger appLogger;

    
    @PostConstruct
    void initService(){
        appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    @Path("/raise_alarm")
    public RemoteAlarmResultStatus raiseRemoteAlarm(RemoteRaisedAlarm remoteAlarm) {

        if (appLogger.isDebugEnabled()) {
            appLogger.debug("Received new RemoteAlarm " + remoteAlarm.toString());
        }

        RemoteAlarmResultStatus returnStatus = RemoteAlarmResultStatus.SUCCESS;

        try {
            AppError appError = null;
            Severity severity = remoteAlarm.getSeverity();
            if (severity == null) {
                appError = new AppError(remoteAlarm.getError(), remoteAlarm.getErrorsource(), remoteAlarm.getMsg(), remoteAlarm.getTimestamp());
            } else {
                appError = new AppError(remoteAlarm.getError(), remoteAlarm.getSeverity(), remoteAlarm.getErrorsource(), remoteAlarm.getMsg(), remoteAlarm.getTimestamp());
            }

            alarmRasingBean.raiseAlarm(appError, remoteAlarm.getAlarmIdentifier());

        } catch (Exception ex) {
            appLogger.error("Unhandled exception cought at AlarmRasingService:raiseRemoteAlarm with request " + remoteAlarm.getTrxId(), ex);
            returnStatus = RemoteAlarmResultStatus.GENERIC_ERROR;
        }

        return returnStatus;
    }

    @POST
    @Produces("application/xml")
    @Path("/alarms_config")
    public RemoteAlarmsConfig remoteAlarmConfigs() {

        if (appLogger.isDebugEnabled()) {
            appLogger.debug("Received new remote alarms configs request");
        }

        RemoteAlarmsConfig remoteAlarmsConfig = new RemoteAlarmsConfig();
        ArrayList<ErrorDefinitionRemote> errorDefinitionRemote = new ArrayList<>();
        try {
            for (ErrorDefinitionEntity errorDefinitionEntity : appErrorManagerAdapter.getDbAlarmDefinitions()) {
                errorDefinitionRemote.add(new ErrorDefinitionRemote(errorDefinitionEntity));
            }
            remoteAlarmsConfig.setErrorsDefinition(errorDefinitionRemote);
        } catch (Exception ex) {
            appLogger.error("Unhandled exception cought at AlarmRasingService:remoteAlarmConfigs ", ex);
        }
        return remoteAlarmsConfig;
    }

}
