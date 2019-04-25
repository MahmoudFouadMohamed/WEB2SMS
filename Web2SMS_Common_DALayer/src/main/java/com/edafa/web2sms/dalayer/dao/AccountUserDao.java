package com.edafa.web2sms.dalayer.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;

/**
 * Session Bean implementation class AccountUsersDao
 */
@Stateless
@LocalBean
public class AccountUserDao extends AbstractDao<AccountUser> implements AccountUserDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@EJB
	AccountStatusDaoLocal accountStatusDao;
	
	/**
	 * Default constructor.
	 */
	public AccountUserDao() {
		super(AccountUser.class);
	}

	@Override
	public int countAccountUsers(String acctId, List<AccountStatus> acctStatus) {

		Long result = em.createNamedQuery(AccountUser.COUNT_BY_ACCOUNT_ID, Long.class)
				.setParameter(AccountUser.ACCOUNT_ID, acctId).setParameter(AccountUser.STATUSES, acctStatus)
				.getSingleResult();

		return result.intValue();

	}
	
	@Override
	public int countActiveAccountUsers(String accountId, String search, String groupId) throws DBException {

//		List<AccountStatus> acctStatus = new ArrayList<AccountStatus>();
//		acctStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
//		
//		Long result = em.createNamedQuery(AccountUser.COUNT_BY_ACCOUNT_ID, Long.class)
//				.setParameter(AccountUser.ACCOUNT_ID, acctId).setParameter(AccountUser.STATUSES, acctStatus)
//				.getSingleResult();
//
//		return result.intValue();
            
            			
		Long result;
		try {
			boolean countFlag = true;
			String sql = formatQuery(accountId, search, groupId, countFlag);
			TypedQuery<Long> query = em.createQuery(sql, Long.class);

			query.setParameter("accountId", accountId);

			List<AccountStatus> acctStatus = new ArrayList<AccountStatus>();
			acctStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));

			query.setParameter("statuses", acctStatus);
                        
                        if (groupId != null && !groupId.trim().isEmpty()) {
                            query.setParameter("groupId", groupId);
                        }

			if (search != null && !search.trim().isEmpty()) {
				query.setParameter("username", search.toLowerCase());
			}

			result = query.getSingleResult();

		} // end try
		catch (Exception e) {
			throw new DBException(e);
		} // end catch

		return result.intValue();

	}
	
	@Override
	public List<AccountUser> findAccountUsers(String acctId, List<AccountStatus> acctStatus) {

		List<AccountUser> result = em.createNamedQuery(AccountUser.FIND_BY_ACCOUNT_ID, AccountUser.class)
				.setParameter(AccountUser.ACCOUNT_ID, acctId).setParameter(AccountUser.STATUSES, acctStatus)
				.getResultList();
		return result;

	}

	@Override
	public AccountUser findAccountAdminUser(String acctId, List<AccountStatus> acctStatus) {

		List<AccountUser> result = em.createNamedQuery(AccountUser.FIND_ADMIN_BY_ACCOUNT_ID, AccountUser.class)
				.setParameter(AccountUser.ACCOUNT_ID, acctId).setParameter(AccountUser.STATUSES, acctStatus)
				.getResultList();
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@Override
	public AccountUser findAccountUser(String acctId, String username, List<AccountStatus> acctStatus) {

		List<AccountUser> result = em.createNamedQuery(AccountUser.FIND_BY_ACCOUNT_ID_AND_USERNAME, AccountUser.class)
				.setParameter(AccountUser.ACCOUNT_ID, acctId).setParameter(AccountUser.USERNAME, username)
				.setParameter(AccountUser.STATUSES, acctStatus).getResultList();
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<AccountUser> searchAccountUser(String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN,
			List<AccountStatus> statuses,int first, int max) throws DBException
	{
		List<AccountUser> result = null;
		try
		{
			boolean countFlag = false;
			String sql = formatQuery(userName,accountID, companyName, billingMSISDN, userMSISDN, statuses, countFlag);
			TypedQuery<AccountUser> query = em.createQuery(sql, AccountUser.class);
			
			if(userName != null && !userName.trim().isEmpty())
			{
				query.setParameter("userName", "%" + userName.toLowerCase() + "%");
			}// end if
			
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
                        
                        if(userMSISDN != null && !userMSISDN.trim().isEmpty())
			{
				query.setParameter("userMSISDN", "%" + userMSISDN.toLowerCase() + "%");
			}// end if
			
			if(statuses != null && !statuses.isEmpty())
			{
				query.setParameter("statuses", statuses);
			}// end if
			
			result = query.setMaxResults(max).setFirstResult(first).getResultList();
		}// end try
		catch(Exception e)
		{
			throw e;
		}// end catch
		
		return result;
	}// end of method searchAccount

	@Override
	public long countSearchAccountUser(String userName, String accountID, String companyName
		, String billingMSISDN, String userMSISDN,List<AccountStatus> statuses) throws DBException
	{
		Long count = 0L;
		try
		{
			boolean countFlag = true;
			String sql = formatQuery(userName, accountID, companyName, billingMSISDN, userMSISDN, statuses, countFlag);
			TypedQuery<Long> query = em.createQuery(sql, Long.class);
			
			if(userName != null && !userName.trim().isEmpty())
			{
				query.setParameter("userName", "%" + userName.toLowerCase() + "%");
			}// end if
			
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
			
                        if(userMSISDN != null && !userMSISDN.trim().isEmpty())
			{
				query.setParameter("userMSISDN", "%" + userMSISDN.toLowerCase() + "%");
			}// end if
                        
			if(statuses != null && !statuses.isEmpty())
			{
				query.setParameter("statuses", statuses);
			}// end if
			
			count = query.getSingleResult();
		}// end try
		catch(Exception e)
		{
			throw e;
		}
		
		return count.longValue();
	}// end of method countSearchAccount
	
	private String formatQuery(String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN,
			List<AccountStatus> statuses, boolean countFlag)
	{
		StringBuilder query = new StringBuilder();
		
//		if (countFlag) 
//		{
//			query.append("SELECT COUNT(a) FROM AccountUser a INNER JOIN FETCH a.accountUserLogin INNER JOIN FETCH a.account.accountSendersList INNER JOIN FETCH a.account.intraSendersList WHERE a.accountUserId IS NOT NULL ");
//		}// end if
//		else 
//		{
//			query.append("SELECT a FROM AccountUser a INNER JOIN FETCH a.accountUserLogin INNER JOIN FETCH a.account.accountSendersList INNER JOIN FETCH a.account.intraSendersList WHERE a.accountUserId IS NOT NULL ");
//		}// end else
                
                if (countFlag) 
		{
			query.append("SELECT COUNT(a) FROM AccountUser a LEFT OUTER JOIN FETCH a.accountUserLogin WHERE a.accountUserId IS NOT NULL ");
		}// end if
		else 
		{
			query.append("SELECT a FROM AccountUser a LEFT OUTER JOIN FETCH a.accountUserLogin WHERE a.accountUserId IS NOT NULL ");
		}// end else
		
		if(userName != null && !userName.trim().isEmpty())
		{
			query.append(" AND LOWER(a.username) LIKE :userName ");
		}// end if
		
		if(accountID != null && !accountID.trim().isEmpty())
		{
			query.append(" AND LOWER(a.account.accountId) LIKE :accountId ");
		}// end if
		
		if(companyName != null && !companyName.trim().isEmpty())
		{
			query.append(" AND LOWER(a.account.companyName) LIKE :companyName ");
		}// end if
		
		if(billingMSISDN != null && !billingMSISDN.trim().isEmpty())
		{
			query.append(" AND LOWER(a.account.billingMsisdn) LIKE :billingMsisdn ");
		}// end if
                
                if(userMSISDN != null && !userMSISDN.trim().isEmpty())
		{
			query.append(" AND LOWER(a.phoneNumber) LIKE :userMSISDN ");
		}// end if
		
		if(statuses != null && !statuses.isEmpty())
		{
			query.append(" AND a.account.status IN :statuses ");
		}// end if
		
		return query.toString();
	}// end of method formatQuery

    @Override
    public AccountUser findAccountUserById(String id) throws DBException {
        return find(id);
    }
        

	@Override
	public List<AccountUser> searchActiveAccountUsers(String accountId, String search, String groupId, int first, int max)
			throws DBException {
		List<AccountUser> result = null;
		try {
			boolean countFlag = false;
			String sql = formatQuery(accountId, search, groupId, countFlag);
			TypedQuery<AccountUser> query = em.createQuery(sql, AccountUser.class);

			query.setParameter("accountId", accountId);

			List<AccountStatus> acctStatus = new ArrayList<AccountStatus>();
			acctStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));

			query.setParameter("statuses", acctStatus);
                        
                        if (groupId != null && !groupId.trim().isEmpty()) {
                            query.setParameter("groupId", groupId);
                        }

			if (search != null && !search.trim().isEmpty()) {
				query.setParameter("username", search.toLowerCase());
			}

			query.setFirstResult(first);

			if (max > 0) {
				query.setMaxResults(max);
			}

			result = query.getResultList();

		} // end try
		catch (Exception e) {
			throw new DBException(e);
		} // end catch

		return result;
	}

	private String formatQuery(String accountId, String search, String groupId, boolean countFlag) {
		StringBuilder query = new StringBuilder();

		if (countFlag) {
			query.append(
					"SELECT COUNT(a) FROM AccountUser a");
		} else {
			query.append(
					"SELECT a FROM AccountUser a");
		}
                
                if (groupId != null && !groupId.trim().isEmpty()) {
			query.append(
					" JOIN a.accountGroups ag WHERE a.account.accountId = :accountId AND a.status IN :statuses AND ag.accountGroupId = :groupId");
		} else {
			query.append(
					" WHERE a.account.accountId = :accountId AND a.status IN :statuses");
		}
                
		if (search != null && !search.trim().isEmpty()) {
			query.append(" AND LOWER(a.username) LIKE CONCAT('%',:username,'%') ");
		} 

		query.append(" ORDER BY a.username ASC ");

		return query.toString();
	}

}// end of class AccountUserDao