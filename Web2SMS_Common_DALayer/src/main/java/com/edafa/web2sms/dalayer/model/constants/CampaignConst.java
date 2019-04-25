package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Campaign;

public interface CampaignConst {
	String CLASS_NAME = Campaign.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	String FIND_BY_ID = PREFIX + "findByCampaignId";
	String FIND_BY_ID_AND_ACCOUNT_ID = PREFIX + "findByIdAndAccountId";
	String FIND_BY_IDS = PREFIX + "findByCampaignIds";
	String FIND_BY_NAME = PREFIX + "findByName";
	String FIND_BY_STATUSES = PREFIX + "findByStatuses";
	String FIND_BY_ACCOUNT_AND_STATUSES = PREFIX + "findByAccountAndStatuses";
	String COUNT_USER_CAMPAIGNS = PREFIX + "countUserCampaigns";
	String REMOVE_BY_ID = PREFIX + "removeById";
	String FIND_EXECUTABLE_CAMP = PREFIX + "findExecutableCampaigns";
	String FIND_CAMP_SMS_STATS = PREFIX + "findCampSMSStats";
	String FIND_CAMP_FREQUENCY = PREFIX + "findCampFrequency";
	String GET_CAMPAIGN_STATUS = PREFIX + "getCampaignStatus";
	String COUNT_BY_ID_AND_ACCOUNT_ID_AND_STATUS = PREFIX + "countByIdAndAccountIdAndStatus";
	String COUNT_BY_NAME_AND_ACCOUNT_ID_AND_STATUS = PREFIX + "countByNameAndAccountIdAndStatus";
	String UPDATE_CAMPAIGN_EXEC_INFO = PREFIX + "updateCampaignExecutionInfo";
	String UPDATE_CAMPAIGN_STATUS = PREFIX + "updateCampaignStatus";
	String UPDATE_CAMPAIGN_STATUS_BY_ACCT_ID = PREFIX + "updateCampaignStatusByAccountId";

	String NAME = "name";
	String CAMPAIGN_ID = "campaignId";
	String CAMPAIGN_IDS = "campaignIds";
	String ACCOUNT_ID = "accountId";
	String STATUSES = "statuses";
	String ACCOUNT_STATUS = "accountStatus";
	String VALIDITY_PERIOD = "validityPeriod";
	String STATUS = "status";

	// For Execution
	String START_TIMESTAMP = "startTimestamp";
	String END_TIMESTAMP = "endTimestamp";
	String SUBMITTED_SMS_COUNT = "submittedSmsCount";
	String SUMITTED_SMS_SEG_COUNT = "submittedSmsSegCount";
	String PROCESSING_TIMESTAMP = "processingTimestamp";
	String COMMENTS = "comments";
	String HANDLER_ID = "handlerId";
	String ORDER_TYPE = "orderType";

}
