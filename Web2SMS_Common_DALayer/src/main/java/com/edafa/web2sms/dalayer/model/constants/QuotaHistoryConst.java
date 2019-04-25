package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.QuotaHistory;

public interface QuotaHistoryConst {

	String CLASS_NAME = QuotaHistory.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	
	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	
	String ACCOUNT_ID = "accountId";
}
