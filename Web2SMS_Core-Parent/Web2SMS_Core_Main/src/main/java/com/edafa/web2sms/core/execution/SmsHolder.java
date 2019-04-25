package com.edafa.web2sms.core.execution;

import com.edafa.smsgw.dalayer.enums.SMSStatusName;
import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.utils.rate.controller.exceptions.InvalidParameterException;
import com.edafa.utils.rate.controller.exceptions.NoSuchLimiterException;
import com.edafa.utils.rate.controller.interfaces.RateController;
import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.Logger;

/**
 *
 * @author mahmoud
 */
public class SmsHolder {

    protected final Logger logger;
    private SMS sms;
    private Integer requiredPermits;
    private Set<String> rateLimitersIds;
    private RateController smsSendingRateController;
    private final String accountId;    
    private SmsHolderStatus smsHolderStatus;
    private String transLogId;
    protected String logId;

    public SmsHolder(SMS sms, Set<String> rateLimitersIds, Logger logger, String transLogId) {
        this.sms = sms;
        this.requiredPermits = sms.getSegmentsCount();
        this.accountId = sms.getOwnerId();
        this.rateLimitersIds = rateLimitersIds;
        this.logger = logger;
        this.logId = transLogId + sms.logId();
        this.transLogId = transLogId;
    }

    public boolean isSMSAllowedToSend() throws InvalidParameterException, NoSuchLimiterException {
        return checkSmsSendingRateController();
    }

    protected boolean checkSmsSendingRateController() throws InvalidParameterException, NoSuchLimiterException {
        if (smsSendingRateController != null) {
            int passedPermits = 0;
            try {
                passedPermits = smsSendingRateController.acquireMaxAllowedPermits(requiredPermits, rateLimitersIds);
            } catch (NoSuchLimiterException e) {
                if (removeRateLimiter(e.getLimiterId())) {
                    return isSMSAllowedToSend();
                } else {
                    throw e;
                }
            }
            requiredPermits -= passedPermits;
            if (requiredPermits <= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return (requiredPermits <= 0);
        }
    }

    public SMS getSms() {
        return sms;
    }

    public Integer getRequiredPermits() {
        return requiredPermits;
    }

    public Set<String> getRateLimitersIds() {
        return rateLimitersIds;
    }

    public void setSms(SMS sms) {
        this.sms = sms;
        requiredPermits = sms.getSegmentsCount();
    }

    public void decreaseRequiredPermits(int passedPermits) {
        this.requiredPermits -= passedPermits;
    }

    public void setRateLimitersIds(Set<String> rateLimitersIds) {
        this.rateLimitersIds = rateLimitersIds;
    }

    public SMSStatusName getSmsStatus() {
        return sms.getStatus();
    }

    public void setSmsStatus(SMSStatusName status) {
        sms.setStatus(status);
    }

    public void setSmsSendingRateController(RateController smsSendingRateController) {
        this.smsSendingRateController = smsSendingRateController;
    }

    public String getAccountId() {
        return accountId;
    }

    public boolean removeRateLimiter(String rateLimiterId) {
        return rateLimitersIds.remove(rateLimiterId);
    }

    public SmsHolderStatus getSmsHolderStatus() {
        return smsHolderStatus;
    }

    public void setSmsHolderStatus(SmsHolderStatus smsHolderStatus) {
        this.smsHolderStatus = smsHolderStatus;
    }

    public String getLogId() {
        return logId;
    }

    public String getTransLogId() {
        return transLogId;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SmsHolder{sms=");
        str = str.append(sms);
        str = str.append(", requiredPermits=");
        str = str.append(requiredPermits);
        str = str.append(", rateLimitersIds=");
        str = str.append(Arrays.toString(rateLimitersIds.toArray()));
        str = str.append(", accountId=");
        str = str.append(accountId);
        str = str.append("}");

        return str.toString();
    }

}
