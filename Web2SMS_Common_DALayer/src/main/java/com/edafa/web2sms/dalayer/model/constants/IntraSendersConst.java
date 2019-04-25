package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.IntraSender;

public interface IntraSendersConst {
	String CLASS_NAME = IntraSender.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	
	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String FIND_SYSTEM_SENDER = PREFIX + "findSystemSender";
	String FIND_BY_SENDER_NAME = PREFIX + "findBySenderName";
	String COUNT_BY_SENDER_NAME = PREFIX + "countBySenderName";

	String INTRASENDER_ID = "intraSenderId";
	String SENDER_NAME = "senderName";
	String ACCOUNT_ID = "accountId";
}
