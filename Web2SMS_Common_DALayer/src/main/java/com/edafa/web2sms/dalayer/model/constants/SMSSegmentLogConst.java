package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.SMSSegmentLog;

public interface SMSSegmentLogConst {
	String CLASS_NAME = SMSSegmentLog.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_SMS_LOG_BY_MSG_ID = PREFIX + "findSMSLogByMessageId";
	String FIND_SMS_LOG_BY_MSG_ID_AND_SMSC_ID = PREFIX + 
    "findSMSLogByMessageIdAndSMSCId";	
	String FIND_BY_SMS_ID = PREFIX + "findBySMSId"; 
	
	String UPDATE_DELIVERED_SMS_SEG_INFO = PREFIX
			+ "updateDeliveredSMSSegmentInfo";

	String SMS_ID = "smsId";
	String DELIVERY_DATE = "deliveryDate";
	String MESSAGE_ID = "messageId";
	String IS_DELIVERED = "delivered";
	String SMSC_ID = "smscId";
}
