package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountQuota;

@Local
public interface AccountQuotaDaoLocal {

	public void create(AccountQuota entity) throws DBException;
	
	AccountQuota getAccountQuotaInfoByAccountTierID(Integer accountTierId);

	void incrementReservedSmss(int accountTierId, int value) throws DBException;

	int updateAccountQuota(String accountId, int reservedDelta, int consumedDelta) throws DBException;

}
