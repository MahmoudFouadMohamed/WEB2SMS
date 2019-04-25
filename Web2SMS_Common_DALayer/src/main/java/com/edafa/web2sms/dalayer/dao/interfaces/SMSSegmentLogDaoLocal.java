package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.dalayer.model.SMSSegmentLog;

/**
 * 
 * @author akhalifah
 */

@Local
public interface SMSSegmentLogDaoLocal {
	void create(SMSSegmentLog smsSegmentLog) throws DBException;

	void edit(SMSSegmentLog smsSegmentLog) throws DBException;

	void remove(SMSSegmentLog smsSegmentLog) throws DBException;

	SMSSegmentLog find(Object id) throws DBException;

	List<SMSSegmentLog> findAll() throws DBException;

	List<SMSSegmentLog> findRange(int[] range) throws DBException;

	int count() throws DBException;

	void create(List<SMSSegmentLog> segmentsLogs) throws DBException;

	SMSLog findSMSLogByMessageId(String messageId) throws DBException;

	public SMSLog findSMSLogByMessageIdAndSMSCId(String messageId, int smscId)
			throws DBException;

	void updateDeliveredSMSSementsInfo(List<SMSSegmentLog> segmentsLogs)
			throws DBException;

	List<SMSSegmentLog> findBySMSId(String smsId) throws DBException;

}