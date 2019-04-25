/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.interfaces.Remote;

import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserModel;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author mahmoud
 */
@Remote
public interface AccountConversionBeanRemote {

    Account getAccount(AccManagUserModel userModel);
    Account getAccount(AccountModel account);
    AccountUser getAccountUser(AccountModel account);
    AccountModel getAccountModel(Account account);
    AccountModel getAccountModel(AccountUser accountUser);
    AccountUserModel getAccountUserModel(AccountUser accountUser);
    List<AccountUserModel> getAccountUserModel(List <AccountUser> accountUsers);
    AccountUser getAccountUser(AccountUserModel accountUser);
    AccountModelFullInfo getAccountModelFullInfo(Account account);
    AccountModelFullInfo getAccountModelFullInfo(AccountUser accountUsr);
    Tier getTier(TierModel tierModel);
    TierModel getTierModel(Tier tier);
}
