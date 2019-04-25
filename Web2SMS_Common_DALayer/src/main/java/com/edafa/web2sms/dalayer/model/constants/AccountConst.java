/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Account;

/**
 * 
 * @author yyaseen
 */
public interface AccountConst {
	String CLASS_NAME = Account.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

//	String FIND_BY_ACCOUNT_ADMIN = PREFIX + "findByAccountAdmin";
	String UPDATE_ACCOUNT_STATUS = PREFIX + "updateAccountStatus";
	String FIND_BY_COMPANY_NAME = PREFIX + "findByCompanyName";
	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String FIND_WITH_SMSAPI_BY_ACCOUNT_ID = PREFIX + "findWithSMSAPIByAccountId";
        String FIND_WITH_SMSAPI_CAMP_BY_ACCOUNT_ID = PREFIX + "findWithSMSAPICampByAccountId";
	String FIND_BY_STATUSES = PREFIX + "findByStatus";
	String FIND_BY_MSISDN = PREFIX + "findByBillingMsisdn";
        String FIND_BY_ID_AND_USER_NAME_AND_ACTION = PREFIX + "findByIdAndUserNameAndAction";
        String FIND_WITH_SMSAPI_BY_ID_AND_ACTION = PREFIX + "findWithSMSAPIByIdAndAction";
        String FIND_WITH_SMSAPI_CAMP_BY_ID_AND_ACTION = PREFIX + "findWithSMSAPICampByIdAndAction";


//	String ACCOUNT_ADMIN = "companyAdmin";
	String STATUS = "status";
	String STATUSES = "statuses";
	String ACCOUNT_ID = "accountId";
	String COMPANY_NAME = "companyName";
	String MSISDN = "billingMsisdn";
}
