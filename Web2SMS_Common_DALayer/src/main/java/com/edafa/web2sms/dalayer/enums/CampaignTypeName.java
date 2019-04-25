package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignType", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum CampaignTypeName {

	NORMAL_CAMPAIGN, INTRA_CAMPAIGN, CUSTOMIZED_CAMPAIGN, API_CAMPAIGN,

	// Default
	UNKNOWN

	;

}