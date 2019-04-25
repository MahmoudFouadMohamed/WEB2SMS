package com.edafa.web2sms.service.accountmanagement.remote;

import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountConversionBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountManegementBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AdminAccountManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.Remote.UserManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.Remote.AccountQuotaManagementBeanRemote;
import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Startup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mahmoud saad
 */
@Singleton
@DependsOn("ConfigsManagerBean")
@Startup
public class AccountManegementRemotePools implements AccountManegementRemotePoolsLocal, ConfigsListener {

    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

    @EJB
    ConfigsManagerBeanLocal configsManagerBean;

    private ObjectPool<AccountManegementBeanRemote> accountManegementBeanRemotePool;

    private ObjectPool<AdminAccountManagementBeanRemote> adminAccountManagementBeanRemotePool;

    private ObjectPool<AccountConversionBeanRemote> accountConversionBeanRemotePool;

    private ObjectPool<UserManagementBeanRemote> userManagementBeanRemotePool;

    private ObjectPool<AccountQuotaManagementBeanRemote> accountQuotaManagementBeanRemotePool;

    @PostConstruct
    public void init() {
        initPools();
        configsManagerBean.registerConfigsListener(ModulesEnum.ConnectAccoutManag, this);
    }

    public void initPools() {

        try {
            appLogger.info("Create AccountManegementBeanRemotePool success with MaxTotal: " + Configs.ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL.getValue() + " and MaxIdle: " + Configs.ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE.getValue());
            GenericObjectPoolConfig accountManegementBeanRemotePoolConfig = new GenericObjectPoolConfig();
            accountManegementBeanRemotePoolConfig.setMaxTotal((int) Configs.ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL.getValue());
            accountManegementBeanRemotePoolConfig.setMaxIdle((int) Configs.ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE.getValue());
            accountManegementBeanRemotePool = new GenericObjectPool<AccountManegementBeanRemote>(new AccountManegementBeanRemoteFactory(), accountManegementBeanRemotePoolConfig);
            appLogger.info("AccountManegementBeanRemotePool created successfully with Active: " + accountManegementBeanRemotePool.getNumActive() + " and Idle: " + accountManegementBeanRemotePool.getNumIdle());

        } catch (Exception e) {
            appLogger.error("Failed to create AccountManegementBeanRemotePool", e);
            throw e;
        }

        try {
            appLogger.info("Create AdminAccountManegementBeanRemotePool success with MaxTotal: " + Configs.ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXTOTAL.getValue() + " and MaxIdle: " + Configs.ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXIDLE.getValue());
            GenericObjectPoolConfig adminAccountManegementBeanRemotePoolConfig = new GenericObjectPoolConfig();
            adminAccountManegementBeanRemotePoolConfig.setMaxTotal((int) Configs.ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXTOTAL.getValue());
            adminAccountManegementBeanRemotePoolConfig.setMaxIdle((int) Configs.ACCOUNT_ADMIN_MANAGEMENT_REMOTE_POOL_MAXIDLE.getValue());
            adminAccountManagementBeanRemotePool = new GenericObjectPool<AdminAccountManagementBeanRemote>(new AdminAccountManegementBeanRemoteFactory(), adminAccountManegementBeanRemotePoolConfig);
            appLogger.info("AdminAccountManegementBeanRemotePool created successfully with Active: " + adminAccountManagementBeanRemotePool.getNumActive() + " and Idle: " + adminAccountManagementBeanRemotePool.getNumIdle());

        } catch (Exception e) {
            appLogger.error("Failed to create AdminAccountManagementBeanRemotePool", e);
            throw e;
        }

        try {
            appLogger.info("Create AccountConversionBeanRemotePool success with MaxTotal: " + Configs.ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL.getValue() + " and MaxIdle: " + Configs.ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE.getValue());
            GenericObjectPoolConfig accountConversionBeanRemotePoolConfig = new GenericObjectPoolConfig();
            accountConversionBeanRemotePoolConfig.setMaxTotal((int) Configs.ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL.getValue());
            accountConversionBeanRemotePoolConfig.setMaxIdle((int) Configs.ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE.getValue());
            accountConversionBeanRemotePool = new GenericObjectPool<AccountConversionBeanRemote>(new AccountConversionBeanRemoteFactory(), accountConversionBeanRemotePoolConfig);
            appLogger.info("AccountConversionBeanRemotePool created successfully with Active: " + accountConversionBeanRemotePool.getNumActive() + " and Idle: " + accountConversionBeanRemotePool.getNumIdle());

        } catch (Exception e) {
            appLogger.error("Failed to create AccountConversionBeanRemotePool", e);
            throw e;
        }

        try {
            appLogger.info("Create UserManagementBeanRemotePool success with MaxTotal: " + Configs.ACCOUNT_CONVERSION_REMOTE_POOL_MAXTOTAL.getValue() + " and MaxIdle: " + Configs.ACCOUNT_CONVERSION_REMOTE_POOL_MAXIDLE.getValue());
            GenericObjectPoolConfig userManagementBeanRemotePoolConfig = new GenericObjectPoolConfig();
            userManagementBeanRemotePoolConfig.setMaxTotal((int) Configs.ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXTOTAL.getValue());
            userManagementBeanRemotePoolConfig.setMaxIdle((int) Configs.ACCOUNT_USER_MANAGEMENT_REMOTE_POOL_MAXIDLE.getValue());
            userManagementBeanRemotePool = new GenericObjectPool<UserManagementBeanRemote>(new AccountUserManegementBeanRemoteFactory(), userManagementBeanRemotePoolConfig);
            appLogger.info("UserManagementBeanRemotePool created successfully with Active: " + userManagementBeanRemotePool.getNumActive() + " and Idle: " + userManagementBeanRemotePool.getNumIdle());

        } catch (Exception e) {
            appLogger.error("Failed to create userManagementBeanRemotePool", e);
            throw e;
        }

        try {
            appLogger.info("Create AccountQuotaManagementBeanRemotePool success with MaxTotal: " + Configs.ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXTOTAL.getValue() + " and MaxIdle: " + Configs.ACCOUNT_MANAGEMENT_REMOTE_POOL_MAXIDLE.getValue());
            GenericObjectPoolConfig accountQuotaManegementBeanRemotePoolConfig = new GenericObjectPoolConfig();
            accountQuotaManegementBeanRemotePoolConfig.setMaxTotal((int) Configs.ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXTOTAL.getValue());
            accountQuotaManegementBeanRemotePoolConfig.setMaxIdle((int) Configs.ACCOUNT_QUOTA_MANAGEMENT_REMOTE_POOL_MAXIDLE.getValue());
            accountQuotaManagementBeanRemotePool = new GenericObjectPool<AccountQuotaManagementBeanRemote>(new AccountQuotaManegementBeanRemoteFactory(), accountQuotaManegementBeanRemotePoolConfig);
            appLogger.info("AccountQuotaManagementBeanRemotePool created successfully with Active: " + accountQuotaManagementBeanRemotePool.getNumActive() + " and Idle: " + accountQuotaManagementBeanRemotePool.getNumIdle());

        } catch (Exception e) {
            appLogger.error("Failed to create AccountQuotaManagementBeanRemotePool", e);
            throw e;
        }
    }

