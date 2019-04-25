package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.CampaignStatus;

public interface CampaignStatusConst {
	String CLASS_NAME = CampaignStatus.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	String FIND_BY_STATUS_NAME = PREFIX + "findByCampaignStatusName";

	String STATUS_NAME = "campaignStatusName";
}
