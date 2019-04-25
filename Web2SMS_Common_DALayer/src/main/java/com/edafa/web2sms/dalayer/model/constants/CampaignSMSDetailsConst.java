/**
 * 
 */
package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.CampaignSMSDetails;

/**
 * @author khalid
 *
 */
public interface CampaignSMSDetailsConst {

	
	String CLASS_NAME = CampaignSMSDetails.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	String FIND_SMS_BY_CAMPAIGN_ID = PREFIX + "findSMSByCampaignId";
	
	String CAMPAIGN_ID = "campaignId";
}
