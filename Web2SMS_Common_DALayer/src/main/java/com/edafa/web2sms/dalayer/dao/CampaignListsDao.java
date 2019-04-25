package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.dalayer.pojo.CampaignAssociatedList;

/**
 * Session Bean implementation class CampaignListsDao
 */
@Stateless
public class CampaignListsDao extends AbstractDao<CampaignLists> implements CampaignListsDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public CampaignListsDao() {
		super(CampaignLists.class);
	}

	@Override
	public int countSubmittedSMSInLists(String campaignId) throws DBException {
		try {
			TypedQuery<Long> q = em.createNamedQuery(CampaignLists.COUNT_SUBMITTED_SMS_IN_LISTS, Long.class)
					.setParameter(CampaignLists.CAMPAIGN_ID, campaignId);
			Long result = q.getSingleResult();
			if (result != null)
				return result.intValue();
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public void resetSubmittedSMSInLists(String campaignId) throws DBException {
		try {
			Query q = em.createNamedQuery(CampaignLists.RESET_SUBMITTED_SMS_IN_LISTS).setParameter(
					CampaignLists.CAMPAIGN_ID, campaignId);
			q.executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public boolean isListAssociatedToCampaignStatus(Integer listId, List<CampaignStatus> statusList) throws DBException {

		try {
			return em.createNamedQuery(CampaignLists.IS_IN_ACTIVE_CAMPAIGN, Long.class)
					.setParameter(CampaignLists.LIST_ID, listId).setParameter(CampaignLists.STATUS_LIST, statusList)
					.getSingleResult().intValue() > 0;
		} catch (Exception e) {

			throw new DBException(e);
		}
	}

	@Override
	public List<CampaignLists> findSubmittableByCampaignIdOrdered(String campId) throws DBException {
		try {
			TypedQuery<CampaignLists> q = em.createNamedQuery(CampaignLists.FIND_SUBMITTABLE_BY_CAMP_ID_ORDERED,
					CampaignLists.class).setParameter(CampaignLists.CAMPAIGN_ID, campId);
			List<CampaignLists> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	
	@Override
	public List<CampaignAssociatedList> findListNameByCampId(List<String> campIdList, List<ListType> listTypes) throws DBException {
		try {
			TypedQuery<CampaignAssociatedList> q = em.createNamedQuery(CampaignLists.FIND_LIST_NAME_BY_CAMPAIGN_ID, CampaignAssociatedList.class)
					.setParameter(CampaignLists.LIST_TYPES, listTypes)
					.setParameter(CampaignLists.CAMP_ID_LIST, campIdList);
			List<CampaignAssociatedList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
	
	
	@Override
	public List<CampaignLists> findByCampaignIdOrdered(String campId) throws DBException {
		try {
			TypedQuery<CampaignLists> q = em.createNamedQuery(CampaignLists.FIND_BY_CAMPAIGN_ID_ORDERED,
					CampaignLists.class).setParameter(CampaignLists.CAMPAIGN_ID, campId);
			List<CampaignLists> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> findListsByCampaignIdAndAccountId(String campId, String accountId) throws DBException {
		try {
			TypedQuery<ContactList> q = em
					.createNamedQuery(CampaignLists.FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_ORDERED, ContactList.class)
					.setParameter(CampaignLists.CAMPAIGN_ID, campId).setParameter(CampaignLists.ACCOUNT_ID, accountId);
			List<ContactList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> findListsByCampaignIdAndAccountIdWithCounts(String campId, String accountId)
			throws DBException {
		try {
			TypedQuery<ContactList> q = em
					.createNamedQuery(CampaignLists.FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_ORDERED_WITH_COUNTS,
							ContactList.class).setParameter(CampaignLists.CAMPAIGN_ID, campId)
					.setParameter(CampaignLists.ACCOUNT_ID, accountId);
			List<ContactList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> findListsByCampaignId(String campaignId) throws DBException {
		try {
			TypedQuery<ContactList> q = em.createNamedQuery(CampaignLists.FIND_LISTS_BY_CAMPAIGN_ID_ORDERED,
					ContactList.class).setParameter(CampaignLists.CAMPAIGN_ID, campaignId);
			List<ContactList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> findListsByCampaignIdWithCounts(String campaignId) throws DBException {
		try {
			TypedQuery<ContactList> q = em.createNamedQuery(
					CampaignLists.FIND_LISTS_BY_CAMPAIGN_ID_ORDERED_WITH_COUNTS, ContactList.class).setParameter(
					CampaignLists.CAMPAIGN_ID, campaignId);
			List<ContactList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateExecutionInfo(List<CampaignLists> campaignLists) throws DBException {
		Query q;
		try {
			for (CampaignLists campaignList : campaignLists) {
				q = em.createNamedQuery(CampaignLists.UPDATE_EXECUTION_INFO)
						.setParameter(CampaignLists.LIST_ID, campaignList.getCampaignListsPK().getListId())
						.setParameter(CampaignLists.CAMPAIGN_ID, campaignList.getCampaignListsPK().getCampaignId())
						.setParameter(CampaignLists.SUBMITTED_SMS_COUNT, campaignList.getSubmittedSMSCount())
						.setParameter(CampaignLists.TOTAL_SUBMITTED_SMS_COUNT, campaignList.getTotalSubmittedSMSCount());
				q.executeUpdate();
			}
		} catch (Exception e) {
			throw new DBException(e);
		}

	}

	@Override
	public List<ContactList> findListsByCampaignIdAndAccountId(String campId, String accountId, List<ListType> listTypes)
			throws DBException {
		try {
			TypedQuery<ContactList> q = em
					.createNamedQuery(CampaignLists.FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_AND_TYPES_ORDERED,
							ContactList.class).setParameter(CampaignLists.CAMPAIGN_ID, campId)
					.setParameter(CampaignLists.ACCOUNT_ID, accountId)
					.setParameter(CampaignLists.LIST_TYPES, listTypes);
			List<ContactList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> findListsByCampaignIdAndAccountIdWithCounts(String campId, String accountId,
			List<ListType> listTypes) throws DBException {
		try {
			TypedQuery<ContactList> q = em
					.createNamedQuery(
							CampaignLists.FIND_LISTS_BY_CAMPAIGN_ID_AND_ACCT_ID_AND_TYPES_ORDERED_WITH_COUNTS,
							ContactList.class).setParameter(CampaignLists.CAMPAIGN_ID, campId)
					.setParameter(CampaignLists.ACCOUNT_ID, accountId)
					.setParameter(CampaignLists.LIST_TYPES, listTypes);
			List<ContactList> resultList = q.getResultList();
			return resultList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ContactList> findListsByCampaignId(String campaignId, List<ListType> listTypes) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ContactList> findListsByCampaignIdWithCounts(String campaignId, List<ListType> listTypes)
			throws DBException {
		// TODO Auto-generated method stub
		return null;
	}
}
