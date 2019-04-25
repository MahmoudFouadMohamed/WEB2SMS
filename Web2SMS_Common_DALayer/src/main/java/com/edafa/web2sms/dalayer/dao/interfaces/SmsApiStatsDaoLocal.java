package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SmsApiDailySmsStats;
import com.edafa.web2sms.dalayer.model.SmsApiHourlySmsStats;

/**
*
* @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
*/
@Local
public interface SmsApiStatsDaoLocal {

	public List<SmsApiHourlySmsStats> getSmsApiHorlyStats(String ownerId, String senderName, Date from, Date to,
			int start, int count) throws DBException;

	public List<SmsApiDailySmsStats> getSmsApiDailyStats(String ownerId, String senderName, Date from, Date to,
			int start, int count) throws DBException;

	public int countSmsApiHorlyStats(String ownerId, String senderName, Date from, Date to) throws DBException;

	public int countSmsApiDailyStats(String ownerId, String senderName, Date from, Date to) throws DBException;

}
