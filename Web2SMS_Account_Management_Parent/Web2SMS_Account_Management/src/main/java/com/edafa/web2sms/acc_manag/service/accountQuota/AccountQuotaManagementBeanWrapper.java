/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.accountQuota;

import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.Remote.AccountQuotaManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.local.AccountQuotaManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.EJB;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AccountQuotaManagementBeanWrapper implements AccountQuotaManagementBeanRemote {

    @EJB
    AccountQuotaManagementBeanLocal accountQuotaManagementBean;

    @Override
    public boolean isPrepaidQuota(AccountTrxInfo trxInfo, String msisdn) throws DBException {
        return accountQuotaManagementBean.isPrepaidQuota(trxInfo, msisdn);
    }

    @Override
    public void reserveAccountQuota(AccountUserTrxInfo trxInfo, Campaign camp) throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException {
        accountQuotaManagementBean.reserveAccountQuota(trxInfo, camp);
    }

    @Override
    public void updateReserveAccountQuota(AccountUserTrxInfo trxInfo, String campaignId, int originalCampaignQuota, int updatedCampaignQuota) throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException {
        accountQuotaManagementBean.updateReserveAccountQuota(trxInfo, campaignId, originalCampaignQuota, updatedCampaignQuota);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
