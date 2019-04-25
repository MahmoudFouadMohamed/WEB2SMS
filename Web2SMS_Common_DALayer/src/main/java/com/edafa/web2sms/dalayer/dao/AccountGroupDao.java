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
import com.edafa.web2sms.dalayer.dao.interfaces.AccountGroupDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.constants.AccountGroupConst;

/**
 * Session Bean implementation class AccountGroupDao
 */
@Stateless
@LocalBean
public class AccountGroupDao extends AbstractDao<AccountGroup> implements AccountGroupDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@EJB
	AccountStatusDaoLocal accountStatusDao;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public AccountGroupDao() {
		super(AccountGroup.class);
	}

	@Override
	public List<AccountGroup> findAccountGroups(String acctId) {
		List<AccountGroup> result = em.createNamedQuery(AccountGroup.FIND_BY_ACCOUNT_ID, AccountGroup.class)
				.setParameter(AccountGroup.ACCOUNT_ID, acctId).getResultList();
		if (result != null && result.size() > 0) {
			return result;
		} else {
			return null;
		}
	}

	@Override
	public AccountGroup findAccountGroup(String acctId, String groupName) {
		List<AccountGroup> result = em
				.createNamedQuery(AccountGroup.FIND_BY_ACCOUNT_ID_AND_GROUP_NAME, AccountGroup.class)
				.setParameter(AccountGroup.ACCOUNT_ID, acctId).setParameter(AccountGroup.GROUP_NAME, groupName)
				.getResultList();
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@Override
	public AccountGroup findAccountDefaultGroup(String accountId) {
		List<AccountGroup> result = em
				.createNamedQuery(AccountGroup.FIND_BY_ACCOUNT_ID_AND_DEFAULT_FLAG, AccountGroup.class)
				.setParameter(AccountGroup.ACCOUNT_ID, accountId).setParameter(AccountGroup.DEFAULT_FLAG, true)
				.getResultList();
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@Override
	public AccountGroup findAccountGroup(String groupId) {
		List<AccountGroup> result = em.createNamedQuery(AccountGroup.FIND_BY_ACCOUNT_GROUP_ID, AccountGroup.class)
				.setParameter(AccountGroup.ACCOUNT_GROUP_ID, groupId).getResultList();
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<AccountUser> findAccountGroupUsers(String acctId, String groupName) {
		List<AccountGroup> result = em
				.createNamedQuery(AccountGroup.FIND_BY_ACCOUNT_ID_AND_GROUP_NAME, AccountGroup.class)
				.setParameter(AccountGroup.ACCOUNT_ID, acctId).setParameter(AccountGroup.GROUP_NAME, groupName)
				.getResultList();
		if (result != null && result.size() > 0) {
			AccountGroup ag = result.get(0);
			return ag.getAccountUsers();
		} else {
			return null;
		}
	}

	@Override
	public void addUserToGroup(String acctId, String groupName, String userName) throws DBException {
		AccountGroup accountGroup = em
				.createNamedQuery(AccountGroup.FIND_BY_ACCOUNT_ID_AND_GROUP_NAME, AccountGroup.class)
				.setParameter(AccountGroup.ACCOUNT_ID, acctId).setParameter(AccountGroup.GROUP_NAME, groupName)
				.getSingleResult();
		if (accountGroup != null) {
			try {
				List<AccountStatus> statuses = new ArrayList<>();
				statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));

				AccountUser user = em.createNamedQuery(AccountUser.FIND_BY_ACCOUNT_ID_AND_USERNAME, AccountUser.class)
						.setParameter(AccountUser.ACCOUNT_ID, acctId).setParameter(AccountUser.USERNAME, userName)
						.setParameter(AccountUser.STATUSES, statuses).getSingleResult();

				accountGroup.getAccountUsers().add(user);
				edit(accountGroup);
			} catch (Exception e) {
				throw new DBException(e);
			}
		} else {
			throw new DBException("Group not exist");
		}
	}

	@Override
	public void removeUserFromGroup(String acctId, String groupName, String userName) throws DBException {
		//		int deletedCount = em.createNamedQuery(AccountGroup.DELETE_USER_FROM_GROUP, AccountGroup.class)
		//				.setParameter(AccountGroup.ACCOUNT_ID, acctId).setParameter(AccountGroup.GROUP_NAME, groupName).executeUpdate();
		//		
	}

	@Override
	public void addUsersToGroup(String accountId, AccountGroup accountGroup, List<AccountUser> accountUsers)
			throws DBException {

		if (accountGroup != null) {
			try {
				accountGroup.getAccountUsers().addAll(accountUsers);
				edit(accountGroup);
			} catch (Exception e) {
				throw new DBException(e);
			}
		} else {
			throw new DBException("Group not exist");
		}
	}

	@Override
	public void removeUsersFromGroup(String accountId, AccountGroup accountGroup, List<AccountUser> accountUsers)
			throws DBException {

		if (accountGroup != null) {
			try {
				accountGroup.getAccountUsers().removeAll(accountUsers);
				edit(accountGroup);
			} catch (Exception e) {
				throw new DBException(e);
			}
		} else {
			throw new DBException("Group not exist");
		}

	}

	@Override
	public int countAccountGroups(String accountId, String groupName, String userName) throws DBException {

		//		Long result = em.createNamedQuery(AccountGroup.COUNT_BY_ACCOUNT_ID, Long.class)
		//				.setParameter(AccountGroup.ACCOUNT_ID, accountId).getSingleResult();
		//
		//		return result.intValue();
		Long result;
		try {
			boolean countFlag = true;
			String sql = formatQuery(accountId, groupName, userName, countFlag);
			TypedQuery<Long> query = em.createQuery(sql, Long.class);

			query.setParameter("accountId", accountId);

			if (userName != null && !userName.trim().isEmpty()) {
				query.setParameter("userName", userName);
			}
			if (groupName != null && !groupName.trim().isEmpty()) {
				query.setParameter("groupName", groupName.toLowerCase());
			}

			result = query.getSingleResult();

		} // end try
		catch (Exception e) {
			throw new DBException(e);
		} // end catch

		return result.intValue();
	}

	@Override
	public AccountUser findAccountGroupAdmin(String accountId, String groupId) throws DBException {

		try {
			AccountGroup group = findAccountGroup(groupId);
			AccountGroup adminsGroup = findAccountGroup(accountId, AccountGroupConst.GROUPS_ADMINS_GROUP_NAME);

			AccountUser groupAdmin = null;
			for (AccountUser user : group.getAccountUsers()) {
				if (adminsGroup.getAccountUsers().contains(user)) {
					groupAdmin = user;
				}
			}

			return groupAdmin;
		} // end try
		catch (Exception e) {
			throw new DBException(e);
		} // end catch

	}

	@Override
	public void removeAccountGroupAdmins(String accountId, List<AccountUser> users) throws DBException {

		try {
			AccountGroup adminsGroup = findAccountGroup(accountId, AccountGroupConst.GROUPS_ADMINS_GROUP_NAME);
			List<AccountUser> groupsAdmins = adminsGroup.getAccountUsers();

			if (adminsGroup != null && users != null) {
				for (AccountUser user : users) {
					if (groupsAdmins.contains(user)) {
						groupsAdmins.remove(user);
					}
				}
			}

			edit(adminsGroup);

		} // end try
		catch (Exception e) {
			throw new DBException(e);
		} // end catch

	}

	public List<AccountGroup> searchGroups(String accountId, String groupName, String userName, int first, int max)
			throws DBException {
		List<AccountGroup> result = null;
		try {
			boolean countFlag = false;
			String sql = formatQuery(accountId, groupName, userName, countFlag);
			TypedQuery<AccountGroup> query = em.createQuery(sql, AccountGroup.class);

			query.setParameter("accountId", accountId);

			if (userName != null && !userName.trim().isEmpty()) {
				query.setParameter("userName", userName);
			}

			if (groupName != null && !groupName.trim().isEmpty()) {
				query.setParameter("groupName", groupName.toLowerCase());
			}

			query.setFirstResult(first);

			if (max > 0) {
				query.setMaxResults(max).getResultList();
			}

			result = query.getResultList();

		} // end try
		catch (Exception e) {
			throw new DBException(e);
		} // end catch

		return result;
	}

	private String formatQuery(String accountId, String groupName, String userName, boolean countFlag) {
		StringBuilder query = new StringBuilder();

		if (countFlag) {
			query.append("SELECT COUNT(ag) FROM AccountGroup ag");
		} else {
			query.append("SELECT ag FROM AccountGroup ag");
		}

		if (userName != null && !userName.trim().isEmpty()) {
			query.append(
					" JOIN ag.accountUsers au WHERE ag.account.accountId = :accountId AND ag.hiddenFlag = FALSE AND au.username = :userName");
		} else {
			query.append(" WHERE ag.account.accountId = :accountId AND ag.hiddenFlag = FALSE ");
		}

		if (groupName != null && !groupName.trim().isEmpty()) {
			query.append(" AND LOWER(ag.groupName) LIKE CONCAT('%',:groupName,'%') ");
		}

		query.append(" ORDER BY ag.groupName ASC ");

		return query.toString();
	}
}