package com.edafa.web2sms.service.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvResponseStatus", namespace = "http://www.edafa.com/web2sms/prov/enums/")
@XmlEnum
public enum ProvResponseStatus {

	SUCCESS, FAIL, INVALID_REQUEST, DUPLICATE_REQUEST, INVALID_ACCOUNT_STATE, ACCOUNT_ALREADY_ACTIVE, SENDER_NAME_ALREADY_ATTACHED, ACCOUNT_NOT_FOUND, CLOUD_CALL_BACK_FAILED, SR_CREATION_FAILED, INVALID_SENDER
	,USER_NOT_FOUND
	;
}
