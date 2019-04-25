package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.dalayer.model.SMSStatus;
import com.edafa.web2sms.dalayer.pojo.AggCDR;
import com.edafa.web2sms.dalayer.pojo.MonthQuota;

/**
 * 
 * @author akhalifah
 */

@Local
public interface SMSLogDaoLocal {

	void create(SMSLog sMSLog) throws DBException;

	void edit(SMSLog sMSLog) throws DBException;

	void remove(SMSLog sMSLog) throws DBException;

	SMSLog find(Object id) throws DBException;

	List<SMSLog> findAll() throws DBException;

	List<SMSLog> findRange(int[] range) throws DBException;

	long findMaxSMSId() throws DBException;

	int count() throws DBException;

	void create(List<SMSLog> smsLogs) throws DBException;

	void updateSentSMSInfo(Long smsId, Date sendDate, Integer sentSegCount, String smscMsgId, String comments)
			throws DBException;

	void updateSentSMSInfoWithStatus(Long smsId, SMSStatusName statusName, Date sendDate, Integer sentSegCount,
			String smscMsgId, String comments) throws DBException;

	void updateDeliveredSMSInfo(Long smsId, Date deliveryDate, Integer deliveredSegCount) throws DBException;

	void updateDeliveredSMSInfoWithStatus(Long smsId, SMSStatusName statusName, Date deliveryDate,
			Integer deliveredSegCount) throws DBException;

	int updateSentSMSInfoWithStatus(List<SMSLog> smsList) throws DBException;

	int updateDeliveredSMSInfoWithStatus(List<SMSLog> logs) throws DBException;

	int updateDeliveredSMSInfo(List<SMSLog> logs) throws DBException;

	List<SMSLog> findByCampaignId(String campaignId, int index, int count) throws DBException;

	int countCampaignLogs(String campaignId) throws DBException;

	int countAccountSMSLogs(String accountId, Date startTimestamp, Date endTimestamp) throws DBException;

	List<SMSLog> findSMSLogsByAccount(String accountId, Date startTimestamp, Date endTimestamp, int index, int count)
			throws DBException;

	int countSMSLogs(Date startTimestamp, Date endTimestamp) throws DBException;

	List<SMSLog> findSMSLogs(Date startTimestamp, Date endTimestamp, int index, int count) throws DBException;

	int countAggCDRs(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes, List<String> mobNetPrefixes,
			List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr, String mobNetStr,
			String etiNetStr, String masrNetStr, List<String> customAccountCDRs, List<SMSStatus> smsStatuses)
			throws DBException;

	List<AggCDR> findAggCDRs(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, List<String> customAccountCDRs,
			List<SMSStatus> smsStatuses, int first, int count) throws DBException;

	int countAggCDRsByAccountId(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, String accountId, List<SMSStatus> smsStatuses)
			throws DBException;

	List<AggCDR> findAggCDRsByAccountId(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, String accountId, List<SMSStatus> smsStatuses,
			int startIndex, int cdrChunkSize) throws DBException;

	List<MonthQuota> findQuotaHistory(String accountId, String dateFrom, String dateTo) throws DBException;

	List<SMSLog> findByCampaignIdAndStatus(String campaignId, List<SMSStatus> status) throws DBException;

	List<Contact> findContactsByCampaignIdAndStatus(String campaignId, List<SMSStatus> status) throws DBException;

	List<SMSLog> findSMSwithinDates(String accountId, String msisdn, Date startDate, Date endDate, int index, int count)
			throws DBException;

	List<SMSLog> findSMSwithinDates(Date startDate, Date endDate, int index, int count) throws DBException;

	int countSmsApiLogs(String accountId, String senderName, Date from, Date to);
	List<SMSLog> getSmsApiLogs(String accountId, String senderName, Date from, Date to, int start, int max);

	public List<SMSLog> findInIdList(List<String> smsIdList) throws DBException;
	public List<SMSLog> findSMSwithinDates(String accountId, Date startDate, Date endDate) throws DBException;
}
