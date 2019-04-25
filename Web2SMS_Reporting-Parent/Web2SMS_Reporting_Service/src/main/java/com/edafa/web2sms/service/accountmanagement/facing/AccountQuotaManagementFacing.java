/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager;

import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.Remote.AccountQuotaManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountQuotaManagementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.remote.AccountManegementRemotePoolsLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AccountQuotaManagementFacing implements AccountQuotaManagementFacingLocal {

	org.apache.logging.log4j.Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	@EJB
	AccountManegementRemotePoolsLocal accountManegementRemotePools;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isPrepaidQuota(AccountTrxInfo trxInfo, String msisdn) throws DBException {
		boolean prepaid = false;
		AccountQuotaManagementBeanRemote accountQuotaManagementBeanRemote = null;
		try {
			accountQuotaManagementBeanRemote = accountManegementRemotePools.getAccountQuotaManagementBeanRemote();
		} catch (Exception ex) {
			appLogger.error("Failed to get AccountQuotaManagementBeanRemote from AccountManegementRemotePoolsLocal",
					ex);
		}
		try {
			prepaid = accountQuotaManagementBeanRemote.isPrepaidQuota(trxInfo, msisdn);
		} finally {
			try {
				accountManegementRemotePools.returnAccountQuotaManagementBeanRemote(accountQuotaManagementBeanRemote);

			} catch (Exception ex) {
				appLogger.error(
						"Failed to return AccountQuotaManagementBeanRemote to AccountManegementRemotePoolsLocal", ex);
			}
		}

		return prepaid;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void reserveAccountQuota(AccountUserTrxInfo trxInfo, Campaign camp)
			throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException {
		AccountQuotaManagementBeanRemote accountQuotaManagementBeanRemote = null;
		try {
			accountQuotaManagementBeanRemote = accountManegementRemotePools.getAccountQuotaManagementBeanRemote();
		} catch (Exception ex) {
			appLogger.error("Failed to get AccountQuotaManagementBeanRemote from AccountManegementRemotePoolsLocal",
					ex);
		}
		try {
			accountQuotaManagementBeanRemote.reserveAccountQuota(trxInfo, camp);
		} finally {
			try {
				accountManegementRemotePools.returnAccountQuotaManagementBeanRemote(accountQuotaManagementBeanRemote);

			} catch (Exception ex) {
				appLogger.error(
						"Failed to return AccountQuotaManagementBeanRemote to AccountManegementRemotePoolsLocal", ex);
			}
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateReserveAccountQuota(AccountUserTrxInfo trxInfo, String campaignId, int originalCampaignQuota,
			int updatedCampaignQuota)
			throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException {
		AccountQuotaManagementBeanRemote accountQuotaManagementBeanRemote = null;
		try {
			accountQuotaManagementBeanRemote = accountManegementRemotePools.getAccountQuotaManagementBeanRemote();
		} catch (Exception ex) {
			appLogger.error("Failed to get AccountQuotaManagementBeanRemote from AccountManegementRemotePoolsLocal",
					ex);
		}
		try {
			accountQuotaManagementBeanRemote.updateReserveAccountQuota(trxInfo, campaignId, originalCampaignQuota,
					updatedCampaignQuota);
		} finally {
			try {
				accountManegementRemotePools.returnAccountQuotaManagementBeanRemote(accountQuotaManagementBeanRemote);

			} catch (Exception ex) {
				appLogger.error(
						"Failed to return AccountQuotaManagementBeanRemote to AccountManegementRemotePoolsLocal", ex);
			}
		}

	}

}
