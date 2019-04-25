package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSAPIView;

@Local
public interface SMSAPILogDaoLocal {

	List<SMSAPIView> getSMSAPIView(String accountId, String senderName, String smsText, Date dateFrom, Date dateTo, int dirst, int max);
	public Long countSMSAPIView(String accountId, String senderName, String smsText, Date dateFrom, Date dateTo);
	
	SMSAPIView find(Object id) throws DBException;	

	public List<SMSAPIView> findSMSwithinDates(String accountId,Date startDate, Date endDate) throws DBException;
	public List<SMSAPIView> findSMSwithinMSISDNandDates(String accountId,String msisdn,Date startDate, Date endDate,int index,int count) throws DBException;
        public List<SMSAPIView> findInIdList(List<String> smsIdList) throws DBException;

}
