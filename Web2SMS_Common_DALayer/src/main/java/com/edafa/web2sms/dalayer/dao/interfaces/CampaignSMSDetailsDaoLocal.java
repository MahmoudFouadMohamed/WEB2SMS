package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;

@Local
public interface CampaignSMSDetailsDaoLocal {

	String findSMSText(String campaignId) throws DBException;

}
