package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.CampaignExecution;

public interface CampaignExecutionConst {
	String CLASS_NAME = CampaignExecution.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";
	String UPDATE_CAMPAIGN_EXEC_INFO = PREFIX + "updateExecutionInfo";
	String UPDATE_CAMPAIGN_EXEC_STATE = PREFIX + "updateExecutionState";
	String UPDATE_ACTION_BY_ACCT_ID_AND_STATUSES = PREFIX + "updateActionByAccountIdAndCampStatuses";

	String ACCOUNT_ID = "accountId";
	String UPDATE_ACTION = PREFIX + "updateAction";
	String ACTION = "action";
	String START_TIMESTAMP = "startTimestamp";
	String END_TIMESTAMP = "endTimestamp";
	String CAMPAIGN_ID = "campaignId";
	String SUBMITTED_SMS_COUNT = "submittedSmsCount";
	String SUMITTED_SMS_SEG_COUNT = "submittedSmsSegCount";
	String PROCESSING_TIMESTAMP = "processingTimestamp";
	String COMMENTS = "comments";
	String HANDLER_ID = "handlerId";
	String EXECUTION_COUNT = "executionCount";
}
