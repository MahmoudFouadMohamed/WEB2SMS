package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.CampaignAction;

@Local
public interface CampaignActionDaoLocal extends Cachable<CampaignAction, CampaignActionName> {
	void create(CampaignAction campaignAction) throws DBException;

	void edit(CampaignAction campaignAction) throws DBException;

	void remove(CampaignAction campaignAction) throws DBException;

	CampaignAction find(Object id) throws DBException;

	List<CampaignAction> findAll() throws DBException;

	List<CampaignAction> findRange(int[] range) throws DBException;

	int count() throws DBException;
}
