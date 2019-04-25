package com.edafa.web2sms.sms.interfaces;

import com.edafa.web2sms.sms.enums.ResultStatus;
import java.util.List;

import javax.ejb.Remote;

import com.edafa.web2sms.sms.enums.SMSResponseStatus;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.sms.model.SMSSubmitState;

@Remote
public interface SubmitSMSBeanRemote {
    public SMSResponseStatus submitSMS(String logId, SMSDetails SMSDeatail) throws Exception;
    public List<SMSSubmitState> submitSMS(String logId, List<SMSDetails> SMSDeatailsList) throws Exception;

    public ResultStatus logExpiredSMS(String logId, List<SMSDetails> smsDeatailsList);

}
