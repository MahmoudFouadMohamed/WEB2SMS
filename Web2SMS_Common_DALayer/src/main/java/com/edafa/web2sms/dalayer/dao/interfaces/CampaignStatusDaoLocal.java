package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.CampaignStatus;

@Local
public interface CampaignStatusDaoLocal extends Cachable<CampaignStatus, CampaignStatusName> {
	void create(CampaignStatus campaignStatus) throws DBException;

	void edit(CampaignStatus campaignStatus) throws DBException;

	void remove(CampaignStatus campaignStatus) throws DBException;

	CampaignStatus find(Object id) throws DBException;

	List<CampaignStatus> findAll() throws DBException;

	List<CampaignStatus> findRange(int[] range) throws DBException;

	int count() throws DBException;

	CampaignStatus findByStatusName(CampaignStatusName statusName);
}
