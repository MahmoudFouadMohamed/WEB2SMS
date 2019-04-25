package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvReqStatus", namespace = "http://www.edafa.com/web2sms/service/model/enums/")
@XmlEnum
public enum ProvReqStatusName {
	PENDING, SUCCESS, FAIL, CANCELLED,
	
	@XmlTransient
	UNKNOWN;
}
