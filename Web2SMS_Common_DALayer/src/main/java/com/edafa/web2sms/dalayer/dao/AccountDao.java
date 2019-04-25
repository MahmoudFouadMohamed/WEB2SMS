/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountDaoLocal;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;

/**
 * 
 * @author yyaseen
 */
@Stateless
// @LocalBean
public class AccountDao extends AbstractDao<Account> implements AccountDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AccountDao() {
		super(Account.class);
	}

	@Override
	public Account findByAccountId(String accountId) throws DBException {
		try{
		TypedQuery<Account> q = em.createNamedQuery(Account.FIND_BY_ACCOUNT_ID, Account.class).setParameter(
				Account.ACCOUNT_ID, accountId);
		List<Account> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}}catch(Exception e){
			throw new DBException(e);
		}
	}

	@Override
	public Account findWithSMSAPIByAccountId(String accountId, int timeOut) throws DBException {
		try{
		TypedQuery<Account> q = em.createNamedQuery(Account.FIND_WITH_SMSAPI_BY_ACCOUNT_ID, Account.class).setParameter(Account.ACCOUNT_ID, accountId).setHint("javax.persistence.query.timeout", timeOut);
		List<Account> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}}catch(Exception e){
			throw new DBException(e);
		}
	}
        
        @Override
	public Account findWithSMSAPICampByAccountId(String accountId, int timeOut) throws DBException {
		try{
		TypedQuery<Account> q = em.createNamedQuery(Account.FIND_WITH_SMSAPI_CAMP_BY_ACCOUNT_ID, Account.class).setParameter(Account.ACCOUNT_ID, accountId).setHint("javax.persistence.query.timeout", timeOut);
		List<Account> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}}catch(Exception e){
			throw new DBException(e);
		}
	}
	
	@Override
	public List<Account> findByStatuses(List<AccountStatus> statusList) throws DBException {
		try{TypedQuery<Account> q = em.createNamedQuery(Account.FIND_BY_STATUSES, Account.class).setParameter(
				Account.STATUSES, statusList);
		List<Account> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list;
		} else {
			return null;
		}}catch(Exception e){
			throw new DBException(e);
		}
	}

	@Override
	public int updateAccountStatus(String acctId, AccountStatus newStatus) throws DBException {
		try {
			return em.createNamedQuery(Account.UPDATE_ACCOUNT_STATUS).setParameter(Account.ACCOUNT_ID, acctId)
					.setParameter(Account.STATUS, newStatus).executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public Account findByCompanyName(String companyName) throws DBException {
		try {

			List<Account> list = em.createNamedQuery(Account.FIND_BY_COMPANY_NAME, Account.class)
					.setParameter(Account.COMPANY_NAME, companyName).getResultList();
			if (list != null && list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		
	}
	
	@Override
	public Account findByBillingMSISDN(String msisdn) throws DBException {
		try {

			List<Account> list = em.createNamedQuery(Account.FIND_BY_MSISDN, Account.class)
					.setParameter(Account.MSISDN, msisdn).getResultList();
			if (list != null && list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		
	}

	@Override
	public String getAccountIdByCompanyId(String companyId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * searching accounts by either accountId, companyName, billingMsisdn, or status.
	 * and return the results paginated
	 * @author tmohamed
	 */
	@Override
	public List<Account> searchAccount(String accountID, String companyName, String billingMSISDN,
			List<AccountStatus> statuses, int first, int max) throws DBException
	{
		List<Account> result = null;
		try
		{
			boolean countFlag = false;
			String sql = formatQuery(accountID, companyName, billingMSISDN, statuses, countFlag);
			TypedQuery<Account> query = em.createQuery(sql, Account.class);
			
			if(accountID != null && !accountID.trim().isEmpty())
			{
				query.setParameter("accountId", "%" + accountID.toLowerCase() + "%");
			}// end if
			
			if(companyName != null && !companyName.trim().isEmpty())
			{
				query.setParameter("companyName", "%" + companyName.toLowerCase() + "%");
			}// end if
			
			if(billingMSISDN != null && !billingMSISDN.trim().isEmpty())
			{
				query.setParameter("billingMsisdn", "%" + billingMSISDN.toLowerCase() + "%");
			}// end if
			
			if(statuses != null && !statuses.isEmpty())
			{
				query.setParameter("statuses", statuses);
			}// end if
			
			result = query.setMaxResults(max).setFirstResult(first).getResultList();
		}// end try
		catch(Exception e)
		{
			throw new DBException(e);
		}// end catch
		
		return result;
	}// end of method searchAccount

	
	
	@Override
	public List<Account> searchAccount(String accountID, String companyName, String billingMSISDN,
			List<AccountStatus> statuses) throws DBException
	{
		List<Account> result = null;
		try
		{
			boolean countFlag = false;
			String sql = formatQuery(accountID, companyName, billingMSISDN, statuses, countFlag);
			TypedQuery<Account> query = em.createQuery(sql, Account.class);
			
			if(accountID != null && !accountID.trim().isEmpty())
			{
				query.setParameter("accountId", "%" + accountID.toLowerCase() + "%");
			}// end if
			
			if(companyName != null && !companyName.trim().isEmpty())
			{
				query.setParameter("companyName", "%" + companyName.toLowerCase() + "%");
			}// end if
			
			if(billingMSISDN != null && !billingMSISDN.trim().isEmpty())
			{
				query.setParameter("billingMsisdn", "%" + billingMSISDN.toLowerCase() + "%");
			}// end if
			
			if(statuses != null && !statuses.isEmpty())
			{
				query.setParameter("statuses", statuses);
			}// end if
			
			result = query.getResultList();
		}// end try
		catch(Exception e)
		{
			throw e;
		}// end catch
		
		return result;
	}// end of method searchAccount Not paginated

	private String formatQuery(String accountID, String companyName, String billingMSISDN,
			List<AccountStatus> statuses, boolean countFlag)
	{
		StringBuilder query = new StringBuilder();
		
		if (countFlag) 
		{
			query.append("SELECT COUNT(a) FROM Account a LEFT OUTER JOIN FETCH a.accountSmsApi WHERE a.accountId IS NOT NULL ");
		}// end if
		else 
		{
			query.append("SELECT a FROM Account a LEFT OUTER JOIN FETCH a.accountSmsApi WHERE a.accountId IS NOT NULL ");
		}// end else
		
		if(accountID != null && !accountID.trim().isEmpty())
		{
			query.append(" AND a.accountId LIKE :accountId ");
		}// end if
		
		if(companyName != null && !companyName.trim().isEmpty())
		{
			query.append(" AND a.companyName LIKE :companyName ");
		}// end if
		
		if(billingMSISDN != null && !billingMSISDN.trim().isEmpty())
		{
			query.append(" AND a.billingMsisdn LIKE :billingMsisdn ");
		}// end if
		
		if(statuses != null && !statuses.isEmpty())
		{
			query.append(" AND a.status IN :statuses ");
		}// end if
		
		return query.toString();
	}// end of method formatQuery

	/**
	 * finding count of total accounts by either accountId, companyName, billingMsisdn, or status.
	 * @author tmohamed
	 */
	@Override
	public long countSearchAccount(String accountID, String companyName, String billingMSISDN,
			List<AccountStatus> statuses) throws DBException
	{
		Long count = 0L;
		try
		{
			boolean countFlag = true;
			String sql = formatQuery(accountID, companyName, billingMSISDN, statuses, countFlag);
			TypedQuery<Long> query = em.createQuery(sql, Long.class);
			
			if(accountID != null && !accountID.trim().isEmpty())
			{
				query.setParameter("accountId", "%" + accountID.toLowerCase() + "%");
			}// end if
			
			if(companyName != null && !companyName.trim().isEmpty())
			{
				query.setParameter("companyName", "%" + companyName.toLowerCase() + "%");
			}// end if
			
			if(billingMSISDN != null && !billingMSISDN.trim().isEmpty())
			{
				query.setParameter("billingMsisdn", "%" + billingMSISDN.toLowerCase() + "%");
			}// end if
			
			if(statuses != null && !statuses.isEmpty())
			{
				query.setParameter("statuses", statuses);
			}// end if
			
			count = query.getSingleResult();
		}// end try
		catch(Exception e)
		{
			throw new DBException(e);
		}
		
		return count.longValue();
	}// end of method countSearchAccount
        
    @Override
    public Account findByIdAndUserNameAndAction(String accountId, String userName, ActionName actionName) throws DBException {
        try {
            TypedQuery<Account> q = em.createNamedQuery(Account.FIND_BY_ID_AND_USER_NAME_AND_ACTION, Account.class).setParameter(
                    Account.ACCOUNT_ID, accountId).setParameter(AccountUser.USERNAME, userName).setParameter(AccountUser.ACTION_NAME, actionName);
            List<Account> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    @Override
    public Account findWithSmsApiByIdAndAction(String accountId, ActionName actionName, int timeOut) throws DBException {
        try {
            TypedQuery<Account> q = em.createNamedQuery(Account.FIND_WITH_SMSAPI_BY_ID_AND_ACTION, Account.class).
                    setParameter(Account.ACCOUNT_ID, accountId).setParameter(AccountUser.ACTION_NAME, actionName).setHint("javax.persistence.query.timeout", timeOut);
            List<Account> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }
    
    @Override
    public Account findWithSmsApiCampByIdAndAction(String accountId, List<ActionName> actionName, int timeOut) throws DBException {
        try {
            TypedQuery<Account> q = em.createNamedQuery(Account.FIND_WITH_SMSAPI_CAMP_BY_ID_AND_ACTION, Account.class).
                    setParameter(Account.ACCOUNT_ID, accountId).setParameter(AccountUser.ACTION_NAME, actionName).setHint("javax.persistence.query.timeout", timeOut);
            List<Account> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }
}// end of class AccountDao
