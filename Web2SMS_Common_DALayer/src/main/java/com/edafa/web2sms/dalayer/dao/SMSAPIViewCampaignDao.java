package com.edafa.web2sms.dalayer.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSAPIViewCampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSSegmentLogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMS_API_VIEW_Camp;

@Stateless
public class SMSAPIViewCampaignDao extends AbstractDao<SMS_API_VIEW_Camp> implements SMSAPIViewCampaignDaoLocal {

	@EJB
	SMSStatusDaoLocal smsStatusDao;

	@EJB
	SMSSegmentLogDaoLocal smsSegmentLogDao;

	public SMSAPIViewCampaignDao() {
		super(SMS_API_VIEW_Camp.class);
	}

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}


	
	
	@Override
	public SMS_API_VIEW_Camp find(Object id) throws DBException {
		SMS_API_VIEW_Camp smsApiView =  super.find(id);
		return smsApiView;
	}




	@Override
	public List<SMS_API_VIEW_Camp> findSMSwithinDates(String accountId,
			Date startDate, Date endDate) throws DBException {
		if (accountId == null || accountId.trim().isEmpty()) {
			return null;
		}
		try {
			TypedQuery<SMS_API_VIEW_Camp> q = em
					.createNamedQuery("SMS_API_VIEW_Camp.findAccountAllSms",
							SMS_API_VIEW_Camp.class)
					.setParameter("account", accountId)
					.setParameter("deliverd", 5)
					.setParameter("undeliverd", 6)
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate);

			List<SMS_API_VIEW_Camp> SMSs = q.getResultList();

			em.flush();
			em.clear();
			return SMSs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

	@Override
	public List<SMS_API_VIEW_Camp> findSMSwithinMSISDNandDates(
			String accountId, String msisdn, Date startDate, Date endDate,
			int index, int count) throws DBException {
		try {
			TypedQuery<SMS_API_VIEW_Camp> q = em
					.createNamedQuery(
							"SMS_API_VIEW_Camp.findAccountMSISDNAllSms",
							SMS_API_VIEW_Camp.class)
					.setParameter("account", accountId)
					.setParameter("msisdn", msisdn)
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate).setFirstResult(index)
					.setMaxResults(count);

			List<SMS_API_VIEW_Camp> Sms = q.getResultList();

			em.flush();
			em.clear();
			return Sms;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}

	}



}
