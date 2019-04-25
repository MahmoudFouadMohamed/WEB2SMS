package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.SMSCConfig;

public interface SMSCConfigConst {
	String CLASS_NAME = SMSCConfig.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_ALL = PREFIX + "findAll";
	String FIND_ALL_COUNT = PREFIX + "findAllCount";
	String FIND_BY_SMSC_ID = PREFIX + "findBySmscId";
	String FIND_BY_SESSION_ID = PREFIX + "findBySessionId";
	String FIND_BY_SERVICE_TYPE = PREFIX + "findByServiceType";
	String FIND_BY_ADDRESS = PREFIX + "findByAddress";
	String FIND_BY_PROT = PREFIX + "findByPort";
	String FIND_ACTIVE = PREFIX + "findActive";

	String SMSC_ID = "smscId";
	String SERVICE_TYPE = "serviceType";
	String ADDRESS = "address";
	String PORT = "port";
}
