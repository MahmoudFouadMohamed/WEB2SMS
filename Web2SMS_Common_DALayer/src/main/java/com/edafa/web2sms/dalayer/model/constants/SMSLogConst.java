package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.SMSLog;

/**
 * @author tmohamed
 * 
 */
public interface SMSLogConst {
	String CLASS_NAME = SMSLog.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String UPDATE_SENT_SMS_INFO = PREFIX + "updateSentSMSInfo";
	String UPDATE_SENT_SMS_INFO_WITH_STATUS = PREFIX + "updateSentSMSInfoWithStatus";

	String UPDATE_DELIVERED_SMS_INFO = PREFIX + "updateDeliveredSMSInfo";
	String UPDATE_DELIVERED_SMS_INFO_WITH_STATUS = PREFIX + "updateDeliveredSMSInfoWithStatus";
	String FIND_BY_SMSC_MSG_ID = PREFIX + "findBySmscMessageId";
	String FIND_MAX_SMS_ID = PREFIX + "findMaxSMSId";
	String FIND_BY_JOB_ID = PREFIX + "findByJobId";
	String FIND_BY_USER_AND_JOB_ID = PREFIX + "findByUserAndJobId";
	String FIND_BY_USER_AND_JOB_ID_COUNT = PREFIX + "findByUserAndJobIdCount";
	String FIND_BY_CAMPAIGN_ID = PREFIX + "findByCampaignId";
	String FIND_DETAILED_BY_CAMPAIGN_ID = PREFIX + "findDetailedByCampaignId";
	String FIND_BY_JOB_ID_COUNT = PREFIX + "findByJobIdCount";
	String FIND_AGG_CDR = PREFIX + "findAggregationCDR";
	String FIND_BY_ACCOUNT_ID_AND_PERIOD = PREFIX + "findByAccountIdAndPeriod";
	String FIND_BY_PERIOD = PREFIX + "findLogsByPeriod";
	String COUNT_AGG_CDR = PREFIX + "countAggregationCDR";
	String COUNT_CAMPAIGN_LOGS = PREFIX + "countCampaignLogs";
	String COUNT_ACCOUNT_LOGS_BY_PERIOD = PREFIX + "countAccountLogsByPeriod";
	String COUNT_LOGS_BY_PERIOD = PREFIX + "countLogsByPeriod";
	String FIND_QUOTA_HISTORY= PREFIX + "findQuotaHistory";
	String FIND_BY_CAMPAIGN_ID_AND_STATUS = PREFIX + "findByCampIdAndStatus";
	String FIND_CONTACT_BY_CAMPAIGN_ID_AND_STATUS = PREFIX + "findContactByCampIdAndStatus";
	String FIND_BY_MSISDN_AND_DATES = PREFIX + "findByMSISDNandDates";
	String FIND_BY_DATES = PREFIX + "findByDates";
	String FIND_SMSLOG_BY_IDLIST = PREFIX + ".findSMSbySmsIdList";
	String FIND_ACCOUNT_ALL_SMSLOG = PREFIX + ".findAccountAllSms";

	String SMS_ID = "smsId";
	String STATUS = "status";
	String STATUS_LIST = "statusList";
	String SEND_RECEIVE_DATE = "sendReceiveDate";
	String DELIVERY_DATE = "deliveryDate";
	String SENT_SEGMENT_COUNT = "sentSegCount";
	String DELIVERED_SEGMENT_COUNT = "deliveredSegCount";
	String SMSC_MESSAGE_ID = "messageId";
	String JOB_ID = "jobId";
	String USER = "user";
	String COMMENTS = "comments";
	String CAMPAIGN_ID = "campaignId";
	String ACCOUNT_ID = "accountId";
	String START_DATE = "startDate";
	String END_DATE = "endDate";
	String MSISDN = "receiver";
	
	
	String SMSAPIVIEW = "SMSAPIView"; 
	String FIND_ACCOUNT_ALL_SMS=  SMSAPIVIEW +".findAccountAllSms";
	String ACCOUNT = "account";
	String DELIVERED = "deliverd";
	String UNDELIVERED = "undeliverd"; 
	String FIND_SMS_BY_IDLIST = SMSAPIVIEW + ".findSMSbySmsIdList";
	String SMS_ID_LIST = "smsIdList";
	
	String FIND_BY_ACCOUNT_AND_MSISDN_ALL_SMS = SMSAPIVIEW + ".findByAccountAndMSISDNAllSms";
}// end of interface SMSLogConst
