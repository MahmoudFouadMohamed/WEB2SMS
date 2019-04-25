package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.QuotaHistoryDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.QuotaHistory;

/**
 * Session Bean implementation class QuotaHistoryDao
 */
@Stateless
@LocalBean
public class QuotaHistoryDao extends AbstractDao<QuotaHistory> implements QuotaHistoryDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public QuotaHistoryDao() {
		super(QuotaHistory.class);
	}

	@Override
	public QuotaHistory findQuotaHistory(String accountId) throws DBException {
		List<QuotaHistory> result = em.createNamedQuery(QuotaHistory.FIND_BY_ACCOUNT_ID, QuotaHistory.class)
				.setParameter(QuotaHistory.ACCOUNT_ID, accountId).getResultList();
		if(result != null && !result.isEmpty()){
			return result.get(0);			
		}
		return null;

	}
}
