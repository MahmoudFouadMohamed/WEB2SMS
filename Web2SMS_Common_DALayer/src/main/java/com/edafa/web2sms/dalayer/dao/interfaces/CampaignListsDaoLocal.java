package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.dalayer.pojo.CampaignAssociatedList;

public interface CampaignListsDaoLocal {

	boolean isListAssociatedToCampaignStatus(Integer listId, List<CampaignStatus> statusList) throws DBException;

	List<CampaignLists> findSubmittableByCampaignIdOrdered(String campId) throws DBException;

	List<CampaignLists> findByCampaignIdOrdered(String campId) throws DBException;

	void updateExecutionInfo(List<CampaignLists> campaignLists) throws DBException;

	List<ContactList> findListsByCampaignId(String campId) throws DBException;

	List<ContactList> findListsByCampaignIdAndAccountId(String campId, String accountId) throws DBException;

	List<ContactList> findListsByCampaignIdAndAccountIdWithCounts(String campId, String accountId) throws DBException;

	List<ContactList> findListsByCampaignIdAndAccountId(String campId, String accountId, List<ListType> listTypes)
			throws DBException;

	List<ContactList> findListsByCampaignIdAndAccountIdWithCounts(String campId, String accountId,
			List<ListType> listTypes) throws DBException;

	List<ContactList> findListsByCampaignIdWithCounts(String campaignId) throws DBException;

	List<ContactList> findListsByCampaignId(String campaignId, List<ListType> listTypes) throws DBException;

	List<ContactList> findListsByCampaignIdWithCounts(String campaignId, List<ListType> listTypes) throws DBException;

	int countSubmittedSMSInLists(String campaignId) throws DBException;

	void resetSubmittedSMSInLists(String campaignId) throws DBException;

	List<CampaignAssociatedList> findListNameByCampId(List<String> campIdList, List<ListType> listTypes)
			throws DBException;


}
