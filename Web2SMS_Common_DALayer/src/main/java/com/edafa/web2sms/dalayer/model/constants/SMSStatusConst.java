package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.SMSStatus;

/**
 * @author tmohamed
 *
 */
public interface SMSStatusConst
{
	String CLASS_NAME = SMSStatus.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	String FIND_BY_ID = PREFIX + "findById";
	String FIND_BY_STATUS_NAME = PREFIX + "findByStatusName";

	String ID = "id";
	String STATUS_NAME = "statusName";
}// end of interface SMSStatusConst
