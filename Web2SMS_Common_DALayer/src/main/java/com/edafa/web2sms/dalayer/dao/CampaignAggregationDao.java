package com.edafa.web2sms.dalayer.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignAggregationDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignAggregationView;

/**
 * Session Bean implementation class CampaignAggregationDao
 */
@Stateless
@LocalBean
public class CampaignAggregationDao extends AbstractDao<CampaignAggregationView> implements CampaignAggregationDaoLocal {

	public CampaignAggregationDao() {
		super(CampaignAggregationView.class);
		// TODO Auto-generated constructor stub
	}

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public List<CampaignAggregationView> findByAccountId(String accountId) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCOUNT_ID, CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId).getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountId(String accountId, int first, int max) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCOUNT_ID, CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId).setFirstResult(first)
					.setMaxResults(max).getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdAndName(String accountId, String campName) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCOUNT_ID_AND_NAME,
							CampaignAggregationView.class).setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.CAMP_NAME, "%" + campName.toLowerCase() + "%")
					.getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public CampaignAggregationView findByCampId(String campId) throws DBException {
		try {
			List<CampaignAggregationView> result = null;
			result = em.createNamedQuery(CampaignAggregationView.FIND_BY_CAMPAIGN_ID, CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.CAMP_ID, campId).getResultList();
			em.clear();
			if (result != null && !result.isEmpty())
				return result.get(0);
			else
				return null;

		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdAndName(String accountId, String campName, int first, int max)
			throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCOUNT_ID_AND_NAME,
							CampaignAggregationView.class).setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.CAMP_NAME, "%" + campName.toLowerCase() + "%")
					.setMaxResults(max).getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdAndStatus(String accountId, List<CampaignStatusName> statues)
			throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCOUNT_ID_AND_STATUS,
							CampaignAggregationView.class).setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.STATUSES, statues).getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdAndStatus(String accountId, List<CampaignStatusName> statues,
			int first, int max) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCOUNT_ID_AND_STATUS,
							CampaignAggregationView.class).setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.STATUSES, statues).getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCT_AND_DATE_RANGE_ORDERD,
							CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, int first, int max) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCT_AND_DATE_RANGE_ORDERD,
							CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.setFirstResult(first).setMaxResults(max).getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdNameAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, String campName) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCT_ID_NAME_AND_DATE_RANGE_ORDERD,
							CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.CAMP_NAME, "%" + campName.toLowerCase() + "%")
					.getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdNameAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, String campName, int first, int max) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCT_ID_NAME_AND_DATE_RANGE_ORDERD,
							CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.CAMP_NAME, "%" + campName.toLowerCase() + "%")
					.setFirstResult(first).setMaxResults(max).getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdStatusAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, List<CampaignStatusName> statues) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCT_AND_STATUS_AND_DATE_RANGE_ORDERD,
							CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.STATUSES, statues).getResultList();
			em.flush();
			em.clear();

			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignAggregationView> findByAccountIdStatusAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, List<CampaignStatusName> statues, int first, int max) throws DBException {
		try {
			List<CampaignAggregationView> result = em
					.createNamedQuery(CampaignAggregationView.FIND_BY_ACCT_AND_STATUS_AND_DATE_RANGE_ORDERD,

					CampaignAggregationView.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.STATUSES, statues).setFirstResult(first).setMaxResults(max)
					.getResultList();
			em.clear();
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public long count(String accountId) throws DBException {
		try {
			long result = em.createNamedQuery(CampaignAggregationView.COUNT_BY_ACCOUNT_ID, Long.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId).getResultList().get(0);
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public long count(String accountId, Date startTimestampFrom, Date startTimestampTo) throws DBException {
		try {
			long result = em
					.createNamedQuery(CampaignAggregationView.COUNT_BY_ACCOUNT_ID_AND_DATES, Long.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.getResultList().get(0);
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public long count(String accountId, Date startTimestampFrom, Date startTimestampTo, String campName)
			throws DBException {
		try {
			long result = em
					.createNamedQuery(CampaignAggregationView.COUNT_BY_ACCOUNT_ID_NAME_AND_DATES, Long.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.CAMP_NAME, "%" + campName.toLowerCase() + "%")
					.getResultList().get(0);
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public long count(String accountId, List<CampaignStatusName> statuses) throws DBException {
		try {
			long result = em.createNamedQuery(CampaignAggregationView.COUNT_BY_ACCOUNT_ID_AND_STATUS, Long.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.STATUSES, statuses).getResultList().get(0);
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public long count(String accountId, List<CampaignStatusName> statuses, Date startTimestampFrom,
			Date startTimestampTo) throws DBException {
		try {
			long result = em
					.createNamedQuery(CampaignAggregationView.COUNT_BY_ACCOUNT_ID_AND_STATUS_AND_DATES, Long.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.STATUSES, statuses)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_FROM, startTimestampFrom,
							TemporalType.TIMESTAMP)
					.setParameter(CampaignAggregationView.START_TIMESTAMP_TO, startTimestampTo, TemporalType.TIMESTAMP)
					.getResultList().get(0);
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public long count(String accountId, String campName) throws DBException {
		try {
			long result = em.createNamedQuery(CampaignAggregationView.COUNT_BY_ACCOUNT_ID_AND_NAME, Long.class)
					.setParameter(CampaignAggregationView.ACCOUNT_ID, accountId)
					.setParameter(CampaignAggregationView.CAMP_NAME, "%" + campName.toLowerCase() + "%")
					.getResultList().get(0);
			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

}
