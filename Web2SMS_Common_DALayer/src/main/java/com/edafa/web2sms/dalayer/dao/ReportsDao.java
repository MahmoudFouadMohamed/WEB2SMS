package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ReportsDaoLocal;
import com.edafa.web2sms.dalayer.enums.ReportStatus;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Reports;

/**
*
* @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
*/
@Stateless
public class ReportsDao extends AbstractDao<Reports> implements ReportsDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ReportsDao() {
		super(Reports.class);
	}

	@Override
	public int countAdminReports() throws DBException {
		int count = -1;

		try {
			Query countQuery = getEntityManager().createNamedQuery("Reports.countAdmin");

			count = ((Long) countQuery.getSingleResult()).intValue();
		} catch (Exception e) {
			throw new DBException(e);

		}

		return count;
	}

	@Override
	public int countUserReports(String ownerId) throws DBException {
		int count = -1;

		try {
			Query countQuery = getEntityManager().createNamedQuery("Reports.countByOwner").setParameter("ownerId",
					ownerId);

			count = ((Long) countQuery.getSingleResult()).intValue();
		} catch (Exception e) {
			throw new DBException(e);

		}

		return count;
	}

	@Override
	public List<Reports> getAdminReports(int start, int count) throws DBException {
		List<Reports> reports = null;

		try {
			Query query = getEntityManager().createNamedQuery("Reports.findByAdmin").setFirstResult(start);

			if (count > 0) {
				query.setMaxResults(count);
			}

			reports = query.getResultList();
		} catch (Exception e) {
			throw new DBException(e);

		}

		return reports;
	}

	@Override
	public List<Reports> getUserReports(String ownerId, int start, int count) throws DBException {
		List<Reports> reports = null;

		try {
			Query query = getEntityManager().createNamedQuery("Reports.findByOwner").setParameter("ownerId", ownerId)
					.setFirstResult(start);

			if (count > 0) {
				query.setMaxResults(count);
			}

			reports = query.getResultList();
		} catch (Exception e) {
			throw new DBException(e);

		}

		return reports;
	}

	@Override
	public List<Reports> getPendingReports() throws DBException {
		List<Reports> reports = null;

		try {
			Query query = getEntityManager().createNamedQuery("Reports.findByStatusOrdered").setParameter("status",
					ReportStatus.PENDING);

			reports = query.getResultList();
		} catch (Exception e) {
			throw new DBException(e);

		}

		return reports;
	}

	@Override
	public List<Reports> getFailedReports() throws DBException {
		List<Reports> reports = null;

		try {
			Query query = getEntityManager().createNamedQuery("Reports.findByStatusOrdered").setParameter("status",
					ReportStatus.FAILED);

			reports = query.getResultList();
		} catch (Exception e) {
			throw new DBException(e);

		}

		return reports;
	}

}
