package com.edafa.web2sms.dalayer.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SmsApiStatsDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SmsApiDailySmsStats;
import com.edafa.web2sms.dalayer.model.SmsApiHourlySmsStats;

/**
*
* @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
*/
@Stateless
public class SmsApiStatsDao implements SmsApiStatsDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public SmsApiStatsDao() {}

	@Override
	public List<SmsApiHourlySmsStats> getSmsApiHorlyStats(String ownerId, String senderName, Date from, Date to,
			int start, int count) throws DBException {
		Query query = null;

		if (senderName == null || senderName.isEmpty()) {
			query = getEntityManager().createNamedQuery("SmsApiHourlySmsStats.findByOwnerIdAndDates");
		} else {
			query = getEntityManager().createNamedQuery("SmsApiHourlySmsStats.findByOwnerIdAndSenderAndDates")
					.setParameter("senderName", senderName);
		}

		if (to == null) {
			to = new Date();
		}

		if (from == null) {
			from = new Date(0l);
		}

		query.setParameter("ownerId", ownerId).setParameter("from", from).setParameter("to", to).setFirstResult(start);

		if (count > 0) {
			query.setMaxResults(count);
		}

		List<SmsApiHourlySmsStats> result = query.getResultList();

		return result;
	}

	@Override
	public List<SmsApiDailySmsStats> getSmsApiDailyStats(String ownerId, String senderName, Date from, Date to,
			int start, int count) throws DBException {
		Query query = null;

		if (senderName == null || senderName.isEmpty()) {
			query = getEntityManager().createNamedQuery("SmsApiDailySmsStats.findByOwnerIdAndDates");
		} else {
			query = getEntityManager().createNamedQuery("SmsApiDailySmsStats.findByOwnerIdAndSenderAndDates")
					.setParameter("senderName", senderName);
		}

		if (to == null) {
			to = new Date();
		}

		if (from == null) {
			from = new Date(0l);
		}

		query.setParameter("ownerId", ownerId).setParameter("from", from).setParameter("to", to).setFirstResult(start);

		if (count > 0) {
			query.setMaxResults(count);
		}

		List<SmsApiDailySmsStats> result = query.getResultList();

		return result;
	}

	@Override
	public int countSmsApiHorlyStats(String ownerId, String senderName, Date from, Date to) throws DBException {
		Query query = null;

		if (senderName == null || senderName.isEmpty()) {
			query = getEntityManager().createNamedQuery("SmsApiHourlySmsStats.countByOwnerIdAndDates");
		} else {
			query = getEntityManager().createNamedQuery("SmsApiHourlySmsStats.countByOwnerIdAndSenderAndDates")
					.setParameter("senderName", senderName);
		}

		if (to == null) {
			to = new Date();
		}

		if (from == null) {
			from = new Date(0l);
		}

		query.setParameter("ownerId", ownerId).setParameter("from", from).setParameter("to", to);

		int result = ((Long) query.getSingleResult()).intValue();

		return result;
	}

	@Override
	public int countSmsApiDailyStats(String ownerId, String senderName, Date from, Date to) throws DBException {
		Query query = null;

		if (senderName == null || senderName.isEmpty()) {
			query = getEntityManager().createNamedQuery("SmsApiDailySmsStats.countByOwnerIdAndDates");
		} else {
			query = getEntityManager().createNamedQuery("SmsApiDailySmsStats.countByOwnerIdAndSenderAndDates")
					.setParameter("senderName", senderName);
		}

		if (to == null) {
			to = new Date();
		}

		if (from == null) {
			from = new Date(0l);
		}

		query.setParameter("ownerId", ownerId).setParameter("from", from).setParameter("to", to);

		int result = ((Long) query.getSingleResult()).intValue();

		return result;
	}

}
