package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.dalayer.model.TierType;

@Local
public interface AccountTierDaoLocal {

	public void create(AccountTier entity) throws DBException;
	AccountTier findByBillingMSISDN(String msisdn) throws DBException;
	TierType getTierTypeForBillingMSISDN(String msisdn) throws DBException;
	AccountTier findByAccountId(String accountId) throws DBException;
	TierType getTierTypeForAccountId(String acctId) throws DBException;
}
