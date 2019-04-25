package com.edafa.web2sms.sms.caching;

import com.edafa.web2sms.sms.enums.SMSResponseStatus;

/**
 *
 * @author loay
 */
public class SMSapiUtils {

    public static boolean checkSMSFailed(SMSResponseStatus smsStatus) {
        return smsStatus == SMSResponseStatus.SYSTEM_FAILURE || smsStatus == SMSResponseStatus.TIMMED_OUT || smsStatus == SMSResponseStatus.CACHE_LIMIT_EXCEEDED;
    }
}
