package com.edafa.web2sms.dalayer.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignExecutionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignExecution;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.pojo.CampSMSStats;
import com.edafa.web2sms.dalayer.pojo.CampaignFrequency;

/**
 * Session Bean implementation class CampaignDao
 */
@Stateless
public class CampaignDao extends AbstractDao<Campaign> implements CampaignDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@EJB
	CampaignExecutionDaoLocal campaignExecutionDao;

	@EJB
	CampaignListsDaoLocal campaignListsDao;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public CampaignDao() {
		super(Campaign.class);
	}

	@Override
	public Campaign findByName(String name) throws DBException {
		try {

			List<Campaign> campaigns = em.createNamedQuery(Campaign.FIND_BY_NAME, Campaign.class)
					.setParameter(Campaign.NAME, name).getResultList();
			if (!campaigns.isEmpty())
				return campaigns.get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	public Campaign findByIdAndAccountId(String accountId, String campaignId, boolean lock) throws DBException {
		try {
			TypedQuery<Campaign> q = em.createNamedQuery(Campaign.FIND_BY_ID_AND_ACCOUNT_ID, Campaign.class)
					.setParameter(Campaign.ACCOUNT_ID, accountId).setParameter(Campaign.CAMPAIGN_ID, campaignId);

			if (lock)
				q.setLockMode(LockModeType.PESSIMISTIC_READ);

			List<Campaign> campaigns = q.getResultList();
			if (!campaigns.isEmpty())
				return campaigns.get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	public List<CampSMSStats> findCampSMSStats(List<String> campIdList) throws DBException {
		try {
			TypedQuery<CampSMSStats> q = em.createNamedQuery(Campaign.FIND_CAMP_SMS_STATS, CampSMSStats.class)
					.setParameter(Campaign.CAMPAIGN_IDS, campIdList).setParameter(Campaign.CAMPAIGN_IDS, campIdList);
			List<CampSMSStats> campaignSMSStatsList = q.getResultList();
			if (!campaignSMSStatsList.isEmpty())
				return campaignSMSStatsList;
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	public List<CampaignFrequency> findCampFreq(List<String> campIdList) throws DBException {
		try {
			/*
			 * to avoid 
			 * java.sql.SQLSyntaxErrorException: 
			 * ORA-01795: maximum number of expressions in a list is 1000
			 */
			if(campIdList != null && campIdList.size() <= 1000){
			TypedQuery<CampaignFrequency> q = em
					.createNamedQuery(Campaign.FIND_CAMP_FREQUENCY, CampaignFrequency.class).setParameter(
							Campaign.CAMPAIGN_IDS, campIdList);
			List<CampaignFrequency> campaignSMSStatsList = q.getResultList();
			if (!campaignSMSStatsList.isEmpty())
				return campaignSMSStatsList;
			} else if(campIdList != null && campIdList.size() <= 1000){
				List<CampaignFrequency> campaignSMSStatsList = new ArrayList<>();
				for (int start = 0; start <= campIdList.size() ; start += 1000){
					TypedQuery<CampaignFrequency> q = em
							.createNamedQuery(Campaign.FIND_CAMP_FREQUENCY, CampaignFrequency.class).setParameter(
									Campaign.CAMPAIGN_IDS, campIdList.subList(start, start+1000));
					campaignSMSStatsList.addAll(q.getResultList());
				}
				if (!campaignSMSStatsList.isEmpty())
					return campaignSMSStatsList;
			}
			else {
				return null;
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	public int countByNameAndAccountIdAndStatus(String accountId, String campaignName, List<CampaignStatus> statuses)
			throws DBException {
		try {
			TypedQuery<Long> q = em.createNamedQuery(Campaign.COUNT_BY_NAME_AND_ACCOUNT_ID_AND_STATUS, Long.class)
					.setParameter(Campaign.ACCOUNT_ID, accountId).setParameter(Campaign.NAME, campaignName)
					.setParameter(Campaign.STATUSES, statuses);
			Long count = q.getSingleResult();
			if (count != null)
				return count.intValue();
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Campaign> findByIds(List<String> campaignIds) throws DBException {
		List<Campaign> campaigns = null;
		try {
			TypedQuery<Campaign> q = em.createNamedQuery(Campaign.FIND_BY_IDS, Campaign.class).setParameter(
					Campaign.CAMPAIGN_IDS, campaignIds);
			// else db will return missing expression exception
			if(campaignIds!=null && !campaignIds.isEmpty())
			{
				campaigns = q.getResultList();
			}
			return campaigns;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public CampaignStatus getCampaignStatus(String campaignId) throws DBException {
		try {
			List<CampaignStatus> resultSet = em.createNamedQuery(Campaign.GET_CAMPAIGN_STATUS, CampaignStatus.class)
					.setParameter(Campaign.CAMPAIGN_ID, campaignId).getResultList();
			if (!resultSet.isEmpty())
				return resultSet.get(0);
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Campaign> findCampaignsByAccountIdAndStatus(String accountId, List<CampaignStatus> status, int first,
			int max) throws DBException {
		try {
			TypedQuery<Campaign> q = em.createNamedQuery(Campaign.FIND_BY_ACCOUNT_AND_STATUSES, Campaign.class)
					.setParameter(Campaign.ACCOUNT_ID, accountId).setParameter(Campaign.STATUSES, status)
					.setMaxResults(max).setFirstResult(first);
			List<Campaign> campaingList = q.getResultList();
			em.flush();
			em.clear();
			return campaingList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Campaign> findCampaignsByAccountAndStatus(String accountId, List<CampaignStatus> status)
			throws DBException {
		try {
			List<Campaign> campaingList = em.createNamedQuery(Campaign.FIND_BY_ACCOUNT_AND_STATUSES, Campaign.class)
					.setParameter(Campaign.ACCOUNT_ID, accountId).setParameter(Campaign.STATUSES, status)
					.getResultList();
			em.flush();
			em.clear();
			return campaingList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Campaign> findCampaignsByStatus(List<CampaignStatus> status, AccountStatus acctStatus) throws DBException {
		try {
			List<Campaign> campaingList = em.createNamedQuery(Campaign.FIND_BY_STATUSES, Campaign.class)
					.setParameter(Campaign.STATUSES, status).setParameter(Campaign.ACCOUNT_STATUS, acctStatus).getResultList();
			em.flush();
			em.clear();
			return campaingList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public int countAccountCampaigns(Account account, List<CampaignStatus> status) throws DBException {
		try {
			return (em.createNamedQuery(Campaign.COUNT_USER_CAMPAIGNS, Long.class)
					.setParameter(Campaign.ACCOUNT_ID, account.getAccountId()).setParameter(Campaign.STATUSES, status)
					.getSingleResult()).intValue();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Campaign> adminGetCampaign(Date from, Date to, String accountId, String companyName,
			String billingMsisdn, String senderName, String userName, List<CampaignStatusName> statuses) {

		// countFlag true for select count // false for select entities
		String sql = formSql(from, to, accountId, companyName, billingMsisdn, senderName, userName, statuses, false);
		TypedQuery<Campaign> query = em.createQuery(sql, Campaign.class);

		if (from != null) {
			query.setParameter("from", from);
		}
		if (to != null) {
			query.setParameter("to", to);
		}
		if (accountId != null) {
			query.setParameter("accountId", accountId);
		}
		if (companyName != null) {
			query.setParameter("companyName", "%" + companyName + "%");
		}
		if (billingMsisdn != null) {
			query.setParameter("billingMsisdn", "%" + billingMsisdn + "%");
		}
		if (senderName != null) {
			query.setParameter("senderName", "%" + senderName.toLowerCase() + "%");
		}
		if (userName != null) {
			query.setParameter("userName", "%" + userName.toLowerCase() + "%");
		}
		if (statuses != null) {
			query.setParameter("statuses", statuses);
		}

		return query.getResultList();
	}

	@Override
	public List<Campaign> adminGetCampaign(Date from, Date to, String accountId, String companyName,
			String billingMsisdn, String senderName, String userName, List<CampaignStatusName> statuses, int first,
			int max) {
		// countFlag true for select count // false for select entities
		String sql = formSql(from, to, accountId, companyName, billingMsisdn, senderName, userName, statuses, false);
		TypedQuery<Campaign> query = em.createQuery(sql, Campaign.class);

		if (from != null) {
			query.setParameter("from", from, TemporalType.TIMESTAMP);
		}
		if (to != null) {
			query.setParameter("to", to, TemporalType.TIMESTAMP);
		}
		if (accountId != null && !accountId.isEmpty()) {
			query.setParameter("accountId", accountId);
		}
		if (companyName != null && !companyName.isEmpty()) {
			query.setParameter("companyName", "%" + companyName + "%");
		}
		if (billingMsisdn != null && !billingMsisdn.isEmpty()) {

			query.setParameter("billingMsisdn", "%" + billingMsisdn + "%");
		}
		if (senderName != null && !senderName.isEmpty()) {

			query.setParameter("senderName", "%" + senderName.toLowerCase() + "%");
		}
		if (userName != null && !userName.isEmpty()) {

			query.setParameter("userName", "%" + userName.toLowerCase() + "%");
		}
		if (statuses != null && !statuses.isEmpty()) {
			query.setParameter("statuses", statuses);
		}

		return query.setFirstResult(first).setMaxResults(max).getResultList();
	}

	@Override
	public Long adminCountCampaign(Date from, Date to, String accountId, String companyName, String billingMsisdn,
			String senderName, String userName, List<CampaignStatusName> statuses) {

		// countFlag true for select count // false for select entities
		String sql = formSql(from, to, accountId, companyName, billingMsisdn, senderName, userName, statuses, true);

		Query query = null;
		try {
			query = em.createQuery(sql);

			if (from != null) {
				query.setParameter("from", from);
			}
			if (to != null) {
				query.setParameter("to", to);
			}
			if (accountId != null && !accountId.isEmpty()) {
				query.setParameter("accountId", accountId);
			}
			if (companyName != null && !companyName.isEmpty()) {
				query.setParameter("companyName", "%" + companyName + "%");
			}
			if (billingMsisdn != null && !billingMsisdn.isEmpty()) {
				query.setParameter("billingMsisdn", "%" + billingMsisdn + "%");
			}
			if (senderName != null && !senderName.isEmpty()) {
				query.setParameter("senderName", "%" + senderName.toLowerCase() + "%");
			}
			if (userName != null && !userName.isEmpty()) {
				query.setParameter("userName", "%" + userName.toLowerCase() + "%");
			}
			if (statuses != null && !statuses.isEmpty()) {
				query.setParameter("statuses", statuses);
			}

		} catch (IllegalArgumentException e) {

			throw new IllegalArgumentException(e);

		}
		return (long) query.getSingleResult();
	}

	private String formSql(Date from, Date to, String accountId, String companyName, String billingMsisdn,
			String senderName, String userName, List<CampaignStatusName> statuses, boolean countFlag) {

		String sql;

		if (countFlag) {
			sql = "SELECT  COUNT(a) FROM Campaign a";
		} else {
			sql = "SELECT  a FROM Campaign a";
		}

		// search if we will add conditions to sql or not
		if (from != null || to != null || accountId != null || companyName != null || billingMsisdn != null
				|| statuses != null || senderName != null || userName != null) {

			sql += " WHERE";

			if (from != null && to != null) {
				sql += " a.campaignExecution.startTimestamp >= :from AND a.campaignExecution.startTimestamp <= :to";
			} else if (from != null) {
				sql += " a.campaignExecution.startTimestamp >= :from";
			} else if (to != null) {
				sql += " a.campaignExecution.startTimestamp <= :to";
			}

			if (billingMsisdn != null && !billingMsisdn.equals("")) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? " a.accountUser.account.billingMsisdn LIKE :billingMsisdn"
						: " AND a.accountUser.account.billingMsisdn LIKE :billingMsisdn";
			}
			if (accountId != null && !accountId.equals("")) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? "  a.accountUser.account.accountId = :accountId"
						: " AND a.accountUser.account.accountId = :accountId";
			}
			if (statuses != null && !statuses.isEmpty()) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? " a.status.campaignStatusName in :statuses"
						: " AND a.status.campaignStatusName in :statuses";
			}
			if (companyName != null && !companyName.equals("")) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? "  a.accountUser.account.companyName LIKE :companyName"
						: " AND a.accountUser.account.companyName LIKE :companyName";
			}
			if (senderName != null && !senderName.equals("")) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? "  LOWER(a.smsDetails.senderName)  LIKE :senderName"
						: " AND LOWER(a.smsDetails.senderName)  LIKE :senderName";
			}
			if (userName != null && !userName.equals("")) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? "  LOWER(a.accountUser.username)  LIKE :userName"
						: " AND LOWER(a.accountUser.username)  LIKE :userName";
			}

		}
		sql += " ORDER BY a.campaignExecution.startTimestamp";

		return sql;
	}
        
        private String formSqlForCampSearch(String accountId, String campaignName, List<CampaignStatus> statuses, boolean countFlag) {

		String sql;

		if (countFlag) {
			sql = "SELECT  COUNT(a) FROM Campaign a";
		} else {
			sql = "SELECT  a FROM Campaign a";
		}

		// search if we will add conditions to sql or not
		if (accountId != null || statuses != null || campaignName != null) {

			sql += " WHERE";

			if (accountId != null && !accountId.equals("")) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? "  a.accountUser.account.accountId = :accountId"
						: " AND a.accountUser.account.accountId = :accountId";
			}
			if (statuses != null && !statuses.isEmpty()) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? " a.status in :statuses"
						: " AND a.status in :statuses";
			}

			if (campaignName != null && !campaignName.equals("")) {
				sql += sql.charAt(sql.length() - 1) == 'E' ? "  LOWER(a.name)  LIKE :campaignName"
						: " AND LOWER(a.name)  LIKE :campaignName";
			}
		}
		sql += " ORDER BY a.campaignScheduling.scheduleStartTimestamp DESC, a.name ASC";
		return sql;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	// TODO: check if it possible to extend thread trx
	public List<Campaign> findExecutableCampaigns(int validityPeriod, int approveValidityPeriod) throws DBException {
		List<Campaign> executableCamps;
		try {
			TypedQuery<String> q = em.createNamedQuery(Campaign.FIND_EXECUTABLE_CAMP, String.class)
					.setParameter(1, validityPeriod).setParameter(2, validityPeriod).setParameter(3, approveValidityPeriod)
					.setParameter(4, validityPeriod + approveValidityPeriod);
			// Object r = q.getResultList();
			// List<Object[]> result =

			List<String> executableCampsIds = q.getResultList();
			// = q.getResultList();

			if (!executableCampsIds.isEmpty()) {
				executableCamps = findByIds(executableCampsIds);

				for (Campaign campaign : executableCamps) {
					CampaignExecution campExe = campaign.getCampaignExecution();
					campExe.setHandlerId("0");
					campaignExecutionDao.edit(campExe);
				}
			} else {
				executableCamps = new ArrayList<>();
			}
			return executableCamps;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Campaign> findTimedoutCampaigns(int campaignValidityPeriod) {
		return new ArrayList<Campaign>();
	}

	@Override
	public void updateCampaignsExecutionState(List<Campaign> updateList, List<List<CampaignLists>> campaignLists)
			throws DBException {
		try {
			Query q;
			for (Campaign camp : updateList) {
				CampaignExecution campExe = camp.getCampaignExecution();
				q = em.createNamedQuery(Campaign.UPDATE_CAMPAIGN_STATUS)
						.setParameter(Campaign.CAMPAIGN_ID, camp.getCampaignId())
						.setParameter(Campaign.STATUS, camp.getStatus());
				q.executeUpdate();

				campaignExecutionDao.updateExecutionState(campExe);
			}

			for (List<CampaignLists> list : campaignLists) {
				campaignListsDao.updateExecutionInfo(list);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public void updateCampaignsExecutionInfo(List<Campaign> updateList, List<List<CampaignLists>> campaignLists)
			throws DBException {
		for (Campaign campaign : updateList) {
			campaignExecutionDao.updateExecutionInfo(campaign.getCampaignExecution());
		}

		for (List<CampaignLists> list : campaignLists) {
			campaignListsDao.updateExecutionInfo(list);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int updateCampaignStatus(String accountId, String campaignId, CampaignStatus status) throws DBException {
		try {
			return em.createNamedQuery(Campaign.UPDATE_CAMPAIGN_STATUS_BY_ACCT_ID)
					.setParameter(Campaign.ACCOUNT_ID, accountId).setParameter(Campaign.CAMPAIGN_ID, campaignId)
					.setParameter(Campaign.STATUS, status).executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

    @Override
    public List<Campaign> searchCampaigns(String accountId, String campaignName, List<CampaignStatus> statuses, int first,
            int max) throws DBException {

        List<Campaign> result = null;
        try {
            String sql = formSqlForCampSearch(accountId, campaignName, statuses, false);
            TypedQuery<Campaign> query = em.createQuery(sql, Campaign.class);

            if (accountId != null && !accountId.equals("")) {
                query.setParameter("accountId", accountId);
            }
            if (campaignName != null && !campaignName.trim().isEmpty()) {
                query.setParameter("campaignName", "%" + campaignName.toLowerCase() + "%");
            }
            if (statuses != null && !statuses.isEmpty()) {
                query.setParameter("statuses", statuses);
            }

            query.setFirstResult(first);

            if (max > 0) {
                query.setMaxResults(max);
            }

            result = query.getResultList();
        } catch (Exception e) {
            throw new DBException(e);
        }
        return result;
    }
    
    @Override
    public int countSearchCampaigns(String accountId, String campaignName, List<CampaignStatus> statuses) throws DBException {

        Long result;
        try {
            String sql = formSqlForCampSearch(accountId, campaignName, statuses, true);
            TypedQuery<Long> query = em.createQuery(sql, Long.class);

            if (accountId != null && !accountId.equals("")) {
                query.setParameter("accountId", accountId);
            }
            if (campaignName != null && !campaignName.trim().isEmpty()) {
                query.setParameter("campaignName", "%" + campaignName.toLowerCase() + "%");
            }
            if (statuses != null && !statuses.isEmpty()) {
                query.setParameter("statuses", statuses);
            }

            result = query.getSingleResult();
        } catch (Exception e) {
            throw new DBException(e);
        }
        return result.intValue();
    }
}
