/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactListDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListType;

/**
 * 
 * @author yyaseen
 */
@Stateless
public class ContactListDao extends AbstractDao<ContactList> implements ContactListDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ContactListDao() {
		super(ContactList.class);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createUsingNewTx(ContactList entity) throws DBException {

		super.create(entity);

	}

	@Override
	public ContactList findByListName(String name) throws DBException {
		ContactList contactList = null;
		try {
			List<ContactList> contactLists = em.createNamedQuery(ContactList.FIND_BY_LIST_NAME, ContactList.class)
					.setParameter(ContactList.LIST_NAME, name).getResultList();
			if (contactLists != null && !contactLists.isEmpty())
				contactList = contactLists.get(0);

			// System.out.println("findlist by name result size: "+
			// contactLists.size() );
			// System.out.println("contactLists.get(0) " + contactLists.get(0));
		} catch (Exception e) {
			throw new DBException(e);
		}
		return contactList;
	}

	@Override
	public List<ContactList> findByListIds(List<Integer> listIds) throws DBException {
		List<ContactList> contactLists = null;
		try {
			contactLists = em.createNamedQuery(ContactList.FIND_BY_LIST_IDS, ContactList.class)
					.setParameter(ContactList.LIST_IDS, listIds).getResultList();

		} catch (Exception e) {
			throw new DBException(e);
		}
		return contactLists;
	}

	@Override
	public ContactList findByListId(int listId) throws DBException {
		ContactList contactList = null;
		try {
			List<ContactList> contactLists = em.createNamedQuery(ContactList.FIND_BY_LIST_ID, ContactList.class)
					.setParameter(ContactList.LIST_ID, listId).getResultList();
			if (contactLists != null && !contactLists.isEmpty())
				contactList = contactLists.get(0);

		} catch (Exception e) {
			throw new DBException(e);
		}
		return contactList;
	}

