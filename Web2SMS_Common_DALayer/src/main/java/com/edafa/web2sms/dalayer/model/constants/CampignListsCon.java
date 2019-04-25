package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.CampaignLists;

public interface CampignListsCon {
	String CLASS_NAME = CampaignLists.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	String FIND_BY_LIST_ID = PREFIX + "findByListId";
	String FIND_LISTS_BY_CAMPAIGN_ID_ORDERED = PREFIX + "findListsByCampaignIdOrdered";
	String FIND_LISTS_BY_CAMPAIGN_ID_ORDERED_WITH_COUNTS = PREFIX + "findListsByCampaignIdOrderedWithCounts";
	String FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_ORDERED = PREFIX + "findListsByCampaignIdAndAcctIdOrdered";
	String FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_ORDERED_WITH_COUNTS = PREFIX
			+ "findListsByCampaignIdAndAcctIdOrderedWithCounts";
	String FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_AND_TYPES_ORDERED = PREFIX
			+ "findListsByCampaignIdAndAcctIdOrderedAndTypes";
	String FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_AND_TYPES_ORDERED_WITH_COUNTS = PREFIX
			+ "findListsByCampaignIdAndAcctIdOrderedAndTypesWithCounts";
	String FIND_BY_CAMPAIGN_ID_ORDERED = PREFIX + "findByCampaignIdOrdered";
	String FIND_LIST_NAME_BY_CAMPAIGN_ID = PREFIX + "findListNameByCampaignId";
	String IS_IN_ACTIVE_CAMPAIGN = PREFIX + "isActiveCampaign";
	String FIND_SUBMITTABLE_BY_CAMP_ID_ORDERED = PREFIX + "findSubmittableByCampIdOrdered";
	String UPDATE_EXECUTION_INFO = PREFIX + "updateExecutionInfo";
	String COUNT_SUBMITTED_SMS_IN_LISTS = PREFIX + "countSubmittedSMSInLists";
	String RESET_SUBMITTED_SMS_IN_LISTS = PREFIX + "resetSubmittedSMSInLists";

	String CAMPAIGN_ID = "campaignId";
	String ACCOUNT_ID = "accountId";
	String LIST_ID = "listId";
	String STATUS_LIST = "statusList";
	String SUBMITTED_SMS_COUNT = "submittedSMSCount";
	String TOTAL_SUBMITTED_SMS_COUNT = "totalSubmittedSMSCount";
	String LIST_TYPES = "listTypes";
	String CAMP_ID_LIST = "campIdList";
}
