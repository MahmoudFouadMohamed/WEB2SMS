package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignAction", namespace = "http://www.edafa.com/web2sms/service/model/enums/")
@XmlEnum
public enum CampaignActionName {
	PAUSE, RESUME, APPROVE, SEND, CANCEL, REJECT, HOLD, UN_HOLD, UNKNOWN;
}
