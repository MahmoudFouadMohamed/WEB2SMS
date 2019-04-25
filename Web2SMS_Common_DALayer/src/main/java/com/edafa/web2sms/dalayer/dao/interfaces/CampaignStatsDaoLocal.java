package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignStatsReport;

/**
*
* @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
*/
@Local
public interface CampaignStatsDaoLocal {

	// @formatter:off
	public CampaignStatsReport find(String accountId, String campId) throws DBException;
	public List<CampaignStatsReport> find(String accountId, String campName, int first, int max) throws DBException;
	public List<CampaignStatsReport> find(String accountId, Date startDate, Date endDate, int first, int max) throws DBException;
	public List<CampaignStatsReport> find(String accountId, String campName, Date startDate,Date endDate, int first, int max) throws DBException;
	// @formatter:on

	public int count(String accountId) throws DBException;
	public int count(String accountId, String campName) throws DBException;
	public int count(String accountId, Date startDate, Date endDate) throws DBException;
	public int count(String accountId, Date startDate, Date endDate, String campName) throws DBException;
}
