package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSMSAPIDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountIP;
import com.edafa.web2sms.dalayer.model.AccountSMSAPI;

/**
 * Session Bean implementation class AccountSMSAPIDao
 */
@Stateless
public class AccountSMSAPIDao extends AbstractDao<AccountSMSAPI> implements
		AccountSMSAPIDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public AccountSMSAPIDao() {
		super(AccountSMSAPI.class);

	}

	public AccountSMSAPI findByAccountId(String accountId) {
		List<AccountSMSAPI> resultList;
		TypedQuery<AccountSMSAPI> q = em.createNamedQuery(AccountSMSAPI.FIND_BY_ACCOUNT_ID, AccountSMSAPI.class)
				.setParameter(AccountSMSAPI.ACCOUNT_ID, accountId);
		resultList = q.getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		} else
			return null;

	}
	
        @Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public com.edafa.web2sms.dalayer.pojo.AccountSMSAPI findPojoByAccountId(String accountId) {
		List<AccountSMSAPI> resultList;
		TypedQuery<AccountSMSAPI> q = em.createNamedQuery(AccountSMSAPI.FIND_BY_ACCOUNT_ID, AccountSMSAPI.class)
				.setParameter(AccountSMSAPI.ACCOUNT_ID, accountId);
		resultList = q.getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			AccountSMSAPI accountSMSAPI=resultList.get(0);
			com.edafa.web2sms.dalayer.pojo.AccountSMSAPI accountSMSIAPIPojo=new 
					com.edafa.web2sms.dalayer.pojo.AccountSMSAPI(accountSMSAPI.getPassword(),accountSMSAPI.getSecureKey());
			for(AccountIP accountIP:accountSMSAPI.getAccountIPs()){
				accountSMSIAPIPojo.addAccountIP(accountIP.getClientIp());
			}
			return accountSMSIAPIPojo;
		} else
			return null;

	}
	
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public com.edafa.web2sms.dalayer.pojo.AccountSMSAPI findPojoByAccountId(Account account) {
        AccountSMSAPI accountSMSAPI = account.getAccountSmsApi();
        if (accountSMSAPI != null) {
            com.edafa.web2sms.dalayer.pojo.AccountSMSAPI accountSMSIAPIPojo = new com.edafa.web2sms.dalayer.pojo.AccountSMSAPI(accountSMSAPI.getPassword(), accountSMSAPI.getSecureKey());
            for (AccountIP accountIP : accountSMSAPI.getAccountIPs()) {
                accountSMSIAPIPojo.addAccountIP(accountIP.getClientIp());
            }
            return accountSMSIAPIPojo;
        } else {
            return null;
        }

    }

	public int CountByAccountId(String accountId) {
		return em.createNamedQuery(AccountSMSAPI.COUNT_BY_ACCOUNT_ID, Integer.class)
				.setParameter(AccountSMSAPI.ACCOUNT_ID, accountId).getFirstResult();

	}

	/****/

	@Override
	public List<Account> getAccountListPaginated(int first, int max) {
		List<Account> resultList = em.createNamedQuery(AccountSMSAPI.FIND_ALL, Account.class)
				.setFirstResult(first).setMaxResults(max).getResultList();
		return resultList;

	}

	@Override
	public int getAccountsCount() {
		return em
				.createNamedQuery(AccountSMSAPI.COUNT_ALL_ACCOUNTS,
						Integer.class).getResultList().get(0);
	}

	@Override
	public void editSmsApiInfo(AccountSMSAPI accountSMSAPI) throws DBException {
		em.merge(accountSMSAPI);
		
	}

	
}
