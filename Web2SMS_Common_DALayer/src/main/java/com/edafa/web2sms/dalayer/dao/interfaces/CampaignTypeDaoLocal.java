package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.CampaignTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.CampaignType;

@Local
public interface CampaignTypeDaoLocal extends Cachable<CampaignType, CampaignTypeName>{
	
	List<CampaignType> findAll() throws DBException;


}