/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing.interfaces;

import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserModel;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.Tier;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface AccountConversionFacingLocal {
    public Account getAccount(AccManagUserModel userModel);
    public Account getAccount(AccountModel account);
    public AccountUser getAccountUser(AccountModel account);
    public AccountModel getAccountModel(Account account);
    public AccountModel getAccountModel(AccountUser accountUser);
    public AccountUserModel getAccountUserModel(AccountUser accountUser);
    public List<AccountUserModel> getAccountUserModel(List <AccountUser> accountUsers);
    public AccountUser getAccountUser(AccountUserModel accountUser);
    public AccountModelFullInfo getAccountModelFullInfo(Account account);
    public AccountModelFullInfo getAccountModelFullInfo(AccountUser accountUsr);
    public Tier getTier(TierModel tierModel);
    public TierModel getTierModel(Tier tier);
}
