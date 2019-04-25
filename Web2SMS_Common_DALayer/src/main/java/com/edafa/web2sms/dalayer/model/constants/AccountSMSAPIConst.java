/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.AccountSMSAPI;

/**
 * 
 * @author khalid
 */
public interface AccountSMSAPIConst {
	String CLASS_NAME = AccountSMSAPI.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String COUNT_BY_ACCOUNT_ID = PREFIX + "CountByAccountId";
	
	String ACCOUNT_ID = "accountId";
	
	String COUNT_ALL_ACCOUNTS = PREFIX+"countAll"	;
	String FIND_ALL = PREFIX+ "findAll";
}
