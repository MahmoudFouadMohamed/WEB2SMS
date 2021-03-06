package com.edafa.web2sms.service.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ResponseStatus", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum ResponseStatus {

	SUCCESS,
	FAIL,
	INVALID_REQUEST,
	INELIGIBLE_ACCOUNT,
	DUPLICATE_LIST_NAME,
	INVALED_FILE,
	INVALED_FILE_TYPE,
	LIST_NOT_FOUND,
	LOCKED_LIST,
	LIST_UPDATE_FAIL,
	CONTACTS_NOT_FOUND,
	DUPLICATE_CONTACT,
	PARTIAL_UPDATE,
	FULLY_UPDATED,
	INVALID_CAMPAIGN,
	INVALID_CAMPAIGN_STATE,
	CAMPAIGN_NOT_FOUND,
	CAMPAIGN_REPORT_NOT_FOUND,
	NO_LOGS_FOR_CAMPAIGN,
	INVALID_CAMPAIGN_LIST,
	ACCOUNT_QUOTA_EXCEEDED,
	ACCOUNT_QUOTA_NOT_FOUND,
	NO_LOGS_FOUND,
	DB_ERROR,
	TEMPLATES_NOT_FOUND;

}
