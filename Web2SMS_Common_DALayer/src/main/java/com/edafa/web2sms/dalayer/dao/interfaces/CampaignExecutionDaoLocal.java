package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignAction;
import com.edafa.web2sms.dalayer.model.CampaignExecution;
import com.edafa.web2sms.dalayer.model.CampaignStatus;

@Local
public interface CampaignExecutionDaoLocal {
	void create(CampaignExecution campaignExecution) throws DBException;

	void edit(CampaignExecution campaignExecution) throws DBException;

	void remove(CampaignExecution campaignExecution) throws DBException;

	void updateCampaignAction(String campaignId, CampaignAction action) throws DBException;

	void updateExecutionState(CampaignExecution campaignExecution) throws DBException;

	void updateExecutionInfo(List<CampaignExecution> campExecutionList) throws DBException;

	void updateExecutionInfo(CampaignExecution campExe) throws DBException;

	int updateCampaignsActionByAcctId(String accountId, CampaignAction action, List<CampaignStatus> statusList) throws DBException;

}
