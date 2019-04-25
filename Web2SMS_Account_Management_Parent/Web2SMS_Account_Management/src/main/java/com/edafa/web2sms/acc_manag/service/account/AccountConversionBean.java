package com.edafa.web2sms.acc_manag.service.account;

import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountConversionBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountConversionBeanRemote;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountSender;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.IntraSender;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserModel;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;

@Stateless
public class AccountConversionBean implements AccountConversionBeanLocal {

    @EJB
    AccountStatusDaoLocal acctStatusDao;

    public AccountConversionBean() {

    }

    @Override
    public Account getAccount(AccManagUserModel userModel) {
        Account account = new Account();
//		account.setAccountAdmin(userModel.getUsername());
        account.setAccountId(userModel.getAccountId());
        return account;
    }

    @Override
    public Account getAccount(AccountModel account) {
        Account acct = new Account();
        acct.setAccountId(account.getAccountId());
        acct.setCompanyName(account.getCompanyDomain());
//		acct.setAccountAdmin(account.getAccountAdmin());
        acct.setStatus(acctStatusDao.getCachedObjectByName(account.getStatus()));
        acct.setBillingMsisdn(account.getBillingMsisdn());
        acct.setTier(getTier(account.getTier()));
        return acct;
    }

    @Override
    public AccountUser getAccountUser(AccountModel account) {
        Account acct = new Account();
        AccountUser acctUsr = new AccountUser();
        acct.setAccountId(account.getAccountId());
        acct.setCompanyName(account.getCompanyDomain());
        acct.setStatus(acctStatusDao.getCachedObjectByName(account.getStatus()));
        acct.setBillingMsisdn(account.getBillingMsisdn());
        acct.setTier(getTier(account.getTier()));

        acctUsr.setAccount(acct);
        acctUsr.setUsername(account.getAccountUser());
        acctUsr.setStatus(acctStatusDao.getCachedObjectByName(account.getStatus()));
        acctUsr.setAdminRoleFlag(account.isAdminRole());
        return acctUsr;
    }

    @Override
    public AccountModel getAccountModel(Account account) {
        AccountModel acct = new AccountModel();
        acct.setAccountId(account.getAccountId());
        acct.setCompanyId(account.getAccountId());
        acct.setCompanyName(account.getCompanyName());
        acct.setStatus(account.getStatus().getAccountStatusName());
        acct.setCompanyDomain(account.getCompanyName());
        acct.setBillingMsisdn(account.getBillingMsisdn());
        acct.setTier(getTierModel(account.getTier()));
        return acct;
    }

    @Override
    public AccountModel getAccountModel(AccountUser accountUser) {
        AccountModel acct = new AccountModel();
        acct.setAccountId(accountUser.getAccount().getAccountId());
        acct.setCompanyId(accountUser.getAccount().getAccountId());
        acct.setCompanyName(accountUser.getAccount().getCompanyName());
        acct.setStatus(accountUser.getAccount().getStatus().getAccountStatusName());
        acct.setAccountUser(accountUser.getUsername());
        acct.setCompanyDomain(accountUser.getAccount().getCompanyName());
        acct.setBillingMsisdn(accountUser.getAccount().getBillingMsisdn());
        acct.setTier(getTierModel(accountUser.getAccount().getTier()));
        return acct;
    }

    @Override
    public AccountUserModel getAccountUserModel(AccountUser accountUser) {
        AccountUserModel acct = new AccountUserModel();
        acct.setStatus(accountUser.getStatus().getAccountStatusName());
        acct.setAccount(getAccountModel(accountUser.getAccount()));
        acct.setAccountUserId(accountUser.getAccountUserId());
        acct.setAdminRoleFlag(accountUser.getAdminRoleFlag());
        acct.setUsername(accountUser.getUsername());

        return acct;
    }

    @Override
    public List<AccountUserModel> getAccountUserModel(List<AccountUser> accountUsers) {

        List<AccountUserModel> accountUsersModel = new ArrayList<>();

        for (AccountUser accountUser : accountUsers) {

            AccountUserModel acct = new AccountUserModel();
            acct.setStatus(accountUser.getStatus().getAccountStatusName());
            acct.setAccount(getAccountModel(accountUser.getAccount()));
            acct.setAccountUserId(accountUser.getAccountUserId());
            acct.setAdminRoleFlag(accountUser.getAdminRoleFlag());
            acct.setUsername(accountUser.getUsername());
            accountUsersModel.add(acct);
        }
        return accountUsersModel;
    }

