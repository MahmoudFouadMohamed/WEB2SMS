/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing;

import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountConversionBeanRemote;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountConversionFacingLocal;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.service.accountmanagement.remote.AccountManegementRemotePoolsLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AccountConversionFacing implements AccountConversionFacingLocal {

    org.apache.logging.log4j.Logger appLogger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());

    @EJB
    AccountManegementRemotePoolsLocal accountManegementRemotePools;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Tier getTier(TierModel tierModel) {
        Tier tier = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            tier = accountConversionBeanRemote.getTier(tierModel);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return tier;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TierModel getTierModel(Tier tier) {
        TierModel tierModel = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            tierModel = accountConversionBeanRemote.getTierModel(tier);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return tierModel;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
