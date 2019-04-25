package com.edafa.web2sms.acc_manag.service.accountQuota;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.AccountQuotaDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountTierDaoLocal;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountQuota;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.TierType;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.Remote.AccountQuotaManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.local.AccountQuotaManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import javax.annotation.PostConstruct;

/**
 * Session Bean implementation class AccountQuotaManagementBean
 */
@Stateless
public class AccountQuotaManagementBean implements  AccountQuotaManagementBeanLocal{
	Logger quotaLogger;

	@EJB
	AccountTierDaoLocal acctTiersDao;

	@EJB
	AccountQuotaDaoLocal acctQuotaDao;

	/**
	 * Default constructor.
	 */
	public AccountQuotaManagementBean() {
		// TODO Auto-generated constructor stub
	}
        
    @PostConstruct
    public void initLoggers() {
        quotaLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
    }

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isPrepaidQuota(AccountTrxInfo trxInfo, String acctId) throws DBException {

		try {
			quotaLogger.info(trxInfo.logInfo() + "Checking is Account: " + acctId + " assigned to prepaid quota..");
			TierType type = acctTiersDao.getTierTypeForAccountId(acctId);
			if (type.getTierTypeName().equals(TierTypesEnum.PREPAID)) {
				return true;
			}
		} catch (DBException e) {
			throw e;
		}
		return false;

	}

	public void reserveAccountQuota(AccountUserTrxInfo trxInfo, Campaign camp) throws NotPrePaidAccountException,
			AccountQuotaNotFoundException, InsufficientQuotaException {
		int campQuota;
		int acctTotalQuota;
		int remainQuota;
		quotaLogger.info("Reserve Account Quota " + trxInfo.logInfo() + camp);
		try {
			AccountTier acctTier = acctTiersDao.findByAccountId(trxInfo.getUser().getAccountId());

			if (acctTier != null && acctTier.isPrepaidTier()) {
				AccountQuota acctQuota = acctQuotaDao.getAccountQuotaInfoByAccountTierID(acctTier.getAccountTiersId());
				if (acctQuota == null) {
					throw new AccountQuotaNotFoundException();
				}// end if

				campQuota = camp.getSmsDetails().getSMSSegCount();
				acctTotalQuota = acctTier.getTier().getQuota();
				remainQuota = acctTotalQuota - (acctQuota.getConsumedSmss() + acctQuota.getReservedSmss());

				if (remainQuota < campQuota) {
					throw new InsufficientQuotaException(trxInfo.getUser().toString() + " has remainQuota("
							+ remainQuota + ") and should reserve campQuota(" + campQuota + ").");
				}// end if

				acctQuotaDao.incrementReservedSmss(acctQuota.getAccountTiersId(), campQuota);
			}// end if
			else {
				throw new NotPrePaidAccountException();
			}// end else
		}// end try
		catch (DBException e) {
			quotaLogger.error(trxInfo.logId() + "DBException occure: " + e.getMessage(), e);
		}// end catch
	}// end of method reserveAccountQuota

	public void updateReserveAccountQuota(AccountUserTrxInfo trxInfo, String campaignId, int originalCampaignQuota,
			int updatedCampaignQuota) throws NotPrePaidAccountException, AccountQuotaNotFoundException,
			InsufficientQuotaException {
		int campQuota;
		int acctTotalQuota;
		int remainQuota;
		quotaLogger.info(trxInfo.logInfo() + "campaignId=[" + campaignId + "]" + "originalCampaignQuota=["
				+ originalCampaignQuota + "]" + "updatedCampaignQuota=[" + updatedCampaignQuota + "]");
		try {
			AccountTier acctTier = acctTiersDao.findByAccountId(trxInfo.getUser().getAccountId());

			if (acctTier != null && acctTier.isPrepaidTier()) {
				AccountQuota acctQuota = acctQuotaDao.getAccountQuotaInfoByAccountTierID(acctTier.getAccountTiersId());
				if (acctQuota == null) {
					throw new AccountQuotaNotFoundException();
				}// end if

				// calculating delta of quota
				campQuota = updatedCampaignQuota - originalCampaignQuota;

				acctTotalQuota = acctTier.getTier().getQuota();
				remainQuota = acctTotalQuota - (acctQuota.getConsumedSmss() + acctQuota.getReservedSmss());

				if (remainQuota < campQuota) {
					throw new InsufficientQuotaException(trxInfo.getUser().toString() + " has remainQuota("
							+ remainQuota + ") and should reserve campQuota(" + campQuota + ").");
				}// end if

				acctQuotaDao.incrementReservedSmss(acctQuota.getAccountTiersId(), campQuota);
			}// end if
			else {
				throw new NotPrePaidAccountException();
			}// end else
		}// end try
		catch (DBException e) {
			quotaLogger.error(trxInfo.logId() + "DBException occure: " + e.getMessage(), e);
		}// end catch
	}// end of method updateReserveAccountQuota
}
