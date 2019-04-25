package com.edafa.web2sms.dalayer.dao;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSLogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSSegmentLogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.dalayer.model.SMSSegmentLog;
import com.edafa.web2sms.dalayer.model.SMSStatus;
import com.edafa.web2sms.dalayer.model.constants.SMSLogConst;
import com.edafa.web2sms.dalayer.pojo.AggCDR;
import com.edafa.web2sms.dalayer.pojo.MonthQuota;

/**
 *
 * @author akhalifah, mfouad
 */
@Stateless
public class SMSLogDao extends AbstractDao<SMSLog> implements SMSLogDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@EJB
	SMSStatusDaoLocal smsStatusDao;

	@EJB
	SMSSegmentLogDaoLocal smsSegmentLogDao;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SMSLogDao() {
		super(SMSLog.class);
	}

	@Override
	public SMSLog find(Object id) throws DBException {
		SMSLog smsLog = super.find(id);
		if (smsLog != null) {
			List<SMSSegmentLog> segmentsLogs = smsSegmentLogDao.findBySMSId(smsLog.getSmsId());
			smsLog.setSmsSegmetsLogList(segmentsLogs);
		}
		return smsLog;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void create(List<SMSLog> smsLogs) throws DBException {
		try {
			for (SMSLog smsLog : smsLogs) {
				create(smsLog);
			}// end for

		} catch (Exception e) {
			throw new DBException(e);
		}
	}// end of method create

	@Override
	public void updateSentSMSInfo(Long smsId, Date sendDate, Integer sentSegCount, String smscMsgId, String comments)
			throws DBException {
		try {
			int noOfAffectedRows = em.createNamedQuery(SMSLog.UPDATE_SENT_SMS_INFO, SMSLog.class)
					.setParameter(SMSLog.SMS_ID, smsId)
					.setParameter(SMSLog.SEND_RECEIVE_DATE, sendDate, TemporalType.TIMESTAMP)
					.setParameter(SMSLog.SENT_SEGMENT_COUNT, sentSegCount)
					.setParameter(SMSLog.SMSC_MESSAGE_ID, smscMsgId).setParameter(SMSLog.COMMENTS, comments)
					.executeUpdate();
		}// end try
		catch (Exception e) {
			throw new DBException(e);
		}// end catch
	}// end of method updateSentSMSInfo

	@Override
	public void updateSentSMSInfoWithStatus(Long smsId, SMSStatusName statusName, Date sendDate, Integer sentSegCount,
			String smscMsgId, String comments) throws DBException {
		try {
			SMSStatus smsStatus = smsStatusDao.findByStatusName(statusName);

			int noOfAffectedRows = em.createNamedQuery(SMSLog.UPDATE_SENT_SMS_INFO_WITH_STATUS, SMSLog.class)
					.setParameter(SMSLog.SMS_ID, smsId).setParameter(SMSLog.STATUS, smsStatus)
					.setParameter(SMSLog.SEND_RECEIVE_DATE, sendDate, TemporalType.TIMESTAMP)
					.setParameter(SMSLog.SENT_SEGMENT_COUNT, sentSegCount)
					.setParameter(SMSLog.SMSC_MESSAGE_ID, smscMsgId).setParameter(SMSLog.COMMENTS, comments)
					.executeUpdate();
		}// end try
		catch (Exception e) {
			throw new DBException(e);
		}// end catch
	}// end of method updateSentSMSInfoWithStatus

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int updateSentSMSInfoWithStatus(List<SMSLog> smsList) throws DBException {
		int noOfAffectedRows = 0;
		// try {
		// for (SMS sms : smsList) {
		// SMSStatus smsStatus = smsStatusDao.findByStatusName(sms
		// .getStatus());
		//
		// noOfAffectedRows += em
		// .createNamedQuery(
		// SMSLog.UPDATE_SENT_SMS_INFO_WITH_STATUS,
		// SMSLog.class)
		// .setParameter(SMSLog.SMS_ID, sms.getSmsId().getId())
		// .setParameter(SMSLog.STATUS, smsStatus)
		// .setParameter(SMSLog.SEND_DATE, sms.getSendDate(),
		// TemporalType.TIMESTAMP)
		// .setParameter(SMSLog.SENT_SEGMENT_COUNT,
		// sms.getSentSegCount())
		// .setParameter(SMSLog.SMSC_MESSAGE_ID,
		// sms.getSmscMessageId())
		// .setParameter(SMSLog.COMMENTS, sms.getComment())
		// .executeUpdate();
		// }
		// } catch (Exception e) {
		// throw new DBException(e);
		// }
		return noOfAffectedRows;
	}

	@Override
	public long findMaxSMSId() throws DBException {
		try {
			List<Long> list = em.createNamedQuery(SMSLog.FIND_MAX_SMS_ID, Long.class).getResultList();

			return (list.size() > 0 && list.get(0) != null) ? list.get(0) : 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<MonthQuota> findQuotaHistory(String accountId, String dateFrom, String dateTo) throws DBException {
		try {

			@SuppressWarnings("unchecked")
			List<Object[]> records = em.createNamedQuery(SMSLog.FIND_QUOTA_HISTORY).setParameter(1, accountId)
					.setParameter(2, dateFrom).setParameter(3, dateTo).getResultList();
			List<MonthQuota> result = new LinkedList<>();
			for (Object[] objects : records) {
				String month = (String) objects[0];
				BigDecimal count = (BigDecimal) objects[1];
				MonthQuota record = new MonthQuota(month, count.longValue());
				result.add(record);
			}
			return result;

		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public void updateDeliveredSMSInfo(Long smsId, Date deliveryDate, Integer deliveredSegCount) throws DBException {
		try {
			int noOfAffectedRows = em.createNamedQuery(SMSLog.UPDATE_DELIVERED_SMS_INFO, SMSLog.class)
					.setParameter(SMSLog.SMS_ID, smsId)
					.setParameter(SMSLog.DELIVERY_DATE, deliveryDate, TemporalType.TIMESTAMP)
					.setParameter(SMSLog.DELIVERED_SEGMENT_COUNT, deliveredSegCount).executeUpdate();
		}// end try
		catch (Exception e) {
			throw new DBException(e);
		}// end catch
	}// end of method updateDeliveredSMSInfo

	@Override
	public void updateDeliveredSMSInfoWithStatus(Long smsId, SMSStatusName statusName, Date deliveryDate,
			Integer deliveredSegCount) throws DBException {
		try {
			SMSStatus smsStatus = smsStatusDao.findByStatusName(statusName);

			int noOfAffectedRows = em.createNamedQuery(SMSLog.UPDATE_DELIVERED_SMS_INFO_WITH_STATUS, SMSLog.class)
					.setParameter(SMSLog.SMS_ID, smsId).setParameter(SMSLog.STATUS, smsStatus)
					.setParameter(SMSLog.DELIVERY_DATE, deliveryDate, TemporalType.TIMESTAMP)
					.setParameter(SMSLog.DELIVERED_SEGMENT_COUNT, deliveredSegCount).executeUpdate();
		}// end try
		catch (Exception e) {
			throw new DBException(e);
		}// end catch
	}// end of method updateDeliveredSMSInfoWithStatus

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int updateDeliveredSMSInfoWithStatus(List<SMSLog> logs) throws DBException {
		int noOfAffectedRows = 0;
		for (SMSLog smsLog : logs) {
			try {
				noOfAffectedRows += em.createNamedQuery(SMSLog.UPDATE_DELIVERED_SMS_INFO_WITH_STATUS, SMSLog.class)
						.setParameter(SMSLog.SMS_ID, smsLog.getSmsId())
						.setParameter(SMSLog.STATUS, smsLog.getSMSStatus()).executeUpdate();
			}// end try
			catch (Exception e) {
				throw new DBException(e);
			}// end catch
		}
		return noOfAffectedRows;

	}// end of method updateDeliveredSMSInfoWithStatus

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int updateDeliveredSMSInfo(List<SMSLog> logs) throws DBException {
		int noOfAffectedRows = 0;
		for (SMSLog smsLog : logs) {
			try {
				noOfAffectedRows = em.createNamedQuery(SMSLog.UPDATE_DELIVERED_SMS_INFO, SMSLog.class)
						.setParameter(SMSLog.SMS_ID, smsLog.getSmsId())
						.setParameter(SMSLog.STATUS, smsLog.getSMSStatus()).executeUpdate();
				// try {
				if (smsLog.getSmsSegmetsLogList() != null) {
					smsSegmentLogDao.updateDeliveredSMSSementsInfo(smsLog.getSmsSegmetsLogList());
				}
				// } catch (DBException dbe) {
				// throw dbe;
				// }

			}// end try
			catch (Exception e) {
				throw new DBException(e);
			}// end catch
		}
		return noOfAffectedRows;

	}

	@Override
	public List<AggCDR> findAggCDRs(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, List<String> customAccountCDRs,
			List<SMSStatus> smsStatuses, int first, int count) throws DBException {
		List<AggCDR> resultSet = new ArrayList<>();
		String sql = getAggCDRQuery(onNetPrefixes, onNetStr, mobNetPrefixes, etiNetPrefixes, masrNetPrefixes, mobNetStr,
				etiNetStr, masrNetStr, customAccountCDRs, smsStatuses);
		Query q = em.createNativeQuery(sql).setParameter(1, startTimestamp, TemporalType.TIMESTAMP)
				.setParameter(2, endTimestamp, TemporalType.TIMESTAMP).setFirstResult(first).setMaxResults(count);

		List<Object[]> resultList = q.getResultList();

		em.flush();
		em.clear();

		for (Object[] objects : resultList) {
			resultSet.add(new AggCDR((String) objects[0] // billingMsisdn
					, (String) objects[1] // companyId
					, (String) objects[2] // sender
					, ((BigDecimal) objects[3]).longValue() // aggSMSSegCount
					, (String) objects[4] // msisdnType)
			));
		}

		return resultSet;
	}

	@Override
	public List<AggCDR> findAggCDRsByAccountId(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, String accountId, List<SMSStatus> smsStatuses,
			int first, int count) throws DBException {
		List<AggCDR> resultSet = new ArrayList<>();
		String sql = getAggCDRQueryByAccountId(onNetPrefixes, onNetStr, mobNetPrefixes, etiNetPrefixes, masrNetPrefixes,
				mobNetStr, etiNetStr, masrNetStr, accountId, smsStatuses);
		Query q = em.createNativeQuery(sql).setParameter(1, startTimestamp, TemporalType.TIMESTAMP)
				.setParameter(2, endTimestamp, TemporalType.TIMESTAMP).setFirstResult(first).setMaxResults(count);

		List<Object[]> resultList = q.getResultList();

		em.flush();
		em.clear();

		for (Object[] objects : resultList) {
			resultSet.add(new AggCDR((String) objects[0] // billingMsisdn
					, (String) objects[1] // companyId
					, (String) objects[2] // sender
					, ((BigDecimal) objects[3]).longValue() // aggSMSSegCount
					, (String) objects[4] // msisdnType)
			));
		}

		return resultSet;
	}

	@Override
	public int countAggCDRs(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, List<String> customAccountCDRs,
			List<SMSStatus> smsStatuses) throws DBException {
		try {

			String sql = "SELECT COUNT(*) FROM (" + getAggCDRQuery(onNetPrefixes, onNetStr, mobNetPrefixes,
					etiNetPrefixes, masrNetPrefixes, mobNetStr, etiNetStr, masrNetStr, customAccountCDRs, smsStatuses)
					+ ")";

			BigDecimal count = (BigDecimal) em.createNativeQuery(sql)
					.setParameter(1, startTimestamp, TemporalType.TIMESTAMP)
					.setParameter(2, endTimestamp, TemporalType.TIMESTAMP).getSingleResult();
			// return (int) count;
			if (count != null) {
				return count.intValue();
			}
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public int countAggCDRsByAccountId(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, String accountId, List<SMSStatus> smsStatuses)
			throws DBException {
		try {

			String sql = "SELECT COUNT(*) FROM (" + getAggCDRQueryByAccountId(onNetPrefixes, onNetStr, mobNetPrefixes,
					etiNetPrefixes, masrNetPrefixes, mobNetStr, etiNetStr, masrNetStr, accountId, smsStatuses) + ")";

			BigDecimal count = (BigDecimal) em.createNativeQuery(sql)
					.setParameter(1, startTimestamp, TemporalType.TIMESTAMP)
					.setParameter(2, endTimestamp, TemporalType.TIMESTAMP).getSingleResult();
			// return (int) count;
			if (count != null) {
				return count.intValue();
			}
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public int countCampaignLogs(String campaignId) throws DBException {
		try {
			Long count = em.createNamedQuery(SMSLog.COUNT_CAMPAIGN_LOGS, Long.class)
					.setParameter(SMSLog.CAMPAIGN_ID, campaignId).getSingleResult();
			if (count != null) {
				return count.intValue();
			}

			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSLog> findByCampaignId(String campaignId, int index, int count) throws DBException {
		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLog.FIND_DETAILED_BY_CAMPAIGN_ID, SMSLog.class)
					.setParameter(SMSLog.CAMPAIGN_ID, campaignId).setFirstResult(index).setMaxResults(count);

			List<SMSLog> logs = q.getResultList();
			em.flush();
			em.clear();

			return logs;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSLog> findByCampaignIdAndStatus(String campaignId, List<SMSStatus> status) throws DBException {
		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLog.FIND_BY_CAMPAIGN_ID_AND_STATUS, SMSLog.class)
					.setParameter(SMSLog.CAMPAIGN_ID, campaignId).setParameter(SMSLog.STATUS_LIST, status);

			List<SMSLog> logs = q.getResultList();
			em.flush();
			em.clear();
			return logs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

	@Override
	public List<Contact> findContactsByCampaignIdAndStatus(String campaignId, List<SMSStatus> status)
			throws DBException {
		try {
			TypedQuery<Contact> q = em.createNamedQuery(SMSLog.FIND_CONTACT_BY_CAMPAIGN_ID_AND_STATUS, Contact.class)
					.setParameter(SMSLog.CAMPAIGN_ID, campaignId).setParameter(SMSLog.STATUS_LIST, status);

			List<Contact> logs = q.getResultList();
			em.flush();
			em.clear();
			return logs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

	@Override
	public int countAccountSMSLogs(String accountId, Date startTimestamp, Date endTimestamp) throws DBException {
		try {
			Long count = em.createNamedQuery(SMSLog.COUNT_ACCOUNT_LOGS_BY_PERIOD, Long.class)
					.setParameter(SMSLog.ACCOUNT_ID, accountId).setParameter(SMSLog.START_DATE, startTimestamp)
					.setParameter(SMSLog.END_DATE, endTimestamp).getSingleResult();

			if (count != null) {
				return count.intValue();
			}

			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSLog> findSMSLogsByAccount(String accountId, Date startTimestamp, Date endTimestamp, int index,
			int count) throws DBException {
		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLog.FIND_BY_ACCOUNT_ID_AND_PERIOD, SMSLog.class)
					.setParameter(SMSLog.ACCOUNT_ID, accountId).setParameter(SMSLog.START_DATE, startTimestamp)
					.setParameter(SMSLog.END_DATE, endTimestamp).setFirstResult(index).setMaxResults(count);

			List<SMSLog> logs = q.getResultList();
			em.flush();
			em.clear();
			return logs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}

	}

	@Override
	public int countSMSLogs(Date startTimestamp, Date endTimestamp) throws DBException {
		try {
			Long count = em.createNamedQuery(SMSLog.COUNT_LOGS_BY_PERIOD, Long.class)
					.setParameter(SMSLog.START_DATE, startTimestamp).setParameter(SMSLog.END_DATE, endTimestamp)
					.getSingleResult();

			if (count != null) {
				return count.intValue();
			}

			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSLog> findSMSLogs(Date startTimestamp, Date endTimestamp, int index, int count) throws DBException {
		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLog.FIND_BY_PERIOD, SMSLog.class)
					.setParameter(SMSLog.START_DATE, startTimestamp).setParameter(SMSLog.END_DATE, endTimestamp)
					.setFirstResult(index).setMaxResults(count);

			List<SMSLog> logs = q.getResultList();
			em.flush();
			em.clear();
			return logs;

		} catch (Exception e) {
			throw new DBException(e);
		}

	}

	private String getAggCDRQuery(List<String> onNetPrefixes, String onNetStr, List<String> mobNetPrefixes,
			List<String> etiNetPrefixes, List<String> masrNetPrefixes, String mobNetStr, String etiNetStr,
			String masrNetStr, List<String> customAccountCDRs, List<SMSStatus> smsStatuses) {
		String onNetPrefixesStr = "'" + onNetPrefixes.get(0) + "'";
		String mobNetPrefixesStr = "'" + mobNetPrefixes.get(0) + "'";
		String etiNetPrefixesStr = "'" + etiNetPrefixes.get(0) + "'";
		String masrNetPrefixesStr = "'" + masrNetPrefixes.get(0) + "'";

		String customAccountCDRsStr = "";
		String caseStat;
		String smsStatusesStr = smsStatuses.get(0).getId().toString();
		String sql;

		for (int i = 1; i < onNetPrefixes.size(); i++) {
			onNetPrefixesStr += ",'" + onNetPrefixes.get(i) + "'";
		}

		for (int i = 1; i < mobNetPrefixes.size(); i++) {
			mobNetPrefixesStr += ",'" + mobNetPrefixes.get(i) + "'";
		}

		for (int i = 1; i < etiNetPrefixes.size(); i++) {
			etiNetPrefixesStr += ",'" + etiNetPrefixes.get(i) + "'";
		}
		for (int i = 1; i < masrNetPrefixes.size(); i++) {
			masrNetPrefixesStr += ",'" + masrNetPrefixes.get(i) + "'";
		}
		for (int i = 1; i < smsStatuses.size(); i++) {
			smsStatusesStr += ", " + smsStatuses.get(i).getId().toString();
		}

		caseStat = "(CASE WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + onNetPrefixesStr + ") THEN '" + onNetStr + "' "
				+ "WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + mobNetPrefixesStr + ") THEN '" + mobNetStr + "' "
				+ "WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + etiNetPrefixesStr + ") THEN '" + etiNetStr + "' "
				+ "WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + masrNetPrefixesStr + ") THEN '" + masrNetStr + "' "
				+ "ELSE l.RECEIVER " + "END) ";

		sql = "SELECT acct.BILLING_MSISDN, acct.ACCOUNT_ID, l.SENDER, SUM(l.SEGMENTS_COUNT) AS AGGREGATE_COUNT,    "
				+ caseStat + "AS RECEIVER_TYPE " + "FROM SMS_LOG l, ACCOUNTS acct "
				+ "WHERE  l.OWNER_ID = acct.ACCOUNT_ID " + "AND l.STATUS_ID IN(" + smsStatusesStr + ") AND "
				+ "l.PROCESSING_TIMESTAMP >= ? AND l.PROCESSING_TIMESTAMP < ? ";

		if (customAccountCDRs != null && !customAccountCDRs.isEmpty()) {

			customAccountCDRsStr = "'" + customAccountCDRs.get(0) + "'";
			for (int i = 1; i < customAccountCDRs.size(); i++) {
				customAccountCDRsStr += ",'" + customAccountCDRs.get(i) + "'";
			}
			sql += "AND acct.ACCOUNT_ID NOT IN (" + customAccountCDRsStr + ") ";
		}

		sql += "GROUP BY acct.BILLING_MSISDN, acct.ACCOUNT_ID, l.SENDER, " + caseStat
				+ " ORDER BY acct.ACCOUNT_ID, l.SENDER, RECEIVER_TYPE DESC";

		return sql;
	}

	private String getAggCDRQueryByAccountId(List<String> onNetPrefixes, String onNetStr, List<String> mobNetPrefixes,
			List<String> etiNetPrefixes, List<String> masrNetPrefixes, String mobNetStr, String etiNetStr,
			String masrNetStr, String accountId, List<SMSStatus> smsStatuses) {
		String onNetPrefixesStr = "'" + onNetPrefixes.get(0) + "'";
		String mobNetPrefixesStr = "'" + mobNetPrefixes.get(0) + "'";
		String etiNetPrefixesStr = "'" + etiNetPrefixes.get(0) + "'";
		String masrNetPrefixesStr = "'" + masrNetPrefixes.get(0) + "'";

		String caseStat;
		String smsStatusesStr = smsStatuses.get(0).getId().toString();
		String sql;

		for (int i = 1; i < onNetPrefixes.size(); i++) {
			onNetPrefixesStr += ",'" + onNetPrefixes.get(i) + "'";
		}

		for (int i = 1; i < mobNetPrefixes.size(); i++) {
			mobNetPrefixesStr += ",'" + mobNetPrefixes.get(i) + "'";
		}

		for (int i = 1; i < etiNetPrefixes.size(); i++) {
			etiNetPrefixesStr += ",'" + etiNetPrefixes.get(i) + "'";
		}

		for (int i = 1; i < masrNetPrefixes.size(); i++) {
			masrNetPrefixesStr += ",'" + masrNetPrefixes.get(i) + "'";
		}

		for (int i = 1; i < smsStatuses.size(); i++) {
			smsStatusesStr += ", " + smsStatuses.get(i).getId().toString();
		}

		caseStat = "(CASE WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + onNetPrefixesStr + ") THEN '" + onNetStr + "' "
				+ "WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + mobNetPrefixesStr + ") THEN '" + mobNetStr + "' "
				+ "WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + etiNetPrefixesStr + ") THEN '" + etiNetStr + "' "
				+ "WHEN (SUBSTR(l.RECEIVER,1,4)) IN (" + masrNetPrefixesStr + ") THEN '" + masrNetStr + "' "
				+ "ELSE l.RECEIVER " + "END) ";

		sql = "SELECT acct.BILLING_MSISDN, acct.ACCOUNT_ID, l.SENDER, SUM(l.SEGMENTS_COUNT) AS AGGREGATE_COUNT,    "
				+ caseStat + "AS RECEIVER_TYPE " + "FROM SMS_LOG l, ACCOUNTS acct "
				+ "WHERE l.OWNER_ID = acct.ACCOUNT_ID " + "AND l.STATUS_ID IN(" + smsStatusesStr + ") AND "
				+ "l.PROCESSING_TIMESTAMP >= ? AND l.PROCESSING_TIMESTAMP < ? ";

		sql += "AND acct.ACCOUNT_ID = '" + accountId + "' ";

		sql += "GROUP BY acct.BILLING_MSISDN, acct.ACCOUNT_ID, l.SENDER, " + caseStat
				+ " ORDER BY acct.ACCOUNT_ID, l.SENDER, RECEIVER_TYPE DESC";

		return sql;
	}

	public static <T> T map(Class<T> type, Object[] tuple) {
		List<Class<?>> tupleTypes = new ArrayList<>();
		for (Object field : tuple) {
			tupleTypes.add(field.getClass());
		}
		try {
			Constructor<T> ctor = type.getConstructor(tupleTypes.toArray(new Class<?>[tuple.length]));
			return ctor.newInstance(tuple);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<SMSLog> findSMSwithinDates(Date startDate, Date endDate, int index, int count) throws DBException {
		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLog.FIND_BY_DATES, SMSLog.class)
					.setParameter(SMSLog.START_DATE, startDate).setParameter(SMSLog.END_DATE, endDate)
					.setFirstResult(index).setMaxResults(count);

			List<SMSLog> logs = q.getResultList();
			em.flush();
			em.clear();
			return logs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSLog> findSMSwithinDates(String accountId, String msisdn, Date startDate, Date endDate, int index,
			int count) throws DBException {

		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLog.FIND_BY_MSISDN_AND_DATES, SMSLog.class)
					.setParameter(SMSLog.ACCOUNT_ID, accountId).setParameter(SMSLog.MSISDN, msisdn)
					.setParameter(SMSLog.START_DATE, startDate).setParameter(SMSLog.END_DATE, endDate)
					.setFirstResult(index).setMaxResults(count);

			List<SMSLog> logs = q.getResultList();

			em.flush();
			em.clear();
			return logs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}

	}

	@Override
	public int countSmsApiLogs(String accountId, String senderName, Date from, Date to) {
		Query query = null;

		if (senderName == null || senderName.isEmpty() || senderName.trim().isEmpty()) {
			query = getEntityManager().createNamedQuery("SMSLog.countDetailedSmsApiByDates");
		} else {
			query = getEntityManager().createNamedQuery("SMSLog.countDetailedSmsApiBySenderNameAndDate")
					.setParameter("senderName", senderName);
		}

		if (to == null) {
			to = new Date();
		}

		if (from == null) {
			from = new Date(0l);
		}

		query.setParameter("accountId", accountId).setParameter("startDate", from).setParameter("endDate", to);

		int result = ((Long) query.getSingleResult()).intValue();

		return result;
	}

	@Override
	public List<SMSLog> getSmsApiLogs(String accountId, String senderName, Date from, Date to, int start, int max) {
		Query query = null;

		if (senderName == null || senderName.isEmpty() || senderName.trim().isEmpty()) {
			query = getEntityManager().createNamedQuery("SMSLog.findDetailedSmsApiByDates");
		} else {
			query = getEntityManager().createNamedQuery("SMSLog.findDetailedSmsApiBySenderNameAndDate")
					.setParameter("senderName", senderName);
		}

		if (to == null) {
			to = new Date();
		}

		if (from == null) {
			from = new Date(0l);
		}

		query.setParameter("accountId", accountId).setParameter("startDate", from).setParameter("endDate", to)
				.setFirstResult(start);

		if (max > 0) {
			query.setMaxResults(max);
		}

		List<SMSLog> result = query.getResultList();

		return result;
	}

	@Override
	public List<SMSLog> findInIdList(List<String> smsIdList) throws DBException {
		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLogConst.FIND_SMSLOG_BY_IDLIST, SMSLog.class)
					.setParameter(SMSLogConst.SMS_ID_LIST, smsIdList);

			List<SMSLog> SMSs = q.getResultList();

			return SMSs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSLog> findSMSwithinDates(String accountId, Date startDate, Date endDate) throws DBException {
		if (accountId == null || accountId.trim().isEmpty()) {
			return null;
		}

		try {
			TypedQuery<SMSLog> q = em.createNamedQuery(SMSLogConst.FIND_ACCOUNT_ALL_SMSLOG, SMSLog.class)
					.setParameter(SMSLogConst.ACCOUNT, accountId).setParameter(SMSLogConst.DELIVERED, 5)
					.setParameter(SMSLogConst.UNDELIVERED, 6).setParameter(SMSLogConst.START_DATE, startDate)
					.setParameter(SMSLogConst.END_DATE, endDate);

			List<SMSLog> SMSs = q.getResultList();

			return SMSs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}
}
