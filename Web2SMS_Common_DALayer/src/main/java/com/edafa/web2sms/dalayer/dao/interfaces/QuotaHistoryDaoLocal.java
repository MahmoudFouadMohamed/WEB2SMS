package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.QuotaHistory;

@Local
public interface QuotaHistoryDaoLocal {
	
	public void create(QuotaHistory entity)throws DBException ;
	public void edit(QuotaHistory entity) throws DBException;
	public QuotaHistory findQuotaHistory(String accountId) throws DBException;

}
