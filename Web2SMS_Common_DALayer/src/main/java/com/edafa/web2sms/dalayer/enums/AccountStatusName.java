package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AccountStatus", namespace = "http://www.edafa.com/web2sms/service/model/enums/")
@XmlEnum
public enum AccountStatusName {

	ACTIVE, SUSPENDED, INACTIVE,

	@XmlTransient
	UNKNOWN;
}
