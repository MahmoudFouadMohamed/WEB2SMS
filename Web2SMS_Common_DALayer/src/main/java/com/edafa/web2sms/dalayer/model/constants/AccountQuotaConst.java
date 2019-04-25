package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.AccountQuota;

public interface AccountQuotaConst {

	  String CLASS_NAME = AccountQuota.class.getSimpleName();
	    String PREFIX = CLASS_NAME + ".";
	    
	    
	    String FIND_BY_ACCT_TIERS_ID = PREFIX + "findByAccountTiersId";
	    String INCREMENT_RESERVED_SMSS = PREFIX + "incrementReservedSmss";
	    String UPDATE_QUOTA = PREFIX + "UpdateQuota";
	    
	    String ACCT_TIERS_ID = "accountTiersId";
	    String ACCOUNT_ID = "accountId";
	    String RESERVED_SMSS = "reservedSmss";
	    String CONSUMED_SMSS = "consumedSmss";
}
