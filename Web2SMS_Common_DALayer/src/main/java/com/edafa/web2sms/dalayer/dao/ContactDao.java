package com.edafa.web2sms.dalayer.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.ListContactPK;
import com.edafa.web2sms.dalayer.model.SMSStatus;

/**
 * Session Bean implementation class Contact
 */
@Stateless
// @ApplicationException(rollback = false)
public class ContactDao extends AbstractDao<Contact> implements ContactDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public ContactDao() {
		super(Contact.class);
	}

	@Override
	public List<Contact> findByListID(int listId) throws DBException {
		try {
			return em.createNamedQuery(Contact.FIND_BY_LIST_ID, Contact.class).setParameter(Contact.LIST_ID, listId)
					.getResultList();

		} catch (Exception e) {
			throw new DBException(e.getCause());
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int copyList(int newListId, int oldListId) throws DBException {
		try {
			Query q = em.createNamedQuery(Contact.COPY_LIST).setParameter(1, newListId).setParameter(2, oldListId);
			return q.executeUpdate();

		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Contact> findByListID(int listId, int first, int max) throws DBException {
		try {
			List<Contact> contacts = null;
			TypedQuery<Contact> query = em.createNamedQuery(Contact.FIND_BY_LIST_ID, Contact.class)
					.setParameter(Contact.LIST_ID, listId).setFirstResult(first);
			if (max != -1) {
				query = query.setMaxResults(max).setFlushMode(FlushModeType.COMMIT);
			}

			contacts = query.getResultList();

			em.flush();
			em.clear();

			return contacts;

		} catch (Exception e) {
			throw new DBException(e.getCause());
		}
	}

	@Override
	public List<Contact> findContactsByCampaignIdAndStatus(String campaignId, List<SMSStatus> status)
			throws DBException {
		try {
			TypedQuery<Contact> q = em.createNamedQuery(Contact.FIND_CONTACT_BY_CAMPAIGN_ID_AND_STATUS, Contact.class)
					.setParameter(Contact.CAMPAIGN_ID, campaignId).setParameter(Contact.STATUS_LIST, status);

			List<Contact> logs = q.getResultList();
			em.flush();
			em.clear();
			return logs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Asynchronous
	public Future<Integer> create(List<Contact> list) {
		int count = 0;
		try {
			Iterator<Contact> contactItr = list.iterator();
			while (contactItr.hasNext()) {
				em.persist(contactItr.next());
				count++;
			}

		} catch (Exception exception) {
			logger.info("Exception while updating transactionLog in DB." + exception);
			exception.printStackTrace();

			if (exception instanceof ConstraintViolationException) {
				ConstraintViolationException constraintException = (ConstraintViolationException) exception;

				for (Iterator<ConstraintViolation<?>> it = constraintException.getConstraintViolations().iterator(); it
						.hasNext();) {
					ConstraintViolation<? extends Object> v = it.next();
					logger.info("Constraint Violation Message" + ": " + v + "," + v.getMessage());
				}
			}
			count = -1;
		}
//		System.out.println(count);
		return new AsyncResult<Integer>(count);
	}

	@Override
	public long countContactInList(Integer listId) throws DBException {
		try {
			Long count = em.createNamedQuery(Contact.COUNT_BY_LIST_ID, Long.class)
					.setParameter(Contact.LIST_ID, listId).getSingleResult();

			if (count != null)
				return count.intValue();
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Contact> searchContacts(String contact, Integer listId) throws DBException {
		return em.createNamedQuery(Contact.SEARCH_LIST, Contact.class).setParameter(Contact.LIST_ID, listId)
				.setParameter(Contact.MSISDN, "%" + contact + "%")
				.setParameter(Contact.FIRST_NAME, "%" + contact + "%").getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer isContactExist(String msisdn, int listId) throws DBException {

		try {
			Long count = em.createNamedQuery(Contact.COUNT_BY_MSISDN_AND_LIST_ID, Long.class)
					.setParameter(Contact.MSISDN, msisdn).setParameter(Contact.LIST_ID, listId).getSingleResult();

			if (count != null)
				return count.intValue();
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int updateContact(Contact contact, String msisdn) throws DBException {
		return em.createNamedQuery(Contact.UPDATE_CONTACT, Integer.class).setParameter(Contact.OLD_MSISDN, msisdn)
				.setParameter(Contact.LIST_ID, contact.getListContactsPK().getListId())
				.setParameter(Contact.MSISDN, contact.getListContactsPK().getMsisdn())
				.setParameter(Contact.FIRST_NAME, contact.getFirstName())
				.setParameter(Contact.LAST_NAME, contact.getLastName())
				.setParameter(Contact.VALUE_1, contact.getValue1()).setParameter(Contact.VALUE_2, contact.getValue2())
				.setParameter(Contact.VALUE_3, contact.getValue3()).setParameter(Contact.VALUE_4, contact.getValue4())
				.setParameter(Contact.VALUE_5, contact.getValue5()).executeUpdate();
	}

	@Override
	@Asynchronous
	public Future<Integer> removeByListId(Integer listId) throws DBException {
		int rowNum = 0;
		try {
			rowNum = em.createNamedQuery(Contact.REMOVE_BY_LIST_ID).setParameter(Contact.LIST_ID, listId)
					.executeUpdate();

		} catch (Exception e) {
			throw new DBException(e);
		}
		return new AsyncResult<Integer>(rowNum);
	}

	@Override
	public List<Contact> findDiffContancts(Integer newSubListId, Integer internalListId) throws DBException {
		List<Contact> contacts = new ArrayList<Contact>();
		try {
			Query q = em.createNamedQuery(Contact.COMPARE_LISTS_BY_LIST_IDs).setParameter(1, newSubListId)
					.setParameter(2, internalListId);
			List<String> msisdns = q.getResultList();

			if (msisdns != null)
				for (int i = 0; i < msisdns.size(); i++) {
					Contact e = new Contact();
					ListContactPK listpk = new ListContactPK();
					listpk.setMsisdn(msisdns.get(i));
					e.setListContactsPK(listpk);
					contacts.add(e);
				}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
		return contacts;
	}

	// @Override
	// public int deteteContactsFromList(Integer listId, List<Contact>
	// contactsToRemove) throws DBException {
	// int num;
	// try {
	// Query q =
	// em.createNamedQuery(Contact.DELETE_CONTACTS_FROM_LIST).setParameter(1,
	// listId)
	// .set(2, contactsToRemove);
	// num = q.executeUpdate();
	// System.out.println("num of rows deleted from list: " + num);
	// return num;
	// } catch (Exception e) {
	//
	// throw new DBException(e);
	// }
	// }

	@Override
	public int findDifferenceAndDelete(Integer newSubListId, Integer internalListId) throws DBException {
		int numOfUpdatedRows;
		try {
			Query q = em.createNamedQuery(Contact.COMPARE_AND_DELETE_DIFF).setParameter(1, newSubListId)
					.setParameter(2, newSubListId).setParameter(3, internalListId);
			numOfUpdatedRows = q.executeUpdate();
			return numOfUpdatedRows;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(e);
		}
	}

}
