package com.edafa.web2sms.account;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.account.exceptions.NoAccountQuotaHandleFoundException;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountQuotaDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountTierDaoLocal;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.TierType;
import com.edafa.web2sms.quota.PostpaidAccountQuotaHandle;
import com.edafa.web2sms.quota.PrepaidAccountQuotaHandle;
import com.edafa.web2sms.quota.interfaces.AccountQuotaHandle;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class AccountHandler {

	protected Logger acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_HANDLER.name());

	private AccountTierDaoLocal accountTierDao;
	private AccountQuotaDaoLocal accountQuotaDao;

	private Map<String, AccountQuotaHandle> accountQuotaHandleMap;

	public AccountHandler(AccountTierDaoLocal accountTierDao, AccountQuotaDaoLocal accountQuotaDao) {
		this.accountQuotaDao = accountQuotaDao;
		this.accountTierDao = accountTierDao;
		accountQuotaHandleMap = new ConcurrentHashMap<String, AccountQuotaHandle>();
	}

	public AccountQuotaHandle getAccountQuotaHandle(Campaign camp) {
		acctLogger.info("Getting Account Quota Handle");
		AccountQuotaHandle acctQuotaHandle = accountQuotaHandleMap.get(camp.getAccountUser().getAccountId());
		if (acctQuotaHandle == null) {
			try {
				TierType tierType = accountTierDao.getTierTypeForAccountId(camp.getAccountUser().getAccountId());
				if (tierType != null && tierType.getTierTypeName().equals(TierTypesEnum.PREPAID)) {
					AccountTier accountTier = accountTierDao.findByAccountId(camp.getAccountUser().getAccountId());
					acctQuotaHandle = new PrepaidAccountQuotaHandle(camp, accountTier);

				} else {
					acctQuotaHandle = new PostpaidAccountQuotaHandle();
				}
				accountQuotaHandleMap.put(camp.getAccountUser().getAccountId(), acctQuotaHandle);
			} catch (DBException e) {
				acctLogger.error(e.getMessage(), e);
			}
		} else {
			acctQuotaHandle.associateCamp(camp.getCampaignId());
		}
		return acctQuotaHandle;
	}

	public void finalizeAccountQuotaHandle(String accountId) throws DBException, NoAccountQuotaHandleFoundException {
		AccountQuotaHandle accountQuotaHandle = accountQuotaHandleMap.get(accountId);
		if (accountQuotaHandle != null && accountQuotaHandle instanceof PrepaidAccountQuotaHandle) {
			persistAccountQuotaHandle(accountQuotaHandle);
			accountQuotaHandleMap.remove(accountId);
            } else {
                if (acctLogger.isDebugEnabled()) {
                    acctLogger.debug("No AccountQuotaHandle found for account(" + accountId + ").");
                }
            }
	}

	private void persistAccountQuotaHandle(AccountQuotaHandle accountQuotaHandle) throws DBException {
			accountQuotaHandle.calculateDelta();
			accountQuotaDao.updateAccountQuota(accountQuotaHandle.getAccountId(), accountQuotaHandle.getReservedDelta().get(),
					accountQuotaHandle.getConsumedDelta().get());
	}

	public void updateAccountQuota(String accountId) throws DBException {
		AccountQuotaHandle accountQuotaHandle = accountQuotaHandleMap.get(accountId);
		if (accountQuotaHandle != null && accountQuotaHandle instanceof PrepaidAccountQuotaHandle) {
			persistAccountQuotaHandle(accountQuotaHandle);
			accountQuotaHandleMap.get(accountId).updateHandle();
            } else {
                if (acctLogger.isDebugEnabled()) {
                    acctLogger.debug("No AccountQuotaHandle found for account(" + accountId + ").");
                }
            }
	}

	public void refreshAfterUpdateAccountQuota(String accountId) throws DBException {
		AccountQuotaHandle accountQuotaHandle = accountQuotaHandleMap.get(accountId);
		if (accountQuotaHandle != null && accountQuotaHandle instanceof PrepaidAccountQuotaHandle) {
			persistAccountQuotaHandle(accountQuotaHandle);

			AccountTier accountTier = accountTierDao.findByAccountId(accountId);
			if (accountTier == null) {
				acctLogger.error("No AccountTier found for account(" + accountId + ").");

				throw new DBException("No AccountTier found for account(" + accountId + ").");
			}
			accountQuotaHandleMap.get(accountId).updateHandle(accountTier);
            } else {
                if (acctLogger.isDebugEnabled()) {
                    acctLogger.debug("No AccountQuotaHandle found for account(" + accountId + ").");
                }
            }
	}

	public void updateAllAccountQuota() throws DBException, NoAccountQuotaHandleFoundException {
		Set<String> accountIdSet = accountQuotaHandleMap.keySet();
		for (String accountId : accountIdSet) {
			updateAccountQuota(accountId);
		}
	}

}
