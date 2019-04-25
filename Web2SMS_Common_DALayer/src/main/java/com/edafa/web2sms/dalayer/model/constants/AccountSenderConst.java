package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.AccountSender;

public interface AccountSenderConst {
	String CLASS_NAME = AccountSender.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String COUNT_BY_ACCOUNT_ID = PREFIX + "countByAccountId";
//	String COUNT_BY_SENDER_NAME = PREFIX + "countBySenderName";
	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
//	String FIND_BY_SENDER_NAME = PREFIX + "findBySenderName";
	String REMOVE_ALL_BY_ACCT_ID = PREFIX + "removeAllByAccountId";
	String FIND_BY_ACCOUNT_ID_AND_SENDER_NAME = PREFIX + "findByAccountIdAndSenderName";
	
	String ACCOUNT_ID = "accountId";
	String SENDER_NAME = "senderName";
}
