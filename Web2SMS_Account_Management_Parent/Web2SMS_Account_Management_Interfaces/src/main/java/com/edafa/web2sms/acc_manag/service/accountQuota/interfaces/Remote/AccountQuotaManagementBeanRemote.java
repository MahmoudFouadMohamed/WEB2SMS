package com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.Remote;

import javax.ejb.Remote;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;

@Remote
public interface AccountQuotaManagementBeanRemote {

	boolean isPrepaidQuota(AccountTrxInfo trxInfo, String msisdn) throws DBException;
	void reserveAccountQuota(AccountUserTrxInfo trxInfo, Campaign camp) throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException;
	void updateReserveAccountQuota(AccountUserTrxInfo trxInfo, String campaignId, int originalCampaignQuota , int updatedCampaignQuota) throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException;

}
