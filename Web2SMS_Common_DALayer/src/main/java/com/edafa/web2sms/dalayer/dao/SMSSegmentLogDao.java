package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSSegmentLogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.dalayer.model.SMSSegmentLog;

/**
 * 
 * @author akhalifah
 */
@Stateless
public class SMSSegmentLogDao extends AbstractDao<SMSSegmentLog> implements
		SMSSegmentLogDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@EJB
	SMSStatusDaoLocal smsStatusDao;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SMSSegmentLogDao() {
		super(SMSSegmentLog.class);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void create(List<SMSSegmentLog> smsSegmentLogs) throws DBException {
		try {
			for (SMSSegmentLog smsSegmentLog : smsSegmentLogs) {
				em.persist(smsSegmentLog);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public SMSLog findSMSLogByMessageId(String messageId) throws DBException {
		try {
			List<SMSLog> smsLogList = em
					.createNamedQuery(SMSSegmentLog.FIND_SMS_LOG_BY_MSG_ID,
							SMSLog.class)
					.setParameter(SMSSegmentLog.MESSAGE_ID, messageId)
					.getResultList();
			if (smsLogList != null && smsLogList.size() > 0)
				return smsLogList.get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	public SMSLog findSMSLogByMessageIdAndSMSCId(String messageId, int smscId)
			throws DBException {
		try {
			List<SMSLog> smsLogList = this.em
					.createNamedQuery(
							SMSSegmentLog.FIND_SMS_LOG_BY_MSG_ID_AND_SMSC_ID,
							SMSLog.class)
					.setParameter(SMSSegmentLog.MESSAGE_ID, messageId)
					.setParameter(SMSSegmentLog.SMSC_ID, smscId)
					.getResultList();
			if ((smsLogList != null) && (smsLogList.size() > 0))
				return smsLogList.get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateDeliveredSMSSementsInfo(List<SMSSegmentLog> segmentsLogs)
			throws DBException {
		try {
			for (SMSSegmentLog segLog : segmentsLogs) {
				em.createNamedQuery(
						SMSSegmentLog.UPDATE_DELIVERED_SMS_SEG_INFO,
						SMSLog.class)
						.setParameter(SMSSegmentLog.MESSAGE_ID,
								segLog.getMessageId())
						.setParameter(SMSSegmentLog.SMSC_ID, segLog.getSmscId())
						.setParameter(SMSSegmentLog.DELIVERY_DATE,
								segLog.getDeliveryDate(),
								TemporalType.TIMESTAMP)
						.setParameter(SMSSegmentLog.IS_DELIVERED,
								segLog.isDelivered()).executeUpdate();
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<SMSSegmentLog> findBySMSId(String smsId) throws DBException {
		try {
			return em
					.createNamedQuery(SMSSegmentLog.FIND_BY_SMS_ID,
							SMSSegmentLog.class)
					.setParameter(SMSSegmentLog.SMS_ID, smsId).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
}
