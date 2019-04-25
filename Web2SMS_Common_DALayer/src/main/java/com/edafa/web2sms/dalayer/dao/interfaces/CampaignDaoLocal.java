package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.pojo.CampSMSStats;
import com.edafa.web2sms.dalayer.pojo.CampaignFrequency;

@Local
public interface CampaignDaoLocal {
	Campaign findByName(String name) throws DBException;

	void create(Campaign campaign) throws DBException;

	void edit(Campaign campaign) throws DBException;

	void remove(Campaign campaign) throws DBException;

	Campaign find(Object id) throws DBException;

	List<Campaign> findAll() throws DBException;

	List<Campaign> findRange(int[] range) throws DBException;

	int count() throws DBException;

	int count(Object id) throws DBException;

	CampaignStatus getCampaignStatus(String campaignId) throws DBException;

	int countAccountCampaigns(Account account, List<CampaignStatus> status) throws DBException;

	Campaign findByIdAndAccountId(String accountId, String campaignId, boolean lock) throws DBException;

	List<Campaign> findCampaignsByAccountIdAndStatus(String accountId, List<CampaignStatus> status, int first, int max) throws DBException;

	List<Campaign> findCampaignsByAccountAndStatus(String accountId, List<CampaignStatus> status) throws DBException;

	List<Campaign> adminGetCampaign(Date from, Date to, String accountId, String companyName, String billingMsisdn, String senderName,String userName,
			List<CampaignStatusName> statuses);

	List<Campaign> adminGetCampaign(Date from, Date to, String accountId, String companyName, String billingMsisdn, String senderName,
			String userName, List<CampaignStatusName> statuses, int first, int max);

	Long adminCountCampaign(Date from, Date to, String accountId, String companyName, String billingMsisdn, String senderName,String userName,
			List<CampaignStatusName> statuses);

	List<Campaign> findExecutableCampaigns(int validityPeriod, int approveValidityPeriod) throws DBException;

	List<Campaign> findTimedoutCampaigns(int campaignValidityPeriod) throws DBException;

	void updateCampaignsExecutionState(List<Campaign> updateList, List<List<CampaignLists>> campaignLists) throws DBException;

	void updateCampaignsExecutionInfo(List<Campaign> updateList, List<List<CampaignLists>> campaignLists) throws DBException;

	List<Campaign> findByIds(List<String> campaignIds) throws DBException;

	int countByNameAndAccountIdAndStatus(String accountId, String campaignName, List<CampaignStatus> statuses) throws DBException;

	int updateCampaignStatus(String accountId, String campaignId, CampaignStatus status) throws DBException;

	public List<Campaign> findCampaignsByStatus(List<CampaignStatus> status, AccountStatus acctStatus) throws DBException;
	List<CampSMSStats> findCampSMSStats(List<String> campIdList) throws DBException;

	List<CampaignFrequency> findCampFreq(List<String> campIdList) throws DBException;
        
        public List<Campaign> searchCampaigns(String accountId, String campaignName, List<CampaignStatus> statuses, int first,
            int max) throws DBException;
        
        public int countSearchCampaigns(String accountId, String campaignName, List<CampaignStatus> statuses) throws DBException;

}
