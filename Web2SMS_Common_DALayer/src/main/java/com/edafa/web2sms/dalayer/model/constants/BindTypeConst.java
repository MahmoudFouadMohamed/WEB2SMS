package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.SMSCBindType;

public interface BindTypeConst {
	String CLASS_NAME = SMSCBindType.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	String FIND_BY_ID = PREFIX + "findById";
	String FIND_BY_TYPE = PREFIX + "findByType";

	String ID = "id";
	String TYPE = "type";
}
