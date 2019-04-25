/**
 * 
 */
package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.AccountUser;

/**
 * @author khalid
 * 
 */
public interface AccountUserConst {

	String CLASS_NAME = AccountUser.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String COUNT_BY_ACCOUNT_ID = PREFIX + "countByAccountId";
	String FIND_ADMIN_BY_ACCOUNT_ID = PREFIX + "findAdminByAccountId";
	String FIND_BY_ACCOUNT_ID_AND_USERNAME = PREFIX + "findByAccountIdAndUsername";
	String FIND_BY_USERNAME = PREFIX + "findByUsername";

	String ACCOUNT_ID = "accountId";
	String USERNAME = "username";
	String STATUS = "status";
	String STATUSES = "statuses";
	String COMPANY_NAME = "companyName";
	String BILLING_MSISDN = "billingMsisdn";
	String ACCOUNTUSER_ID = "accountUserId";
        String ACTION_NAME = "actionName";

}
