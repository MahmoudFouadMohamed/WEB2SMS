package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountQuotaDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountQuota;

/**
 *
 * @author mayahmed
 */

@Stateless
public class AccountQuotaDao extends AbstractDao<AccountQuota> implements AccountQuotaDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AccountQuotaDao() {
		super(AccountQuota.class);
	}

	public AccountQuota getAccountQuotaInfoByAccountTierID(Integer accountTierId) {
		List<AccountQuota> resultList = em.createNamedQuery(AccountQuota.FIND_BY_ACCT_TIERS_ID, AccountQuota.class)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.setParameter(AccountQuota.ACCT_TIERS_ID, accountTierId).getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		} else
			return null;

	}

	@Override
	public void incrementReservedSmss(int accountTierId, int value) throws DBException {
		try {
			em.createNamedQuery(AccountQuota.INCREMENT_RESERVED_SMSS, Integer.class)
					.setParameter(AccountQuota.ACCT_TIERS_ID, accountTierId)
					.setParameter(AccountQuota.RESERVED_SMSS, value).executeUpdate();
		}// end try
		catch (Exception e) {
			throw e;
		}// end catch
	}// end of method incrementReservedSmss

	@Override
	public int updateAccountQuota(String accountId, int reservedDelta, int consumedDelta) throws DBException {
		try {
			return em.createNamedQuery(AccountQuota.UPDATE_QUOTA).setParameter(AccountQuota.ACCOUNT_ID, accountId)
					.setParameter(AccountQuota.RESERVED_SMSS, reservedDelta)
					.setParameter(AccountQuota.CONSUMED_SMSS, consumedDelta).executeUpdate();
		} catch (Exception e) {
			throw e;
		}
		
	}
}// end of class AccountQuotaDao
