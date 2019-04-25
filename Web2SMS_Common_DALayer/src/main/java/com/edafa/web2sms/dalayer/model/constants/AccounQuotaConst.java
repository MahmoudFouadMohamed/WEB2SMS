package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.AccountTier;

public interface AccounQuotaConst {
	String CLASS_NAME = AccountTier.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	
	String findByAccountTiersId = "findByAccountTiersId";
}
