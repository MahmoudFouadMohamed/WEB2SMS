/**
 * 
 */
package com.edafa.web2sms.dalayer.model.constants;


import com.edafa.web2sms.dalayer.model.AccountTier;

/**
 * @author khalid
 *
 */
public interface AccountTierConst {
	String CLASS_NAME = AccountTier.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	
	String FIND_BY_MSISDN = PREFIX + "findByBillingMsisdn";
	String FIND_BY_ACCT_ID = PREFIX + "findByAccountId";
	
	String MSISDN = "billingMsisdn";
	String ACCOUNT_ID = "accountId";



}
