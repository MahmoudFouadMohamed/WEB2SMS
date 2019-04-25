package com.edafa.web2sms.dalayer.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSAPILogDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSAPIView;
import com.edafa.web2sms.dalayer.model.constants.SMSLogConst;

/**
 * Session Bean implementation class SMSAPILog
 */
@Stateless
@LocalBean
public class SMSAPIViewDao extends AbstractDao<SMSAPIView> implements SMSAPILogDaoLocal {

	/**
	 * Default constructor.
	 */
	public SMSAPIViewDao() {
		super(SMSAPIView.class);
	}

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public List<SMSAPIView> getSMSAPIView(String accountId, String senderName, String smsText, Date dateFrom,
			Date dateTo, int first, int max) {
		if (accountId == null || accountId.trim().isEmpty()) {
			return null;
		}
		String sql = formSql(accountId, senderName, smsText, dateFrom, dateTo, false);
		TypedQuery<SMSAPIView> query = em.createQuery(sql, SMSAPIView.class);

		query.setParameter("accountId", accountId);
		if (senderName != null && !senderName.trim().equals("")) {
			query.setParameter("senderName", "%" + senderName.toLowerCase() + "%");
		}
		if (dateFrom != null) {
			query.setParameter("dateFrom", dateFrom);
		}
		if (dateTo != null) {
			query.setParameter("dateTo", dateTo);
		}
		if (smsText != null && !smsText.trim().equals("")) {
			query.setParameter("smsText", "%" + smsText.toLowerCase() + "%");
		}
		return query.getResultList();
	}

	public Long countSMSAPIView(String accountId, String senderName, String smsText, Date dateFrom, Date dateTo) {
		if (accountId == null || accountId.trim().isEmpty()) {
			return null;
		}
		String sql = formSql(accountId, senderName, smsText, dateFrom, dateTo, true);
		TypedQuery<Long> query = em.createQuery(sql, Long.class);

		query.setParameter("accountId", accountId);
		if (senderName != null && !senderName.trim().equals("")) {
			query.setParameter("senderName", "%" + senderName.toLowerCase() + "%");
		}
		if (dateFrom != null) {
			query.setParameter("dateFrom", dateFrom);
		}
		if (dateTo != null) {
			query.setParameter("dateTo", dateTo);
		}
		if (smsText != null && !smsText.trim().equals("")) {
			query.setParameter("smsText", "%" + smsText.toLowerCase() + "%");
		}

		return (long) query.getSingleResult();

	}

	private String formSql(String accountId, String senderName, String smsText, Date dateFrom, Date dateTo,
			boolean countFlag) {

		String sql;

		if (countFlag) {
			sql = "SELECT COUNT(DISTINCT(a.sender)) FROM SMSAPIView a WHERE a.account.accountId=:accountId";
		} else {
			sql = "SELECT  a FROM SMSAPIView a WHERE a.account.accountId=:accountId";
		}

		if (senderName != null && !senderName.trim().equals("")) {
			sql += " AND LOWER(a.sender)  LIKE :senderName";
		}

		if (dateFrom != null && dateTo != null) {
			sql += " AND a.processingDate >= :dateFrom AND a.processingDate <= :dateTo";
		} else if (dateFrom != null) {
			sql += " AND a.processingDate >= :dateFrom";
		} else if (dateTo != null) {
			sql += " AND a.processingDate <= :dateTo";
		}

		if (smsText != null && !smsText.trim().equals("")) {
			sql += " AND LOWER(a.smsText)  LIKE :smsText";
		}

		return sql;
	}

	@Override
	public SMSAPIView find(Object id) throws DBException {
		SMSAPIView smsApiView = super.find(id);
		return smsApiView;
	}
        
        @Override
	public List<SMSAPIView> findInIdList(List<String> smsIdList) throws DBException {

		try {
			TypedQuery<SMSAPIView> q = em.createNamedQuery(SMSLogConst.FIND_SMS_BY_IDLIST, SMSAPIView.class)
					.setParameter(SMSLogConst.SMS_ID_LIST, smsIdList);

			List<SMSAPIView> SMSs = q.getResultList();

			em.flush();
			em.clear();
			return SMSs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
		
	}

	@Override
	public List<SMSAPIView> findSMSwithinDates(String accountId, Date startDate, Date endDate) throws DBException {
		if (accountId == null || accountId.trim().isEmpty()) {
			return null;
		}
		try {
			TypedQuery<SMSAPIView> q = em.createNamedQuery(SMSLogConst.FIND_ACCOUNT_ALL_SMS, SMSAPIView.class)
					.setParameter(SMSLogConst.ACCOUNT, accountId).setParameter(SMSLogConst.DELIVERED, 5).setParameter(SMSLogConst.UNDELIVERED, 6)
					.setParameter(SMSLogConst.START_DATE, startDate).setParameter(SMSLogConst.END_DATE, endDate);

			List<SMSAPIView> SMSs = q.getResultList();

			em.flush();
			em.clear();
			return SMSs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSAPIView> findSMSwithinMSISDNandDates(String accountId, String msisdn, Date startDate, Date endDate,
			int index, int count) throws DBException {
		try {
			TypedQuery<SMSAPIView> q = em.createNamedQuery(SMSLogConst.FIND_BY_ACCOUNT_AND_MSISDN_ALL_SMS, SMSAPIView.class)
					.setParameter(SMSLogConst.ACCOUNT, accountId).setParameter(SMSLogConst.MSISDN, msisdn)
					.setParameter(SMSLogConst.START_DATE, startDate).setParameter(SMSLogConst.END_DATE, endDate).setFirstResult(index)
					.setMaxResults(count);

			List<SMSAPIView> Sms = q.getResultList();

			em.flush();
			em.clear();
			return Sms;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}

	}

}
