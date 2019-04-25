/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing.interfaces;

import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface AccountQuotaManagementFacingLocal {

    boolean isPrepaidQuota(AccountTrxInfo trxInfo, String msisdn) throws DBException;

    void reserveAccountQuota(AccountUserTrxInfo trxInfo, Campaign camp) throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException;

    void updateReserveAccountQuota(AccountUserTrxInfo trxInfo, String campaignId, int originalCampaignQuota, int updatedCampaignQuota) throws NotPrePaidAccountException, AccountQuotaNotFoundException, InsufficientQuotaException;

}
