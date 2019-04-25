package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvRequestType", namespace = "http://www.edafa.com/web2sms/prov/enums/")
@XmlEnum
public enum ProvRequestTypeName {
	ACTIVATE_ACCOUNT, UPGRADE_ACCOUNT, DOWNGRADE_ACCOUNT, DEACTIVATE_ACCOUNT, SUSPEND_ACCOUNT, 
//	we have to rename it REACTIVATE_ACCT_AFTER_SUSPENSION
	REACTIVATE_ACCT_AFTER_SUSPENSION, CHANGE_SENDER_NAME, ADD_SENDER_NAME, DELETE_SENDER_NAME,
	USER_CREATE, USER_DELETE,

	@XmlTransient
	UNKNOWN;
}
