/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account;

import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountConversionBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountConversionBeanLocal;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserModel;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.Tier;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.EJB;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AccountConversionBeanWrapper implements AccountConversionBeanRemote {

    @EJB
    AccountConversionBeanLocal accountConversionBean;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Account getAccount(AccManagUserModel userModel) {
        return accountConversionBean.getAccount(userModel);
    }

    @Override
    public Account getAccount(AccountModel account) {
        return accountConversionBean.getAccount(account);
    }

    @Override
    public AccountUser getAccountUser(AccountModel account) {
        return accountConversionBean.getAccountUser(account);
    }

    @Override
    public AccountModel getAccountModel(Account account) {
        return accountConversionBean.getAccountModel(account);
    }

    @Override
    public AccountModel getAccountModel(AccountUser accountUser) {
        return accountConversionBean.getAccountModel(accountUser);
    }

    @Override
    public AccountUserModel getAccountUserModel(AccountUser accountUser) {
        return accountConversionBean.getAccountUserModel(accountUser);
    }

    @Override
    public List<AccountUserModel> getAccountUserModel(List<AccountUser> accountUsers) {
        return accountConversionBean.getAccountUserModel(accountUsers);
    }

    @Override
    public AccountUser getAccountUser(AccountUserModel accountUser) {
        return accountConversionBean.getAccountUser(accountUser);
    }

    @Override
    public AccountModelFullInfo getAccountModelFullInfo(Account account) {
        return accountConversionBean.getAccountModelFullInfo(account);
    }

    @Override
    public AccountModelFullInfo getAccountModelFullInfo(AccountUser accountUsr) {
        return accountConversionBean.getAccountModelFullInfo(accountUsr);
    }

    @Override
    public Tier getTier(TierModel tierModel) {
        return accountConversionBean.getTier(tierModel);
    }

    @Override
    public TierModel getTierModel(Tier tier) {
        return accountConversionBean.getTierModel(tier);
    }
}
