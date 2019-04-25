package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSenderDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountSender;

/**
 * Session Bean implementation class AccountSender
 */
@Stateless
public class AccountSenderDao extends AbstractDao<AccountSender> implements AccountSenderDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public AccountSenderDao() {
		super(AccountSender.class);
	}

	
	@Override
	public int CountByAccountId(String accountId) throws DBException {
		try {
			Long result = em.createNamedQuery(AccountSender.COUNT_BY_ACCOUNT_ID, Long.class)
					.setParameter(AccountSender.ACCOUNT_ID, accountId).getResultList().get(0);
			return result.intValue();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
	
	
	@Override
	public List<AccountSender> findByAccountId(String accountId) throws DBException {
		try {
			List<AccountSender> resutlSet = em.createNamedQuery(AccountSender.FIND_BY_ACCOUNT_ID, AccountSender.class)
					.setParameter(AccountSender.ACCOUNT_ID, accountId).getResultList();
			return resutlSet;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

//	@Override
//	public AccountSender findBySenderName(String sender) throws DBException {
//		try {
//		List<AccountSender> resutlSet = em.createNamedQuery(AccountSender.FIND_BY_SENDER_NAME, AccountSender.class)
//				.setParameter(AccountSender.SENDER_NAME, sender).getResultList();
//
//			if (resutlSet != null && !resutlSet.isEmpty()) {
//				return resutlSet.get(0);
//			}
//		} catch (Exception e) {
//			throw new DBException(e);
//		}
//		return null;
//	}
//	
//	@Override
//	public int CountBySenderName(String sender) throws DBException {
//		try {
//			List<Integer> resultList = em.createNamedQuery(AccountSender.COUNT_BY_SENDER_NAME, Integer.class)
//					.setParameter(AccountSender.SENDER_NAME, sender).getResultList();
//			if(resultList != null && !resultList.isEmpty()){
//				return resultList.get(0);
//			}
//			return 0;
//		} catch (Exception e) {
//			throw new DBException(e);
//		}
//	}
//
	
	@Override
	public AccountSender findByAccountIdAndSenderName(String accountId, String sender) throws DBException {
		try {
			List<AccountSender>  resultList = em.createNamedQuery(AccountSender.FIND_BY_ACCOUNT_ID_AND_SENDER_NAME, AccountSender.class)
					.setParameter(AccountSender.ACCOUNT_ID, accountId)
					.setParameter(AccountSender.SENDER_NAME, sender).getResultList();
			if(resultList != null && !resultList.isEmpty()){
				return resultList.get(0);
			}
			return null;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
	
	@Override
	public void removeAllByAccountId(String acctId) throws DBException {
		try {
			em.createNamedQuery(AccountSender.REMOVE_ALL_BY_ACCT_ID).setParameter(AccountSender.ACCOUNT_ID, acctId)
					.executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

}
