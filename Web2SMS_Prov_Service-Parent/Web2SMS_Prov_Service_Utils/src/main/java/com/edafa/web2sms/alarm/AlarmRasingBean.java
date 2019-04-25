package com.edafa.web2sms.alarm;

import com.edafa.jee.alarms.vfeg.AlarmConfiguration;
import com.edafa.jee.alarms.vfeg.AlarmUtils;
import com.edafa.jee.alarms.vfeg.exception.AlarmRaiseLocallyException;
import com.edafa.jee.apperr.AppError;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.jee.apperr.alarm.Alarm;
import com.edafa.jee.apperr.exceptions.AlarmRaiseException;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.dao.interfaces.AlarmRaiseDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.utils.configs.enums.AppSettings;

/**
 * Session Bean implementation class AlarmRasingBean
 */
@Stateless
@LocalBean
public class AlarmRasingBean implements com.edafa.jee.apperr.alarm.AlarmRasingBean {

    Logger logger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());

    @EJB
    AlarmRaiseDaoLocal alarmRaiseDao;

    @Override
    @Asynchronous
    public void raiseAlarm(Alarm appError) {
        String alarmingIdentifier = (String) Configs.ALARMING_IDENTIFIER.getValue();

        try {
            logger.debug(appError.logId() + "Will raise alarm: (" + appError.logDetails() + ")");
            alarmRaiseDao.raiseAlarm(alarmingIdentifier, appError);

            logger.info(appError.logId() + "raised via DB, " + appError.logDetails());
        } catch (DBException e) {
            logger.error(appError.logId() + e.getMessage(), e);
            raiseAlarmLocally(alarmingIdentifier, appError);
        } catch (AlarmRaiseException e) {
            logger.warn(appError.logId() + e.getMessage());
            raiseAlarmLocally(alarmingIdentifier, appError);
        } catch (Exception e) {
            logger.error(appError.logId() + "Cannot be raised", e);
            raiseAlarmLocally(alarmingIdentifier, appError);
        }
    }
    
    
    @Asynchronous
    public void raiseAlarm(AppError appError, String alarmingIdentifier) {
                
        try {
            logger.debug(appError.logId() + "Will raise remote alarm: (" + appError.logDetails() + ")");
            alarmRaiseDao.raiseAlarm(alarmingIdentifier, appError);

            logger.info(appError.logId() + "raised remote alarms via DB, " + appError.logDetails());
        } catch (DBException e) {
            logger.error(appError.logId() + e.getMessage(), e);
            raiseAlarmLocally(alarmingIdentifier, appError);
        } catch (AlarmRaiseException e) {
            logger.warn(appError.logId() + e.getMessage());
            raiseAlarmLocally(alarmingIdentifier, appError);
        } catch (Exception e) {
            logger.error(appError.logId() + "Cannot be raised", e);
            raiseAlarmLocally(alarmingIdentifier, appError);
        }
    }

    private void raiseAlarmLocally(String alarmingIdentifier, Alarm appError) {
        String localFilePath = AppSettings.BaseDir.getEnvEntryValue() + (String) Configs.ALARM_PATH.getValue();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(appError.logId() + "Try to raise alarm via code");
            }
            AlarmConfiguration alarmConfiguration = alarmRaiseDao.getAlarmConfiguration(alarmingIdentifier, appError);
            alarmConfiguration.setLocalFilePath(localFilePath);

            String alarmFileName = AlarmUtils.raiseAlarmLocally(alarmConfiguration);
            logger.info("Could NOT raise alarm via DB, but done via code successfully with name " + alarmFileName);
        } catch (AlarmRaiseLocallyException e) {
            logger.error("Can NOT raise alarm via DB nor code due to :" + e.getMessage());
        } catch (Exception e) {
            logger.error("Can NOT raise alarm via DB nor code due to :" + e.getMessage(), e);
        }
    }

}
