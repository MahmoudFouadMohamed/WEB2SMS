package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignAggregationView;

@Local
public interface CampaignAggregationDaoLocal {

	List<CampaignAggregationView> findByAccountId(String accountId) throws DBException;

	List<CampaignAggregationView> findByAccountId(String accountId, int first, int max) throws DBException;

	List<CampaignAggregationView> findByAccountIdAndName(String accountId, String campName) throws DBException;

	List<CampaignAggregationView> findByAccountIdAndName(String accountId, String campName, int first, int max)
			throws DBException;

	List<CampaignAggregationView> findByAccountIdAndStatus(String accountId, List<CampaignStatusName> statues)
			throws DBException;

	List<CampaignAggregationView> findByAccountIdAndStatus(String accountId, List<CampaignStatusName> statues,
			int first, int max) throws DBException;

	List<CampaignAggregationView> findByAccountIdAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo) throws DBException;

	List<CampaignAggregationView> findByAccountIdAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, int first, int max) throws DBException;

	List<CampaignAggregationView> findByAccountIdStatusAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, List<CampaignStatusName> statues) throws DBException;

	List<CampaignAggregationView> findByAccountIdStatusAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, List<CampaignStatusName> statues, int first, int max) throws DBException;

	long count(String accountId) throws DBException;

	long count(String accountId, List<CampaignStatusName> statuses) throws DBException;

	long count(String accountId, List<CampaignStatusName> statuses, Date startDate, Date endDate) throws DBException;

	long count(String accountId, Date startTimestampFrom, Date startTimestampTo) throws DBException;

	long count(String accountId, String campName) throws DBException;

	long count(String accountId, Date startTimestampFrom, Date startTimestampTo, String campName) throws DBException;

	List<CampaignAggregationView> findByAccountIdNameAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, String campName) throws DBException;

	List<CampaignAggregationView> findByAccountIdNameAndDateRange(String accountId, Date startTimestampFrom,
			Date startTimestampTo, String campName, int first, int max) throws DBException;

	CampaignAggregationView findByCampId(String campId) throws DBException;

}
