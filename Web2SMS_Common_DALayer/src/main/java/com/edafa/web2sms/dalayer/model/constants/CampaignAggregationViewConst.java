/**
 * 
 */
package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.CampaignAggregationView;

/**
 * @author khalid
 * 
 */
public interface CampaignAggregationViewConst {

	String CLASS_NAME = CampaignAggregationView.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String COUNT_BY_ACCOUNT_ID = PREFIX + "countByAccountId";
	
	String FIND_BY_ACCOUNT_ID_AND_NAME = PREFIX + "findByAccountIdAndName";
	String COUNT_BY_ACCOUNT_ID_AND_NAME = PREFIX + "countByAccountIdAndName";
	
	String FIND_BY_ACCT_ID_NAME_AND_DATE_RANGE_ORDERD = PREFIX + "findByAccountIdNameAndDateRangeOrdered";
	String COUNT_BY_ACCOUNT_ID_NAME_AND_DATES = PREFIX + "countByAccountIdNameAndDates";
	
	String FIND_BY_ACCT_AND_STATUS_AND_DATE_RANGE_ORDERD = PREFIX + "findByAccountIdAndStatusAndDateRangeOrdered";
	String COUNT_BY_ACCOUNT_ID_AND_STATUS_AND_DATES = PREFIX +  "countByAccountIdAndStatusAndDates";
	
	String FIND_BY_ACCT_AND_DATE_RANGE_ORDERD = PREFIX + "findByAccountIdAndDateRangeOrdered";
	String COUNT_BY_ACCOUNT_ID_AND_DATES = PREFIX +  "countByAccountIdAndDates";
	
	String FIND_BY_ACCOUNT_ID_AND_STATUS = PREFIX + "findByAccountIdAndStatus";
	String COUNT_BY_ACCOUNT_ID_AND_STATUS = PREFIX + "countByAccountIdAndStatus";
	
	
	String FIND_BY_CAMPAIGN_ID = PREFIX + "findByCampaignId";
	

	String ACCOUNT_ID = "accountId";
	String TIME_IN_DAYS = "timeInDays";
	String START_TIMESTAMP_FROM = "startTimestampFrom";
	String START_TIMESTAMP_TO = "startTimestampTo";
	String STATUSES = "statuses";
	String CAMP_NAME = "campName";
	String CAMP_ID="campId";
}