	@Override
	public ContactList findByListIdAndAccountId(int listId, String accountId) throws DBException {
		ContactList contactList = null;
		try {
			List<ContactList> contactLists = em
					.createNamedQuery(ContactList.FIND_BY_LIST_ID_AND_ACCOUNT_ID, ContactList.class)
					.setParameter(ContactList.LIST_ID, listId).setParameter(ContactList.ACCOUNT_ID, accountId)
					.getResultList();
			if (contactLists != null && !contactLists.isEmpty())
				contactList = contactLists.get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return contactList;
	}

	@Override
	public int countByListIdAndAccountId(String listId, String accountId) throws DBException {
		int contactListsCount = 0;
		try {
			contactListsCount = em.createNamedQuery(ContactList.COUNT_BY_LIST_ID_AND_ACCOUNT_ID, Integer.class)
					.setParameter(ContactList.LIST_ID, listId).setParameter(ContactList.ACCOUNT_ID, accountId)
					.getResultList().get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return contactListsCount;
	}

	@Override
	public int countByListNameAndAccountId(String listName, String accountId) throws DBException {
		Long contactListsCount;
		try {
			contactListsCount = em.createNamedQuery(ContactList.COUNT_BY_LIST_NAME_AND_ACCOUNT_ID, Long.class)
					.setParameter(ContactList.LIST_NAME, listName).setParameter(ContactList.ACCOUNT_ID, accountId)
					.getSingleResult();
			if (contactListsCount != null)
				return contactListsCount.intValue();
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public int updateListName(int listId, String listName, String accountId) throws DBException {
		int updateResult = 0;
		try {
			updateResult = em.createNamedQuery(ContactList.UPDATE_LIST_NAME).setParameter(ContactList.LIST_ID, listId)
					.setParameter(ContactList.LIST_NAME, listName).setParameter(ContactList.ACCOUNT_ID, accountId)
					.executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return updateResult;
	}

	@Override
	public String getListIdByName(String listName) throws DBException {
		String listId = null;
		try {
			List<String> resultList = em.createNamedQuery(ContactList.GET_LIST_ID_BY_NAME, String.class)
					.setParameter(ContactList.LIST_NAME, listName).getResultList();

			if (!resultList.isEmpty()) {
				listId = resultList.get(0);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		return listId;
	}

	@Override
	public List<ContactList> findByAccountId(String accountId) throws DBException {
		try {
			return em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> findByAccountIdAndType(String accountId, ListType listType) throws DBException {
		try {
			// return
			// em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE_WITH_COUNTS,
			// ContactList.class)
			return em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPE, listType)
					.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	// @Override
	// public List<ContactList> findMSISDNsByAccountIdAndType(String accountId,
	// ListType listType) throws DBException {
	// try {
	// // return
	// //
	// em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE_WITH_COUNTS,
	// // ContactList.class)
	// return
	// em.createNamedQuery(ContactList.FIND_MSISDNs_BY_ACCOUNT_ID_AND_TYPE,
	// ContactList.class)
	// .setParameter(ContactList.ACCOUNT_ID,
	// accountId).setParameter(ContactList.LIST_TYPE, listType)
	// .getResultList();
	// } catch (Exception e) {
	// throw new DBException(e);
	// }
	// }

	@Override
	public List<ContactList> findByAccountIdAndTypes(String accountId, List<ListType> listTypes) throws DBException {
		try {
			// return
			// em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES_WITH_COUNTS,
			// ContactList.class)
			return em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPES, listTypes)
					.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ContactList> findByAccountIdAndType(String accountId, ListType listType, int first, int max)
			throws DBException {
		List<ContactList> resultSet = null;
		try {
			// resultSet =
			// em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE_WITH_COUNTS,
			// ContactList.class)
			resultSet = em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPE, listType)
					.setFirstResult(first).setMaxResults(max).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return resultSet;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ContactList> findByAccountIdAndTypes(String accountId, List<ListType> listTypes, int first, int max)
			throws DBException {
		List<ContactList> resultSet = null;
		try {
			// resultSet =
			// em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES_WITH_COUNTS,
			// ContactList.class)
			resultSet = em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPES, listTypes)
					.setFirstResult(first).setMaxResults(max).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return resultSet;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int counByAccountIdAndType(String accountId, ListType listType) throws DBException {
		Long result;
		try {
			result = em.createNamedQuery(ContactList.COUNT_BY_ACCOUNT_ID_AND_TYPE, Long.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPE, listType)
					.getSingleResult();

		} catch (Exception e) {
			throw new DBException(e);
		}
		if (result != null) {

			return result.intValue();
		}
		return 0;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int counByAccountIdAndTypes(String accountId, List<ListType> listTypes) throws DBException {
		Long result;
		try {
			result = em.createNamedQuery(ContactList.COUNT_BY_ACCOUNT_ID_AND_TYPES, Long.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPES, listTypes)
					.getSingleResult();

		} catch (Exception e) {
			throw new DBException(e);
		}
		if (result != null) {

			return result.intValue();
		}
		return 0;
	}

	@Override
	public List<ContactList> findListsByCampaignId(String campId) throws DBException {
		try {
			TypedQuery<ContactList> q = em.createNamedQuery(CampaignLists.FIND_LISTS_BY_CAMPAIGN_ID_ORDERED,
					ContactList.class).setParameter(CampaignLists.CAMPAIGN_ID, campId);
			List<ContactList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ContactList> findByAccountIdAndTypeWithCounts(String accountId, ListType listType) throws DBException {
		try {
			// return
			// em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE_WITH_COUNTS,
			// ContactList.class)
			return em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPE, listType)
					.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ContactList> findByAccountIdAndTypesWithCounts(String accountId, List<ListType> listTypes)
			throws DBException {
		try {
			// return
			// em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES_WITH_COUNTS,
			// ContactList.class)
			return em.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPES, listTypes)
					.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ContactList> findByAccountIdAndTypeWithCounts(String accountId, ListType listType, int first, int max)
			throws DBException {
		List<ContactList> resultSet = null;
		try {
			TypedQuery<ContactList> q = em
					// .createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE_WITH_COUNTS,
					// ContactList.class)
					.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPE, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPE, listType)
					.setFirstResult(first).setMaxResults(max);
//			System.out.println(q);
			resultSet = q.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return resultSet;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ContactList> findByAccountIdAndTypesWithCounts(String accountId, List<ListType> listTypes, int first,
			int max) throws DBException {
		List<ContactList> resultSet = null;
		try {
			TypedQuery<ContactList> q = em
					// .createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES_WITH_COUNTS,
					// ContactList.class)
					.createNamedQuery(ContactList.FIND_BY_ACCOUNT_ID_AND_TYPES, ContactList.class)
					.setParameter(ContactList.ACCOUNT_ID, accountId).setParameter(ContactList.LIST_TYPES, listTypes)
					.setFirstResult(first).setMaxResults(max);
			resultSet = q.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return resultSet;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long countContacts(List<Integer> lists) throws DBException {
		try {
			Long count = em.createNamedQuery(ContactList.COUNT_CONTACTS_IN_LISTS, Long.class)
					.setParameter(ContactList.LIST_IDS, lists).getSingleResult();
			if (count != null)
				return count;
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long countContacts(Integer listId) throws DBException {
		try {
			Long count = em.createNamedQuery(ContactList.COUNT_CONTACTS_IN_LIST, Long.class)
					.setParameter(ContactList.LIST_ID, listId).getSingleResult();

			return count.intValue();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> searchByListNameAndType(String listName, ListType listType, String accountId)
			throws DBException {
		try {
			return em.createNamedQuery(ContactList.SEARCH_BY_LIST_NAME_AND_TYPE, ContactList.class)
					.setParameter(ContactList.LIST_TYPE, listType).setParameter(ContactList.ACCOUNT_ID, accountId)
					.setParameter(ContactList.LIST_NAME, "%" + listName.toLowerCase() + "%").getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> searchByListNameAndTypeWithCounts(String listName, List<ListType> listType,
			String accountId) throws DBException {
		try {
			// return
			// em.createNamedQuery(ContactList.SEARCH_BY_LIST_NAME_AND_TYPE_WITH_COUNTS,
			// ContactList.class)
			return em.createNamedQuery(ContactList.SEARCH_BY_LIST_NAME_AND_TYPE, ContactList.class)
					.setParameter(ContactList.LIST_TYPE, listType).setParameter(ContactList.ACCOUNT_ID, accountId)
					.setParameter(ContactList.LIST_NAME, "%" + listName.toLowerCase() + "%").getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@Asynchronous
	public Future<Integer> removeByListId(Integer listId) throws DBException {
		int rowNum = 0;
		try {
			rowNum = em.createNamedQuery(ContactList.REMOVE_BY_LIST_ID).setParameter(ContactList.LIST_ID, listId)
					.executeUpdate();

		} catch (Exception e) {
			throw new DBException(e);
		}
		return new AsyncResult<Integer>(rowNum);
	}

	@Override
	public ContactList findByListNameAndAccountId(String listName, String accountId) throws DBException {
		ContactList contactList = null;
		try {
			List<ContactList> contactLists = em
					.createNamedQuery(ContactList.FIND_BY_LIST_NAME_AND_ACCOUNT_ID, ContactList.class)
					.setParameter(ContactList.LIST_NAME, listName).setParameter(ContactList.ACCOUNT_ID, accountId)
					.getResultList();
			if (contactLists != null && !contactLists.isEmpty())
				contactList = contactLists.get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return contactList;
	}

	// NOTE: this method return only first occurrence of list name with given
	// account id, depends on uniqueness lists' name condition.
	@Override
	public Integer findByListNameAndAccountIdNativeSql(String listName, String accountId) throws DBException {
		Integer listId;
		try {
			//select list_id from lists where list_name=? and account_id= ?
			Query q =  em.createNamedQuery(ContactList.FIND_BY_LIST_NAME_AND_ACCOUNT_ID_SQL)
					.setParameter(1, listName).setParameter(2, accountId);
			
			 BigDecimal result =(BigDecimal) q.getSingleResult();
			listId = result.intValue();
		} catch (Exception e) {
//			e.printStackTrace();
			throw new DBException(e);
		}
		return listId;
	}

}
