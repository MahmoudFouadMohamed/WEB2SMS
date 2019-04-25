package com.edafa.web2sms.service.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://www.edafa.com/web2sms/service/model/enums/")
@XmlEnum
public enum CampaignValidationStatus {
	DUPLICAT_CAMPAIGN_NAME, INVALID_SMS_TEXT, INVALID_SCHEDULING, INVALID_SENDER_NAME, NO_CONTACT_LISTS, INVALID_CAMPAIN_LANG;
}
