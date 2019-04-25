package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMS_API_VIEW_Camp;

@Local
public interface SMSAPIViewCampaignDaoLocal {
	
	SMS_API_VIEW_Camp find(Object id) throws DBException;	
	public List<SMS_API_VIEW_Camp> findSMSwithinDates(String accountId,Date startDate, Date endDate) throws DBException;
	public List<SMS_API_VIEW_Camp> findSMSwithinMSISDNandDates(String accountId,String msisdn,Date startDate, Date endDate,int index,int count) throws DBException;
}