    /**
     *
     * @return @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public AccountManegementBeanRemote getAccountManegementBeanRemote() throws Exception {
        return accountManegementBeanRemotePool.borrowObject();
    }

    /**
     *
     * @param submitSMSBeanRemote
     * @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public void returnAccountManegementBeanRemote(AccountManegementBeanRemote accountManegementBeanRemote) throws Exception {
        accountManegementBeanRemotePool.returnObject(accountManegementBeanRemote);
    }

    /**
     *
     * @return @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public AdminAccountManagementBeanRemote getAdminAccountManagementBeanRemote() throws Exception {
        return adminAccountManagementBeanRemotePool.borrowObject();
    }

    /**
     *
     * @param submitSMSBeanRemote
     * @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public void returnAdminAccountManagementBeanRemote(AdminAccountManagementBeanRemote adminAccountManagementBeanRemote) throws Exception {
        adminAccountManagementBeanRemotePool.returnObject(adminAccountManagementBeanRemote);
    }

    /**
     *
     * @return @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public AccountConversionBeanRemote getAccountConversionBeanRemote() throws Exception {
        return accountConversionBeanRemotePool.borrowObject();
    }

    /**
     *
     * @param submitSMSBeanRemote
     * @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public void returnAccountConversionBeanRemote(AccountConversionBeanRemote accountConversionBeanRemote) throws Exception {
        accountConversionBeanRemotePool.returnObject(accountConversionBeanRemote);
    }

    /**
     *
     * @return @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public UserManagementBeanRemote getUserManagementBeanRemote() throws Exception {
        return userManagementBeanRemotePool.borrowObject();
    }

    /**
     *
     * @param submitSMSBeanRemote
     * @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public void returnUserManagementBeanRemote(UserManagementBeanRemote userManagementBeanRemote) throws Exception {
        userManagementBeanRemotePool.returnObject(userManagementBeanRemote);
    }

    @Override
    @Lock(LockType.READ)
    public AccountQuotaManagementBeanRemote getAccountQuotaManagementBeanRemote() throws Exception {
        return accountQuotaManagementBeanRemotePool.borrowObject();
    }

    /**
     *
     * @param submitSMSBeanRemote
     * @throws Exception
     */
    @Override
    @Lock(LockType.READ)
    public void returnAccountQuotaManagementBeanRemote(AccountQuotaManagementBeanRemote accountQuotaManagementBeanRemote) throws Exception {
        accountQuotaManagementBeanRemotePool.returnObject(accountQuotaManagementBeanRemote);
    }

    @Override
    @Lock(LockType.READ)
    public String getAccountManegementBeanRemotePoolData() {
        return " NumActive: " + accountManegementBeanRemotePool.getNumActive() + " ,NumIdle: " + accountManegementBeanRemotePool.getNumIdle();
    }

    @Override
    public void configurationRefreshed(ModulesEnum module) {
        if (module == ModulesEnum.ConnectAccoutManag) {
            initPools();
        }
    }

    @Override
    public void configurationRefreshed() {
    }

}
