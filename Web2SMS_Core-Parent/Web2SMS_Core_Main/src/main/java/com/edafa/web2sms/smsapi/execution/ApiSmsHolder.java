package com.edafa.web2sms.smsapi.execution;

import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.utils.rate.controller.exceptions.InvalidParameterException;
import com.edafa.utils.rate.controller.exceptions.NoSuchLimiterException;
import com.edafa.web2sms.core.execution.SmsHolder;
import java.util.Set;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mahmoud
 */
public class ApiSmsHolder extends SmsHolder {

    protected SmsApiAccountInfo smsApiAccountInfo;
    protected boolean cachedSms = false;

    public ApiSmsHolder(SMS sms, Set<String> rateLimitersIds, Logger logger, String transLogId, boolean cachedSms, SmsApiAccountInfo smsApiAccountInfo) {
        super(sms, rateLimitersIds, logger, transLogId);
        this.smsApiAccountInfo = smsApiAccountInfo;
        this.cachedSms = cachedSms;
    }

    @Override
    public boolean isSMSAllowedToSend() throws InvalidParameterException, NoSuchLimiterException {

        if (!smsApiAccountInfo.isAccountAllowedToSend()) {
            if (logger.isTraceEnabled()) {
                logger.trace(logId + "Not allowed to send now");
            }
            return false;
        }

        if (cachedSms && smsApiAccountInfo.getAccountConcurrentSmsCounter() > 0) {
            if (logger.isDebugEnabled()) {
                logger.info(logId + "cached SMS, Not allowed to send now, still have live SMSs, count=" + smsApiAccountInfo.getAccountConcurrentSmsCounter());
            }
            return false;
        }

        boolean allowResult = checkSmsSendingRateController();
        if (allowResult) {
            if (!cachedSms) {
                smsApiAccountInfo.decrementAccountConcurrentSmsCounter();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.info(logId + "Not allowed to send now");
            }
            smsApiAccountInfo.setAccountAllowedToSend(allowResult);
        }

        return allowResult;
    }

    public SmsApiAccountInfo getSmsApiAccountInfo() {
        return smsApiAccountInfo;
    }

    public boolean isCachedSms() {
        return cachedSms;
    }

    @Override
    public String toString() {
        return "ApiSmsHolder{" + super.toString() + "smsApiAccountInfo=" + smsApiAccountInfo + ", cachedSms=" + cachedSms + '}';
    }

}
