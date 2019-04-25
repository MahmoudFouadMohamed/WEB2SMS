package com.edafa.web2sms.dalayer.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatsDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignStatsReport;

/**
*
* @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
*/
@Stateless
public class CampaignStatsDao implements CampaignStatsDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public CampaignStatsDao() {}

	@Override
	public List<CampaignStatsReport> find(String accountId, String campName, int first, int max) throws DBException {
		Query query = getEntityManager().createNamedQuery("CampaignStatsReport.findByAccountIdAndCampaignName");

		query.setParameter("accountId", accountId).setParameter("campName", campName).setFirstResult(first);

		if (max > 0) {
			query.setMaxResults(max);
		}

		List<CampaignStatsReport> result = query.getResultList();

		return result;
	}

	@Override
	public CampaignStatsReport find(String accountId, String campId) throws DBException {
		CampaignStatsReport result = null;
		Query query = getEntityManager().createNamedQuery("CampaignStatsReport.findByAccountIdAndCampaignId");

		query.setParameter("accountId", accountId).setParameter("campId", campId);

		List<CampaignStatsReport> resultList = query.getResultList();

		if (resultList != null && resultList.size() > 0) {
			result = resultList.get(0);
		}

		return result;
	}

	@Override
	public List<CampaignStatsReport> find(String accountId, Date startDate, Date endDate, int first, int max)
			throws DBException {
		Query query = getEntityManager().createNamedQuery("CampaignStatsReport.findByAccountIdAndDates");

		if (endDate == null) {
			endDate = new Date();
		}

		if (startDate == null) {
			startDate = new Date(0l);
		}

		query.setParameter("accountId", accountId).setParameter("startDate", startDate).setParameter("endDate", endDate)
				.setFirstResult(first);

		if (max > 0) {
			query.setMaxResults(max);
		}

		List<CampaignStatsReport> result = query.getResultList();

		return result;
	}

	@Override
	public List<CampaignStatsReport> find(String accountId, String campName, Date startDate, Date endDate, int first,
			int max) throws DBException {
		Query query = getEntityManager().createNamedQuery("CampaignStatsReport.findByAccountIdAndCampNameAndDates");

		if (endDate == null) {
			endDate = new Date();
		}

		if (startDate == null) {
			startDate = new Date(0l);
		}

		query.setParameter("accountId", accountId).setParameter("campName", campName)
				.setParameter("startDate", startDate).setParameter("endDate", endDate).setFirstResult(first);

		if (max > 0) {
			query.setMaxResults(max);
		}

		List<CampaignStatsReport> result = query.getResultList();

		return result;
	}

	@Override
	public int count(String accountId) throws DBException {
		Query query = null;

		query = getEntityManager().createNamedQuery("CampaignStatsReport.countByAccountId").setParameter("accountId",
				accountId);

		int result = ((Long) query.getSingleResult()).intValue();

		return result;
	}

	@Override
	public int count(String accountId, String campName) throws DBException {
		Query query = null;

		query = getEntityManager().createNamedQuery("CampaignStatsReport.countByAccountIdAndCampName")
				.setParameter("campName", campName).setParameter("accountId", accountId);

		int result = ((Long) query.getSingleResult()).intValue();

		return result;
	}

	@Override
	public int count(String accountId, Date startDate, Date endDate) throws DBException {
		Query query = null;

		if (endDate == null) {
			endDate = new Date();
		}

		if (startDate == null) {
			startDate = new Date(0l);
		}

		query = getEntityManager().createNamedQuery("CampaignStatsReport.countByAccountIdAndDate")
				.setParameter("startDate", startDate).setParameter("endDate", endDate)
				.setParameter("accountId", accountId);

		int result = ((Long) query.getSingleResult()).intValue();

		return result;
	}

	@Override
	public int count(String accountId, Date startDate, Date endDate, String campName) throws DBException {
		Query query = null;

		if (endDate == null) {
			endDate = new Date();
		}

		if (startDate == null) {
			startDate = new Date(0l);
		}

		query = getEntityManager().createNamedQuery("CampaignStatsReport.countByAccountIdAndCampNameAndDate")
				.setParameter("startDate", startDate).setParameter("endDate", endDate)
				.setParameter("accountId", accountId).setParameter("campName", campName);

		int result = ((Long) query.getSingleResult()).intValue();

		return result;
	}

}