    @Override
    public AccountUser getAccountUser(AccountUserModel accountUser) {
        AccountUser acct = new AccountUser();
        acct.setStatus(acctStatusDao.getCachedObjectByName(accountUser.getStatus()));
        acct.setAccount(getAccount(accountUser.getAccount()));
        acct.setAccountUserId(accountUser.getAccountUserId());
        acct.setAdminRoleFlag(accountUser.getAdminRoleFlag());
        acct.setUsername(accountUser.getUsername());

        return acct;
    }

    @Override
    public AccountModelFullInfo getAccountModelFullInfo(Account account) {
        AccountModelFullInfo acctInfo = new AccountModelFullInfo(getAccountModel(account));

        // List<ProvisioningRequest> provisioningRequests = new ArrayList<>();
        // if (provRequests != null && !provRequests.isEmpty()) {
        // for (ProvRequest provRequestActive : provRequests) {
        // ProvisioningRequest provReq =
        // provConversion.getProvRequest(provRequestActive);
        // provisioningRequests.add(provReq);
        // }
        // }
        // acctInfo.setActiveProvRequests(provisioningRequests);
        List<AccountSender> senders = account.getSenders();
        List<String> acctSenders = new ArrayList<String>();
        if (senders != null && !senders.isEmpty()) {
            for (AccountSender accountSender : senders) {
                String sender = accountSender.getAccountSendersPK().getSenderName();
                acctSenders.add(sender);
            }
        }
        acctInfo.setSenders(acctSenders);

        List<IntraSender> intraSenders = account.getIntraSendersList();
        List<String> acctIntraSenders = new ArrayList<String>();
        if (intraSenders != null && !intraSenders.isEmpty()) {
            for (IntraSender accountIntraSender : intraSenders) {
                String sender = accountIntraSender.getSenderName();
                acctIntraSenders.add(sender);
            }
        }
        acctInfo.setIntraSenders(acctIntraSenders);
        acctInfo.setAccountUsers(getAccountUserModel(account.getAccountUsers()));
        return acctInfo;
    }

    @Override
    public AccountModelFullInfo getAccountModelFullInfo(AccountUser accountUsr) {
        AccountModelFullInfo acctInfo = new AccountModelFullInfo(getAccountModel(accountUsr));

        // List<ProvisioningRequest> provisioningRequests = new ArrayList<>();
        // if (provRequests != null && !provRequests.isEmpty()) {
        // for (ProvRequest provRequestActive : provRequests) {
        // ProvisioningRequest provReq =
        // provConversion.getProvRequest(provRequestActive);
        // provisioningRequests.add(provReq);
        // }
        // }
        // acctInfo.setActiveProvRequests(provisioningRequests);
        List<AccountSender> senders = accountUsr.getAccount().getSenders();
        List<String> acctSenders = new ArrayList<String>();
        if (senders != null && !senders.isEmpty()) {
            for (AccountSender accountSender : senders) {
                String sender = accountSender.getAccountSendersPK().getSenderName();
                acctSenders.add(sender);
            }
        }
        acctInfo.setSenders(acctSenders);

        List<IntraSender> intraSenders = accountUsr.getAccount().getIntraSendersList();
        List<String> acctIntraSenders = new ArrayList<String>();
        if (senders != null && !senders.isEmpty()) {
            for (IntraSender accountIntraSender : intraSenders) {
                String sender = accountIntraSender.getSenderName();
                acctIntraSenders.add(sender);
            }
        }
        acctInfo.setIntraSenders(acctIntraSenders);
        acctInfo.setAccountUsers(getAccountUserModel(accountUsr.getAccount().getAccountUsers()));
        return acctInfo;
    }

    @Override
    public Tier getTier(TierModel tierModel) {
        Tier tier = new Tier();
        tier.setTierId(tierModel.getTierId());
        tier.setTierName(tierModel.getTierName());
        tier.setQuota(tierModel.getQuota());
        tier.setDescription(tierModel.getDescription());
        tier.setRatePlan(tierModel.getRateplan());
        return tier;
    }

    @Override
    public TierModel getTierModel(Tier tier) {
        TierModel tierModel = new TierModel();
        tierModel.setTierId(tier.getTierId());
        tierModel.setTierName(tier.getTierName());
        tierModel.setQuota(tier.getQuota());
        tierModel.setDescription(tier.getDescription());
        tierModel.setRateplan(tier.getRatePlan());
        return tierModel;
    }

}
