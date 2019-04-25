package com.edafa.web2sms.sms.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SMSResponseStatus", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum SMSResponseStatus {
	
	SUBMITTED, FAILED_TO_SUBMITTED, TIMMED_OUT, SYSTEM_FAILURE, CACHE_LIMIT_EXCEEDED
	;
}
