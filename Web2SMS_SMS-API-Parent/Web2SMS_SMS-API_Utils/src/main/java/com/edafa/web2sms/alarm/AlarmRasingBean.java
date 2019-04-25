package com.edafa.web2sms.alarm;

import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.jee.apperr.alarm.Alarm;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.XmlHttpClient;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.ClientResponse;
import java.util.Date;
import javax.ejb.EJB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Session Bean implementation class AlarmRasingBean
 */
@Stateless
@LocalBean
public class AlarmRasingBean implements com.edafa.jee.apperr.alarm.AlarmRasingBean {

    @EJB
    XmlHttpClient httpClient;

    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
    Logger smsLogger = LogManager.getLogger(LoggersEnum.SMS_API.name());

    /**
     * Default constructor.
     */
    public AlarmRasingBean() {
        // TODO Auto-generated constructor stub
    }

    @Override
    @Asynchronous
    public void raiseAlarm(Alarm appError) {
        String trxId = TrxId.getTrxId();

        try {
            smsLogger.debug(trxId+"Alarm raising error: " + appError.toString());
            RemoteRaisedAlarm remoteRaisedAlarm = new RemoteRaisedAlarm(trxId, (String)Configs.ALARMING_IDENTIFIER.getValue(), (String)appError.getSource(), appError.getAlarmId(), appError.getSeverity(), appError.getInfo(), new Date());
            smsLogger.info(trxId+"Raising alarm: " + remoteRaisedAlarm);
            String errorsRaisingServiceURI = (String) Configs.ERRORS_RAISING_SERVICE_URI.getValue();
            
            ClientResponse cr = httpClient.sendHttpXmlRequest(errorsRaisingServiceURI, remoteRaisedAlarm, "ERROR");
            if (cr != null) {
                if (cr.getStatus() == 200) {
                    RemoteAlarmResultStatus resultStatus = (RemoteAlarmResultStatus) cr.getEntity(RemoteAlarmResultStatus.class);
                    smsLogger.info(trxId+"Alarm raising response: " + resultStatus);
                } else {
                    smsLogger.error(trxId+"Alarm raising response status: " + cr.getStatus());
                }
            } else {
                smsLogger.error(trxId+"Alarm raising response is null");
            }

        } catch (Exception e) {
            smsLogger.error(trxId+"Unhandled Exception while raising error ");
            appLogger.error(trxId+"Unhandled Exception while raising error ", e);
        }
    }

}
