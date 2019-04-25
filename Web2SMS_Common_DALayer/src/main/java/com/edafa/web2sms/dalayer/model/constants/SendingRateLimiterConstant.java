package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.SendingRateLimiter;

public interface SendingRateLimiterConstant {

	String CLASS_NAME = SendingRateLimiter.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_ALL = PREFIX + "findAll";

	String UPDATE_LIMITER = PREFIX + "updateLimiter";

	String MAX_PERMITS = "maxPermits";

	String SMS_API_ENABLED = "smsapiEnabled";
	String CAMP_ENABLED = "campEnabled";

	String LIMITER_ID = "limiterId";
}
