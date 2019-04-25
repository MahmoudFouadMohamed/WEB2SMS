package com.edafa.web2sms.acc_manag.service.account;

//import com.edafa.utils.timing.StopWatch;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager; 
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.AccountDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountQuotaDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSenderDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusUserActionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountTierDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.IntraSenderDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.QuotaHistoryDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.QuotaInquiryDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.TierDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountQuota;
import com.edafa.web2sms.dalayer.model.AccountSender;
import com.edafa.web2sms.dalayer.model.AccountSenderPK;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.IntraSender;
import com.edafa.web2sms.dalayer.model.QuotaHistory;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.dalayer.model.TierType;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountAlreadyActiveException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountStateException;
import com.edafa.web2sms.acc_manag.service.account.exception.NoAttachedSendersException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameAlreadyAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;

import com.edafa.web2sms.acc_manag.service.model.ProvisioningRequestInfo;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.QuotaHistoryModel;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.acc_manag.utils.sms.AccountManagMsisdnFormat;
import com.edafa.web2sms.acc_manag.utils.sms.SMSUtils;
import com.edafa.web2sms.acc_manag.utils.sms.SenderType;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidSMSSender;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountConversionBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.AccountManegementServiceBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountManegementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AdminAccountManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.local.UserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.model.PrivilegeModel;
import com.edafa.web2sms.dalayer.dao.AccountGroupDao;
import com.edafa.web2sms.dalayer.dao.ActionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.PrivilegeDaoLocal;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.Privilege;
import com.edafa.web2sms.dalayer.model.constants.AccountGroupConst;
import com.edafa.web2sms.dalayer.model.constants.PrivilegeConst;
import javax.annotation.PostConstruct;


/**
 * Session Bean implementation class AccountManagementBean
 */
@Stateless
@LocalBean
public class AccountManegementBean implements AccountManegementServiceBeanLocal, AdminAccountManagementBeanLocal, AccountManegementBeanLocal {

	Logger appLogger;
	Logger acctLogger;
	//Logger adminLogger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());
	private final String PROV_TRX_ID_PREFIX = "2";

	@EJB
	AccountDaoLocal accountDao;
        
        @EJB
        ActionDaoLocal actionDao;
        
	@EJB
	AccountUserDaoLocal accountUserDao;

	@EJB
	AccountSenderDaoLocal accountSender;

	@EJB
        AccountConversionBeanLocal acctConversionBean;

	@EJB
	AccountStatusDaoLocal accountStatusDao;

	@EJB
	AccountStatusUserActionDaoLocal accountStatusUserActionDao;

	@EJB
	TierDaoLocal tierDao;

	@EJB
	AccountSenderDaoLocal accountSenderDao;

	@EJB
	QuotaInquiryDaoLocal quotaInquiryDao;

	@EJB
	QuotaHistoryDaoLocal quotaHistoryDao;

	@EJB
	UserManagementBeanLocal UserManagementBean;

	@EJB
	IntraSenderDaoLocal intraSenderDao;

	@EJB
	AccountTierDaoLocal accountTiersDao;

	@EJB
	AccountQuotaDaoLocal accountQuotaDao;
        
        @EJB
        AccountGroupDao accountGroupDao;
                       
        @EJB
        PrivilegeDaoLocal privilegeDao;
//	@EJB
//	ServiceProvisioningLocal serviceProvisioning;

	static Map<AccountStatusName, Set<ProvRequestTypeName>> validAccountProvActions;
	static Map<AccountStatusName, Set<ActionName>> validAccountUserActions;

	static {
		validAccountProvActions = new HashMap<>();
		Set<ProvRequestTypeName> validProvActions;

		validProvActions = new HashSet<>();
		validProvActions.add(ProvRequestTypeName.SUSPEND_ACCOUNT);
		validProvActions.add(ProvRequestTypeName.UPGRADE_ACCOUNT);
		validProvActions.add(ProvRequestTypeName.DOWNGRADE_ACCOUNT);
		validProvActions.add(ProvRequestTypeName.DEACTIVATE_ACCOUNT);
		validAccountProvActions.put(AccountStatusName.ACTIVE, validProvActions);

		validProvActions = new HashSet<>();
		validProvActions.add(ProvRequestTypeName.REACTIVATE_ACCT_AFTER_SUSPENSION);
		validProvActions.add(ProvRequestTypeName.DEACTIVATE_ACCOUNT);
		validAccountProvActions.put(AccountStatusName.SUSPENDED, validProvActions);

		validProvActions = new HashSet<>();
		validProvActions.add(ProvRequestTypeName.REACTIVATE_ACCT_AFTER_SUSPENSION);
		// validProvActions.add(ProvisioningAction.DEACTIVATE);
		validAccountProvActions.put(AccountStatusName.INACTIVE, validProvActions);

	}

	public AccountManegementBean() {
	}
        
        @PostConstruct
        public void initLoggers() {
            appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
            acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
        }

	@Override
	public Account getAccount(String accountId) {
		return new Account(accountId);
	}

    @Override
    public List<ActionName> checkAccountAndUserEligibility(AccountUserTrxInfo userTrxInfo) throws DBException, IneligibleAccountException {
        String accountId = userTrxInfo.getUser().getAccountId();
        String userName = userTrxInfo.getUser().getUsername();
        List<ActionName> requiredActionNames = userTrxInfo.getUserActions();
        //Account account = accountDao.findByIdAndUserNameAndAction(accountId, userName, actionName);
        List<ActionName> allowedActionNames = actionDao.getUserAllowedActions(accountId, userName);

        if (allowedActionNames == null) {
            acctLogger.info(userTrxInfo.logId() + "AccountUser(" + userName + "), account(" + accountId + ")"
                    + " is ineligible to  [" + requiredActionNames + "]");
            throw new IneligibleAccountException("AccountUser(" + userName + "), account(" + accountId + ")"
                    + " is ineligible to  [" + requiredActionNames + "]");
        } else {
            allowedActionNames.retainAll(requiredActionNames);
            if (allowedActionNames.isEmpty()) {
                acctLogger.info(userTrxInfo.logId() + "AccountUser(" + userName + "), account(" + accountId + ")"
                        + " is ineligible to  [" + requiredActionNames + "]");
                throw new IneligibleAccountException("AccountUser(" + userName + "), account(" + accountId + ")"
                        + " is ineligible to  [" + requiredActionNames + "]");
            } else {
                if (allowedActionNames.size() >= requiredActionNames.size()) {
                    acctLogger.info(userTrxInfo.logId() + "AccountUser(" + userName + "), account(" + accountId + ")"
                            + " is eligible to  [" + allowedActionNames + "]");
                } else {
                    List<ActionName> notAllowedActionNames = new ArrayList<>(requiredActionNames);
                    notAllowedActionNames.removeAll(allowedActionNames);
                    acctLogger.info(userTrxInfo.logId() + "AccountUser(" + userName + "), account(" + accountId + ")"
                            + " is eligible to  [" + allowedActionNames + "] and ineligible to [" + notAllowedActionNames + "]");
                }
            }
        }    
        return allowedActionNames;
    }

    @Override
    public Account checkAccountEligibilitySMSAPI(String trxId, String accountId, ActionName userActionName, int timeOut) throws IneligibleAccountException, DBException {
        if (acctLogger.isDebugEnabled()) {
            acctLogger.debug(trxId + "Fetch account id=" + accountId + " from DB");
        }
        Account account = accountDao.findWithSmsApiByIdAndAction(accountId, ActionName.SEND_SMS, timeOut);
        if (account == null) {
            throw new IneligibleAccountException("This account is ineligible to [" + userActionName.toString() + "]");
        }
//        checkAccountEligibility(trxId, account, userActionName);
        return account;
    }

    @Override
    public Account checkAccountEligibilitySMSAPICamp(String trxId, String accountId, ActionName userActionName, int timeOut) throws IneligibleAccountException, DBException {
        if (acctLogger.isDebugEnabled()) {
            acctLogger.debug(trxId + "Fetch account id=" + accountId + " from DB");
        }
        List<ActionName> actionNames = new ArrayList<ActionName>();
        actionNames.add(ActionName.SEND_SMS);
        actionNames.add(ActionName.CREATE_CAMPAIGN);
        Account account = accountDao.findWithSmsApiCampByIdAndAction(accountId, actionNames, timeOut);
        if (account == null) {
            throw new IneligibleAccountException("This account is ineligible to [" + userActionName.toString() + "]");
        }
//        checkAccountEligibility(trxId, account, userActionName);
        return account;

//	public void checkAdminEligibility(AccountAdminTrxInfo adminTrxInfo) throws IneligibleAccountException {
        // throw new IneligibleAccountException();
    }

	@Override
	public void checkAccountState(AccountProvTrxInfo trxInfo, AccountStatusName acctStatusName)
			throws InvalidAccountStateException {
		ProvRequestTypeName provReqType = trxInfo.getProvReqType();
		acctLogger.debug(trxInfo.logId() + "Check account(" + trxInfo.getAccountId()
				+ ") state for provisioning action " + provReqType);
		AccountStatusName statusName = acctStatusName;

		acctLogger.trace(trxInfo.logId() + "Account status = " + statusName);

		Set<ProvRequestTypeName> validProvActions = validAccountProvActions.get(statusName);

		if (!validProvActions.contains(trxInfo.getProvReqType())) {
			acctLogger.info(trxInfo.logId() + "Account with status= " + statusName + " is not valid for action "
					+ provReqType);
			throw new InvalidAccountStateException(statusName, provReqType);
		}
	}

	public boolean checkAccountExistance(String accountId) throws DBException {
		return accountDao.count(accountId) == 1;
	}

//	public boolean checkAccountSender(String sender) throws DBException {
//		return accountSender.CountBySenderName(sender) == 1;
//	}

	@Override
	public AccountStatusName getAccountStatus(String accountId) throws DBException {
		try {
			Account account = accountDao.findByAccountId(accountId);
			return account.getStatus().getAccountStatusName();
		} catch (DBException e) {
//			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public Account findAccountById(AccountTrxInfo trxInfo, String accountId) throws DBException, AccountNotFoundException {
		acctLogger.debug(trxInfo.logId() + "Retriving account with id (" + accountId + ")");
		Account acct = accountDao.findByAccountId(accountId);
		acctLogger.trace(trxInfo.logId() + "Retrived account with id (" + accountId + ")");
		if (acct == null) {
			acctLogger.error(trxInfo.logId() + "Account with id (" + accountId + ") is not found");
			throw new AccountNotFoundException(accountId);
		}

		return acct;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProvisioningRequestInfo activateNewAccount(AccountProvTrxInfo provTrxInfo, Account acct, String acctAdmin) throws DBException,
			InvalidAccountException, AccountAlreadyActiveException {
		provTrxInfo.setProvReqType(ProvRequestTypeName.ACTIVATE_ACCOUNT);
		acctLogger.info(provTrxInfo.logInfo() + "Account: " + acct.logInfo());
		// if (!acct.isValid()) {
		// acctLogger.error(provTrxInfo.logInfo() + "invalid account");
		// throw new InvalidAccountException();
		// }
		// The account id is the same as company id
		// TODO: check the rest of account params (msisdn, domainname, ..)
		boolean exist = checkAccountExistance(acct.getAccountId());
		// String accountId =
		// accountDao.getAccountIdByCompanyId(acct.getCompanyId());
		if (exist) {
			AccountAlreadyActiveException e = new AccountAlreadyActiveException(acct.getAccountId());
			acctLogger.error(provTrxInfo.logId() + e.getMessage());
			throw e;
		}

		// Account account = acctConversionBean.getAccount(acct);
		acct.setCompanyName(acct.getCompanyName().toLowerCase());
		// acct.setAccountAdmin(acct.getAccountAdmin().toLowerCase());

		acctLogger.trace(provTrxInfo.logId() + "set account status to " + AccountStatusName.ACTIVE);
		acct.setStatus(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
		acctLogger.debug(provTrxInfo.logId() + "persisting the new account into database");
		try {
			accountDao.create(acct);
			acctLogger.info(provTrxInfo.logId() + "the account is created and activated successfully with id: "
					+ acct.getAccountId());
			acctLogger.debug(provTrxInfo.logId() + "persisting quotaHoistoty record for new account into database");
			QuotaHistory quotahist = new QuotaHistory(acct.getAccountId());
			quotahist.setUpdateTimestamp(new Date());
			quotaHistoryDao.create(quotahist);
			acctLogger.info(provTrxInfo.logId() + "quotaHoistoty record is created successfully");

			// filing account quota and tier quota in case of one off..
			if (acct.getTier().getTierType().getTierTypeName() == TierTypesEnum.PREPAID) {

				// expire Date after activate account .
				Date expairyDate = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(expairyDate);
				c.add(Calendar.DAY_OF_MONTH, acct.getTier().getExpairyDays());
				expairyDate = c.getTime();

				// String accoutId = acct.getAccountId();
				Integer tierTypeId = acct.getTier().getTierType().getTierTypeId();

				AccountTier accountTier = new AccountTier();
				accountTier.setAccountId(acct);
				accountTier.setTierId(acct.getTier());
				accountTier.setTierId(acct.getTier());
				accountTier.setBillingMsisdn(acct.getBillingMsisdn());
				accountTiersDao.create(accountTier);

				AccountQuota accQuota = new AccountQuota(acct.getTier().getTierType().getTierTypeId(), 0, 0,
						expairyDate);
				accQuota.setAccountTiersId(accountTier.getAccountTiersId());
				accountQuotaDao.create(accQuota);

			}
                    acctLogger.debug(provTrxInfo.logId() + "Create " + AccountGroupConst.GROUPS_ADMINS_GROUP_NAME);
                    AccountGroup groupsAdminsGroup = new AccountGroup();
                    groupsAdminsGroup.setAccount(acct);
                    groupsAdminsGroup.setGroupName(AccountGroupConst.GROUPS_ADMINS_GROUP_NAME);
                    groupsAdminsGroup.setCreationDate(new Date());
                    groupsAdminsGroup.setHiddenFlag(true);    
                    groupsAdminsGroup.setDefaultFlag(false);
                    
                    Privilege groupsAdminsPrivilege = privilegeDao.findByPrivilegeName(PrivilegeConst.GROUPS_ADMINS_PRIVILEGE);
                    List<Privilege> privileges = new ArrayList<>();
                    privileges.add(groupsAdminsPrivilege);
                    groupsAdminsGroup.setPrivileges(privileges);
                    accountGroupDao.create(groupsAdminsGroup);
                        
                    acctLogger.debug(provTrxInfo.logId() + "Create " + AccountGroupConst.ADMINS_GROUP_NAME);
                    AccountGroup adminsGroup = new AccountGroup();
                    adminsGroup.setAccount(acct);
                    adminsGroup.setGroupName(AccountGroupConst.ADMINS_GROUP_NAME);
                    adminsGroup.setCreationDate(new Date());
                    adminsGroup.setHiddenFlag(false);
                    adminsGroup.setDefaultFlag(true);
                    adminsGroup.setPrivileges(privilegeDao.findByHiddenFlag(false));
                    accountGroupDao.create(adminsGroup);                    

                    List<AccountGroup> userGroups = new ArrayList<>();
                    userGroups.add(adminsGroup);
                    
                    acctAdmin = acctAdmin.substring(0, acctAdmin.indexOf('@'));
                    UserManagementBean.activateAdminUser(provTrxInfo, acctAdmin.toLowerCase(), userGroups);
                    
                } catch (DBException e) {
			appLogger.error(provTrxInfo.logId() + "failed to persist the new account into database ", e);
			acctLogger.error(provTrxInfo.logId() + "failed to persist the new account into database " + e.getMessage());
			throw e;
		}
		// } catch (AdminAlreadyGrantedException e) {
		// // This will never happens
		// appLogger.error(provTrxInfo.logId() +
		// "failed to persist the new account into database ", e);
		// acctLogger.error(provTrxInfo.logId() +
		// "failed to persist the new account into database " + e.getMessage());
		// }

		return createProvisioningRequestInfo(provTrxInfo, acct.getStatus().getAccountStatusName(), acct.getStatus().getAccountStatusName(),
				null, acctConversionBean.getTierModel(acct.getTier()));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProvisioningRequestInfo migrateAccount(AccountProvTrxInfo provTrxInfo, Tier newTier) throws DBException, AccountNotFoundException,
			InvalidAccountStateException {

		acctLogger.info(provTrxInfo.logInfo() + "Migrate account");
		acctLogger.info(provTrxInfo.logId() + "Find account(" + provTrxInfo.getAccountId() + ")");
		Account acct = findAccountById(provTrxInfo, provTrxInfo.getAccountId());

		checkAccountState(provTrxInfo, acct.getStatus().getAccountStatusName());

		acctLogger.debug(provTrxInfo.logId() + "Merging the account into database");
		Tier oldTier = acct.getTier();
		try {
			acctLogger.info(provTrxInfo.logId() + "Migrating the account from " + oldTier.logInfo() + " to "
					+ newTier.logInfo());
			acct.setTier(newTier);
			accountDao.edit(acct);
			acctLogger.info(provTrxInfo.logId() + "The account is migrated");
		} catch (DBException e) {
			appLogger.error(provTrxInfo.logId() + "Failed to persist the new account into database ", e);
			acctLogger.error(provTrxInfo.logId() + "Failed to persist the new account into database ", e);
			throw e;
		}

		return createProvisioningRequestInfo(provTrxInfo, acct.getStatus().getAccountStatusName(), acct.getStatus().getAccountStatusName(),
				acctConversionBean.getTierModel(oldTier), acctConversionBean.getTierModel(newTier));

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProvisioningRequestInfo deactivateAccount(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException,
			InvalidAccountStateException {
		String acctId = trxInfo.getAccountId();
		if (acctId == null)
			throw new NullPointerException("Account Id should not be null");

		// Setting the provisioning action
		trxInfo.setProvReqType(ProvRequestTypeName.DEACTIVATE_ACCOUNT);
		Account account;
		AccountStatus oldStatus;
		AccountStatus newStatus;
		acctLogger.info(trxInfo.logInfo());
		try {
			acctLogger.debug(trxInfo.logId() + "Getting account with id " + acctId);
			account = findAccountById(trxInfo, acctId);
			checkAccountState(trxInfo, account.getStatus().getAccountStatusName());

			oldStatus = account.getStatus();
			newStatus = accountStatusDao.getCachedObjectByName(AccountStatusName.INACTIVE);
			// accountDao.updateAccountStatus(acctId, newStatus);

			// if account tier is prepaid, delete account
			// if (account.getTier().getTierType().getTierTypeName() ==
			// TierTypesEnum.PREPAID) {
			// // accountUserDao
			// accountDao.remove(account);
			//
			// } else { // it's post paid so just change the status
			account.setStatus(newStatus);
			accountDao.edit(account);

			acctLogger.info(trxInfo.logId() + "Updated account status to "
					+ newStatus.getAccountStatusName().toString());
			// }

		} catch (DBException e) {
			appLogger.error(trxInfo.logId() + "Database error: ", e);
			acctLogger.error(trxInfo.logId() + "Database error: " + e.getMessage());
			throw e;
		}
		try {
			List<AccountStatus> statuses = new ArrayList<>();
			statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
			statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.SUSPENDED));
			acctLogger.debug(trxInfo.logId() + "Getting users of account(" + acctId + ") with status: " + statuses);
			List<AccountUser> accountUserList = UserManagementBean.getAccountUsers(trxInfo, statuses);
			acctLogger.debug(trxInfo.logId() + "Found (" + accountUserList.size() + ") user(s)");
			for (AccountUser accountUser : accountUserList) {
				accountUser.setStatus(newStatus);
				accountUserDao.edit(accountUser);
			}
			acctLogger.debug(trxInfo.logId() + "Status updated to Inactive for all active and suspended user(s)");
		} catch (UserNotFoundException e) {
			appLogger.error(trxInfo.logId() + "No active or suspended user for this account: ", e);
			acctLogger.error(trxInfo.logId() + e.getMessage());
		}

		TierModel tier = acctConversionBean.getTierModel(account.getTier());
		return createProvisioningRequestInfo(trxInfo, oldStatus.getAccountStatusName(), newStatus.getAccountStatusName(), tier, tier);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProvisioningRequestInfo suspendAccount(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException,
			InvalidAccountStateException {
		String acctId = trxInfo.getAccountId();
		if (acctId == null)
			throw new NullPointerException("Account Id should not be null");

		// Setting the provisioning action
		trxInfo.setProvReqType(ProvRequestTypeName.SUSPEND_ACCOUNT);

		acctLogger.info(trxInfo.logInfo());
		Account account;
		AccountStatus oldStatus;
		AccountStatus newStatus;
		try {
			acctLogger.debug(trxInfo.logId() + "Getting account with id " + acctId);
			account = findAccountById(trxInfo, acctId);
			checkAccountState(trxInfo, account.getStatus().getAccountStatusName());

			oldStatus = account.getStatus();
			newStatus = accountStatusDao.getCachedObjectByName(AccountStatusName.SUSPENDED);
			// accountDao.updateAccountStatus(acctId, newStatus);
			account.setStatus(newStatus);
			accountDao.edit(account);

			acctLogger.info(trxInfo.logId() + "updated account status to "
					+ newStatus.getAccountStatusName().toString());
		} catch (DBException e) {
			appLogger.error(trxInfo.logId() + "database error: ", e);
			acctLogger.error(trxInfo.logId() + "database error: " + e.getMessage());
			throw e;
		}

		try {
			List<AccountStatus> statuses = new ArrayList<>();
			statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
			acctLogger.debug(trxInfo.logId() + "Getting users of account(" + acctId + ") with status: " + statuses);
			List<AccountUser> accountUserList = UserManagementBean.getAccountUsers(trxInfo, statuses);
			acctLogger.debug(trxInfo.logId() + "Found (" + accountUserList.size() + ") user(s)");
			for (AccountUser accountUser : accountUserList) {
				accountUser.setStatus(newStatus);
				accountUserDao.edit(accountUser);
			}
			acctLogger.debug(trxInfo.logId() + "Status updated to suspended for all active user(s)");

		} catch (UserNotFoundException e) {
			appLogger.error(trxInfo.logId() + "No active or suspended user for this account: ", e);
			acctLogger.error(trxInfo.logId() + e.getMessage());
		}

		TierModel tier = acctConversionBean.getTierModel(account.getTier());
		return createProvisioningRequestInfo(trxInfo, oldStatus.getAccountStatusName(), newStatus.getAccountStatusName(), tier, tier);
	}

	private ProvisioningRequestInfo createProvisioningRequestInfo (AccountProvTrxInfo trxInfo, AccountStatusName oldStatus, AccountStatusName newStatus,
			TierModel oldTier, TierModel newTier) {
		ProvisioningRequestInfo provisioningRequestInfo = new ProvisioningRequestInfo();
		provisioningRequestInfo.setAccountId(trxInfo.getAccountId());
		provisioningRequestInfo.setOldStatus(oldStatus);
		provisioningRequestInfo.setNewStatus(newStatus);
		provisioningRequestInfo.setOldTier(oldTier);
		provisioningRequestInfo.setNewTier(newTier);
		provisioningRequestInfo.setTimestamp(new Date());
//		acctLogger.info(trxInfo.logId() + "Throw provisioning event");
//		eventListener.handleProvisioningEvent(provisioningRequestInfo);
                return provisioningRequestInfo;

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ProvisioningRequestInfo reactivateAccountAfterSuspension(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException,
			InvalidAccountStateException {
		String acctId = trxInfo.getAccountId();
		if (acctId == null)
			throw new NullPointerException("Account Id should not be null");

		// Setting the provisioning action
		trxInfo.setProvReqType(ProvRequestTypeName.REACTIVATE_ACCT_AFTER_SUSPENSION);

		acctLogger.info(trxInfo.logInfo());
		Account account;
		AccountStatus oldStatus;
		AccountStatus newStatus;
		try {
			acctLogger.debug(trxInfo.logId() + "Retrieve account with id " + acctId);
			account = findAccountById(trxInfo, acctId);
			checkAccountState(trxInfo, account.getStatus().getAccountStatusName());

			oldStatus = account.getStatus();
			newStatus = accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE);
			account.setStatus(newStatus);
			accountDao.edit(account);

			acctLogger.info(trxInfo.logId() + "Updated account status to "
					+ newStatus.getAccountStatusName().toString());
		} catch (DBException e) {
			appLogger.error(trxInfo.logId() + "Database error: ", e);
			acctLogger.error(trxInfo.logId() + "Database error: " + e.getMessage());
			throw e;
		}

		try {
			List<AccountStatus> statuses = new ArrayList<>();
			statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.SUSPENDED));
			acctLogger.debug(trxInfo.logId() + "Getting users of account(" + acctId + ") with status: " + statuses);
			List<AccountUser> accountUserList = UserManagementBean.getAccountUsers(trxInfo, statuses);
			acctLogger.debug(trxInfo.logId() + "Found (" + accountUserList.size() + ") user(s)");
			for (AccountUser accountUser : accountUserList) {
				accountUser.setStatus(newStatus);
				accountUserDao.edit(accountUser);
			}
			acctLogger.debug(trxInfo.logId() + "Status updated to active for all suspended user(s)");

		} catch (UserNotFoundException e) {
			appLogger.error(trxInfo.logId() + "No active or suspended user for this account: ", e);
			acctLogger.error(trxInfo.logId() + e.getMessage());
		}

		TierModel tier = acctConversionBean.getTierModel(account.getTier());
		return createProvisioningRequestInfo(trxInfo, oldStatus.getAccountStatusName(), newStatus.getAccountStatusName(), tier, tier);

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public AccountModel findAccountByCoName(AccountTrxInfo trxInfo, String companyName) throws DBException,
			AccountNotFoundException {
		acctLogger.info(trxInfo.logId() + "companyName: " + companyName.toLowerCase());
		Account acct = accountDao.findByCompanyName(companyName);

		if (acct == null) {
			throw new AccountNotFoundException();
		}

		AccountModel account = acctConversionBean.getAccountModel(acct);
		return account;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public AccountModelFullInfo findAccountByCoNameFullInfo(AccountUserTrxInfo trxInfo, String companyName)
			throws DBException, AccountNotFoundException {
		AccManagUserModel usr = trxInfo.getUser();
		acctLogger.info(trxInfo.logInfo() + "companyName: " + companyName);
		Account acct = accountDao.findByCompanyName(companyName.toLowerCase());
		acctLogger.debug(trxInfo.logId() + "account: " + acct + " will converted to full info model.");

		if (acct == null) {
			throw new AccountNotFoundException();
		}
		acctLogger.debug(trxInfo.logId() + "finding account username: " + usr.getUsername() + ".");
		List<AccountStatus> statuses = new ArrayList<>();
		statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
		AccountUser acctUser = accountUserDao.findAccountUser(acct.getAccountId(), usr.getUsername(), statuses);

		if (acctUser == null) {
			acctLogger.debug(trxInfo.logId() + "account user: " + acctUser + " not found.");
			throw new AccountNotFoundException();
		}

		List<IntraSender> intraSenderSys = intraSenderDao.findSystemIntraSendersList();
		AccountModelFullInfo account = acctConversionBean.getAccountModelFullInfo(acctUser);
		if (account != null && intraSenderSys != null) {
			List<String> intraSenders = new ArrayList<String>();
			for (int i = 0; i < intraSenderSys.size(); i++) {
				intraSenders.add(intraSenderSys.get(i).getSenderName());
			}
			account.getIntraSenders().addAll(intraSenders);
		}
		acctLogger.debug(trxInfo.logId() + "account user: " + acctUser + " converted to account model full info: "
				+ account);

		return account;
	}

	// Need to refine it to accept userName
	@Override
	public AccountModel findAccountByCoAdmin(AccountTrxInfo trxInfo, String accountAdmin) throws DBException,
			AccountNotFoundException {
		Account acct = accountDao.findByCompanyName(accountAdmin.toLowerCase());
		if (acct == null)
			throw new AccountNotFoundException();

		return acctConversionBean.getAccountModel(acct);
	}

	@Override
	public AccountModelFullInfo findAccountByCoAdminFullInfo(AccountTrxInfo trxInfo, String accountAdmin) throws DBException,
			AccountNotFoundException {
		if (accountAdmin != null) {
			acctLogger.info(trxInfo.logId() + "Searching for account: " + accountAdmin);
			Account acct = accountDao.findByCompanyName(accountAdmin.toLowerCase());
			if (acct == null) {
				acctLogger.error(trxInfo.logId() + "Account with company name: " + accountAdmin + " not found");
				throw new AccountNotFoundException();
			}

			acctLogger.info(trxInfo.logId() + "Account: " + acct);

			return acctConversionBean.getAccountModelFullInfo(acct);
		}
		throw new AccountNotFoundException();
	}

	@Override
	public AccountModelFullInfo findAccountByMSISDNFullInfo(AccountTrxInfo trxInfo, String msisdn) throws DBException,
			AccountNotFoundException {
		acctLogger.info(trxInfo.logId() + "Searching for account by Billing MSISDN: " + msisdn);
		Account acct = accountDao.findByBillingMSISDN(msisdn);
		if (acct == null) {
			acctLogger.error(trxInfo.logId() + "Account with Billing MSISDN: " + msisdn + " not found");
			throw new AccountNotFoundException();
		}
		acctLogger.info(trxInfo.logId() + "Account: " + acct);

		return acctConversionBean.getAccountModelFullInfo(acct);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public TierModel getRateplanTierMappingModel(AccountProvTrxInfo provTrxInfo, String ratePlan)
			throws TierNotFoundException, DBException {
		acctLogger.info(provTrxInfo.logInfo() + "get rate plan tier mapping for rate plan: " + ratePlan);
		TierModel tierModel = null;
		try {
			Tier tier = tierDao.findByRatePlan(ratePlan);
			acctLogger.debug(provTrxInfo.logId() + "found tier rate plan mapping: " + tier
					+ " will convert it tier model");
			tierModel = acctConversionBean.getTierModel(tier);
			acctLogger.debug(provTrxInfo.logId() + "found tier rate plan mapping tierId: " + tier.getTierId());
		} catch (DBException e) {
			String logMsg = provTrxInfo.logId() + "failed to get rate plan tier mapping from database";
			appLogger.error(logMsg, e);
			acctLogger.error(logMsg + ": " + e.getLocalizedMessage());
			throw e;
		}
		if (tierModel == null)
			throw new TierNotFoundException(null, "No mapping for the rate plan: " + ratePlan);

		return tierModel;
	}

	@Override
	public Tier getRateplanTierMapping(AccountProvTrxInfo provTrxInfo, String ratePlan) throws TierNotFoundException,
			DBException {
		acctLogger.info(provTrxInfo.logInfo() + "Find rate plan tier mapping for rate plan: " + ratePlan);
		Tier tier = null;
		try {
			tier = tierDao.findByRatePlan(ratePlan);
		} catch (DBException e) {
			String logMsg = provTrxInfo.logId() + "Failed to get rate plan tier mapping from database";
			appLogger.error(logMsg, e);
			acctLogger.error(logMsg + ": " + e.getMessage());
			throw e;
		}
		if (tier == null) {
			TierNotFoundException e = new TierNotFoundException(null, "No mapping for the rate plan: " + ratePlan);
			acctLogger.info(provTrxInfo.logId() + e.getMessage());
			throw e;
		}

		acctLogger.debug(provTrxInfo.logId() + "Found rate plan tier mapping tierId: " + tier.getTierId());
		return tier;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public AccountSender addAccountSenderName(AccountProvTrxInfo provTrxInfo, String senderName)
			throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, Exception {
		String accountId = provTrxInfo.getAccountId();
		acctLogger.info(provTrxInfo.logInfo() + "adding sender name (" + senderName + ") to account " + accountId);

		acctLogger.info(provTrxInfo.logId() + "Validating the sender name");
		validateSMSSender(provTrxInfo, senderName);

		// TODO MFarouk: As per Vodafone requirements, it is needed to allow the same sender name
		// for multiple accounts.
		AccountSender acctSender = accountSenderDao.findByAccountIdAndSenderName(accountId, senderName);
		if (acctSender != null) {
			SenderNameAlreadyAttached e = new SenderNameAlreadyAttached(senderName, acctSender.getAccountSendersPK()
					.getAccountId());
			acctLogger.info(provTrxInfo.logId() + e.getMessage());
			throw e;
		}
		
		// TODO MFarouk: Enhance Log
		acctLogger.info(provTrxInfo.logId() + "Found acctSender=" + acctSender);
		// 

		acctSender = persistSenderName(provTrxInfo, senderName);
		return acctSender;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public AccountSender changeAccountSenderName(AccountProvTrxInfo trxInfo, String oldSenderName, String newSenderName)
			throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, SenderNameNotAttached {

		String accountId = trxInfo.getAccountId();
		acctLogger.info(trxInfo.logInfo() + "changing account sender name (" + oldSenderName + ") to new sender name ("
				+ newSenderName + ") accountId " + accountId);

		acctLogger.debug(trxInfo.logId() + "Validating the sender name");
		SenderType senderType;
		if (newSenderName != null && !newSenderName.isEmpty()) {
			senderType = SMSUtils.getSenderType(newSenderName);
		} else {
			throw new AccountManagInvalidSMSSender("Invalid SMS new Sender(" + newSenderName + ").");
		}
		
		if (!senderType.equals(SenderType.ALPHANUMERIC)) {
			throw new AccountManagInvalidSMSSender("SMS sender name should be alphanumeric");
		}

		acctLogger.debug(trxInfo.logId() + "Checking new sender name availability");
		AccountSender newAcctSender = accountSenderDao.findByAccountIdAndSenderName(accountId,newSenderName);
		if (newAcctSender != null) {
//			if (newAcctSender.getAccountSendersPK().getAccountId().equals(trxInfo.getAccountId())) {
				acctLogger.warn(trxInfo.getAccountId() + "The new sender name is already attached");
				return newAcctSender;
//			} 
//			else {
//				SenderNameAlreadyAttached e = new SenderNameAlreadyAttached(newSenderName, newAcctSender
//						.getAccountSendersPK().getAccountId());
//				acctLogger.error(trxInfo.logId() + e.getMessage());
//				throw e;
//			}
		} 
//		else {
			// has no new sender so throw exception
//		}

		if (oldSenderName != null && !oldSenderName.isEmpty()) {
			acctLogger.debug(trxInfo.logId() + "Check old sender owner account");
			AccountSender oldAcctSender = accountSenderDao.findByAccountIdAndSenderName(accountId,oldSenderName);

			if (oldAcctSender != null) {
//				if (!oldAcctSender.getAccountSendersPK().getAccountId().equals(trxInfo.getAccountId())) {
//					SenderNameAlreadyAttached e = new SenderNameAlreadyAttached(oldSenderName, oldAcctSender
//							.getAccountSendersPK().getAccountId());
//					acctLogger.error(trxInfo.logId() + e.getMessage());
//					throw e;
//				} else {
					acctLogger.debug(trxInfo.logId() + "Old sender found and will be deleted");
					accountSenderDao.remove(oldAcctSender);
					acctLogger.debug(trxInfo.logId() + "Old sender is deleted");

//				}
			} else {
				SenderNameNotAttached e = new SenderNameNotAttached(oldSenderName);
				acctLogger.error(trxInfo.logId() + e.getMessage());
				throw e;
			}

		} else {
			int accountSenderCount= accountSenderDao.CountByAccountId(accountId);
			if (accountSenderCount == 1) {
				// Remove last old senders
				acctLogger.warn(trxInfo.logId() + "Old sender not found, last account sender "
						+ "will be deleted and replaced by the new sender");
				accountSenderDao.removeAllByAccountId(trxInfo.getAccountId());
				acctLogger.debug(trxInfo.logId() + "All account sender(s) are deleted");
			} else {
				acctLogger.error(trxInfo.logId() + "Old sender not found, request failed");
				SenderNameNotAttached e = new SenderNameNotAttached();
				throw e;
			}
		}

		newAcctSender = persistSenderName(trxInfo, newSenderName);
		return newAcctSender;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void deleteAccountSenderName(AccountProvTrxInfo trxInfo, String senderName) throws DBException,
			SenderNameNotAttached, IneligibleAccountException {
		String accountId = trxInfo.getAccountId();
		acctLogger.info(trxInfo.logInfo() + "Deleteing sender name (" + senderName + ") from account " + accountId);

		AccountSender acctSender = accountSenderDao.findByAccountIdAndSenderName(trxInfo.getAccountId(), senderName);
		if (acctSender == null) {
			SenderNameNotAttached e = new SenderNameNotAttached(senderName);
			throw e;

		}
		int senderCount = accountSenderDao.CountByAccountId(trxInfo.getAccountId());
		acctLogger.debug(trxInfo.logId() + senderCount + " Sender(s) for Account(" + trxInfo.getAccountId()
				+ ") Found.");
		if (senderCount > 1) {
			accountSenderDao.remove(acctSender);
			acctLogger.info(trxInfo.logId() + "Sender: " + senderName + " deleted Successfully.");
		} else {
			throw new IneligibleAccountException("Account must have more than one sender to delete senders");

		}

	}

	/*
	 * @Override
	 * 
	 * @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) public
	 * AccountSender changeAccountSenderName(AccountProvTrxInfo provTrxInfo, String
	 * newSenderName) throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender,
	 * DBException { String accountId = provTrxInfo.getAccountId();
	 * acctLogger.info(provTrxInfo.logId() + "Changing account sender name to ("
	 * + newSenderName + "), accountId: " + accountId);
	 * 
	 * acctLogger.info(provTrxInfo.logId() + "Validating the sender name");
	 * SenderType senderType = SMSUtils.getSenderType(newSenderName); if
	 * (!senderType.equals(SenderType.ALPHANUMERIC)) { throw new
	 * AccountManagInvalidSMSSender("SMS sender name should be alphanumeric"); }
	 * 
	 * // Check new sender availability acctLogger.info(provTrxInfo.logId() +
	 * "Checking new sender name availability"); AccountSender newAcctSender =
	 * accountSenderDao .findBySenderName(newSenderName); if (newAcctSender !=
	 * null) { if (newAcctSender.getAccountSendersPK().getAccountId()
	 * .equals(accountId)) { return newAcctSender; } else {
	 * SenderNameAlreadyAttached e = new SenderNameAlreadyAttached(
	 * newSenderName, newAcctSender.getAccountSendersPK() .getAccountId());
	 * acctLogger.error(provTrxInfo.logId() + e.getMessage()); throw e; } }
	 * 
	 * // Remove all old senders (if any) acctLogger.info(provTrxInfo.logId() +
	 * "All old sender(s) will be deleted (if any)");
	 * accountSenderDao.removeAllByAccountId(accountId);
	 * acctLogger.debug(provTrxInfo.logId() + "All old sender(s) are deleted");
	 * 
	 * newAcctSender = persistSenderName(provTrxInfo, accountId, newSenderName);
	 * return newAcctSender; }
	 */
	@Override
	public String getAccountSenderName(AccountProvTrxInfo provTrxInfo, String acctId) throws NoAttachedSendersException,
			DBException {
		List<AccountSender> senders = accountSenderDao.findByAccountId(acctId);
		acctLogger.info(provTrxInfo.logId() + "Retriveing all account senders");
		if (senders != null && !senders.isEmpty()) {
			// Because the account should only have one sender
			acctLogger.debug(provTrxInfo.logId() + "Found (" + senders.size() + ") sender(s), returning the first one");
			String senderName = senders.get(0).getAccountSendersPK().getSenderName();
			acctLogger.info(provTrxInfo.logId() + "Account sender name: " + senderName);
			return senderName;
		} else {
			NoAttachedSendersException e = new NoAttachedSendersException(acctId);
			acctLogger.warn(provTrxInfo.logId() + e.getMessage());
			throw e;
		}
	}

	// public QuotaInfo getQuotaInfo(AccountTrxInfo trxInfo, String accountId) throws
	// DBException, AccountNotFoundException,
	// InvalidCustomerForQuotaInquiry, QuotaInquiryFailed {
	// Account acct = findAccountById(trxInfo, accountId);
	// quotaInquiryDao.inquireQuota(acct.getBillingMsisdn());
	// }

	@Override
	public QuotaInfo getQuotaInfoByMSISDN(AccountUserTrxInfo trxInfo, String billingMSISDN) throws DBException,
			InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, AccountManagInvalidMSISDNFormatException,
			AccountManagInvalidAddressFormattingException, IneligibleAccountException {
		acctLogger.info(trxInfo.logInfo() + "Checking Inquiry for quota eligibility on billing MSISDN: (" + billingMSISDN + ")");
		List<ActionName> userActions = new ArrayList<>();
		userActions.add(ActionName.VIEW_QUOTA_INFO);
		trxInfo.setUserActions(userActions);                        
		checkAccountAndUserEligibility(trxInfo);
    
    	return getQuotaInfoByMSISDN0(trxInfo,billingMSISDN);
    
	}


	@Override
	public QuotaInfo getQuotaInfoByMSISDN(AccountTrxInfo trxInfo, String billingMSISDN) throws DBException,
			InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, AccountManagInvalidMSISDNFormatException,
			AccountManagInvalidAddressFormattingException {

		acctLogger.info(trxInfo.logInfo() + "Inquiry for quota on billing MSISDN: (" + billingMSISDN + ")");
		return getQuotaInfoByMSISDN0(trxInfo, billingMSISDN);
		
	}
	
	
	private QuotaInfo getQuotaInfoByMSISDN0(AccountTrxInfo trxInfo, String billingMSISDN) throws DBException,
			InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, AccountManagInvalidMSISDNFormatException,
			AccountManagInvalidAddressFormattingException {
		String formattedMSISDN;

		formattedMSISDN = SMSUtils.formatAddress(billingMSISDN, AccountManagMsisdnFormat.NATIONAL);

		acctLogger.debug(trxInfo.logId() + "Formatted billing MSISDN:" + formattedMSISDN);

		acctLogger.debug(trxInfo.logId() + "getting tier type for bigllin msisdn:" + formattedMSISDN);
		QuotaInfo qi = new QuotaInfo();
		TierType tierType = accountTiersDao.getTierTypeForBillingMSISDN(billingMSISDN);
		if (tierType != null) {
			acctLogger.debug(trxInfo.logId() + " tier type for bigllin msisdn:" + formattedMSISDN + " is : "
					+ tierType.toString());

		} else {
			tierType = new TierType();
			tierType.setTierTypeName(TierTypesEnum.POSTPAID);
		}
		switch (tierType.getTierTypeName()) {
		case POSTPAID:
			acctLogger.info(trxInfo.logInfo() + "inquire Quota for POST_PAID (NORMAL) tierType");
			qi = quotaInquiryDao.inquireQuota(formattedMSISDN);
			acctLogger.info(trxInfo.logInfo() + "Quota iquiry result: " + qi);
			break;
		case PREPAID:
			acctLogger.info(trxInfo.logInfo() + "inquire Quota for PRE_PAID (ONE_OFF) tierType by formattedMSISDN :"
					+ formattedMSISDN);
			AccountTier accountTiers = accountTiersDao.findByBillingMSISDN(billingMSISDN);
			acctLogger.info(trxInfo.logInfo() + "get accountTiers for billingMSISDN: " + billingMSISDN + " : "
					+ accountTiers);
			if (accountTiers != null) {
				qi.setConsumedSMS(accountTiers.getAccountQuota().getConsumedSmss());
				qi.setExpiryDate(accountTiers.getAccountQuota().getExpairyDate());
				qi.setReservedSMS(accountTiers.getAccountQuota().getReservedSmss());
				qi.setGrantedSMS(accountTiers.getAccountId().getTier().getQuota());
				acctLogger.info(trxInfo.logInfo() + "Quota iquiry result for pre-paid (one_off): " + qi);

				/*
				 * double consumed =
				 * accountTiers.getAccountQuota().getConsumedSmss();
				 * acctLogger.info(trxInfo.logInfo() +
				 * " consumed Quota for billing MSISDN is : " + billingMSISDN +
				 * "is : " + consumed); acctLogger.info(trxInfo.logInfo() +
				 * "granted Quota for billing : " + billingMSISDN + " is : " +
				 * accountTiers.getAccountId().getTier().getQuota());
				 * 
				 * if (consumed >=
				 * accountTiers.getAccountId().getTier().getQuota()) {
				 * acctLogger.info(trxInfo.logInfo() +
				 * "Account with billing MSISDN [" + billingMSISDN +
				 * "] is exceeding its quota and has getting to be suspended.");
				 * Account account =
				 * accountDao.findByBillingMSISDN(billingMSISDN);
				 * 
				 * acctLogger.info(trxInfo.logInfo() +
				 * " Account found for billing MSISDN "); acctLogger
				 * .info(trxInfo.logInfo() +
				 * " Sending Provisioning Request to suspend account with id: "
				 * + account.getAccountId());
				 * 
				 * ProvisioningRequest provRequest = new ProvisioningRequest();
				 * provRequest.setCompanyId(account.getAccountId()); //
				 * provRequest.setCompanyDomain(account.getCompanyName());
				 * provRequest.setCompanyName(account.getCompanyName()); //
				 * provRequest.setAccountAdmin(account.g);
				 * provRequest.setEntryDate(new Date());
				 * provRequest.setRequestId(trxInfo.getTrxId());
				 * provRequest.setCallbackUrl((String)
				 * Configs.CLOUD_CALL_BACK_SERVICE_URI.getValue());
				 * provRequest.setMSISDN(account.getBillingMsisdn());
				 * provRequest
				 * .setRequestType(ProvRequestTypeName.SUSPEND_ACCOUNT);
				 * acctLogger.info(trxInfo.logInfo() + "Prov Request is: " +
				 * provRequest);
				 * 
				 * AccountProvTrxInfo provTrxInfo = new
				 * AccountProvTrxInfo(TrxId.getTrxId(PROV_TRX_ID_PREFIX));
				 * provTrxInfo.setAccountId(account.getAccountId());
				 * provTrxInfo.setTrxId(trxInfo.getTrxId());
				 * provTrxInfo.setProvReqType
				 * (ProvRequestTypeName.SUSPEND_ACCOUNT);
				 * 
				 * acctLogger.info(trxInfo.logInfo() + "Prov Trx is: " +
				 * provTrxInfo);
				 * 
				 * serviceProvisioning.HandleInternalSuspendProvRequest(provTrxInfo
				 * , provRequest);
				 * 
				 * acctLogger.info(trxInfo.logInfo() + "Account Suspended.");
				 * 
				 * }
				 */

			} else
				throw new QuotaInquiryFailed();
			break;
		}

		return qi;

	}

	@Override
	public QuotaHistoryModel getQuotaHistory(AccountUserTrxInfo trxInfo, String accountId) throws DBException,
			AccountNotFoundException, IneligibleAccountException {
		acctLogger.info(trxInfo.logInfo() + "Getting quota history for account(" + accountId + ")");
                             
		int existanceCheck = accountDao.count(accountId);
		if (existanceCheck != 0) {
                    
                        List<ActionName> userActions = new ArrayList<>();
                        userActions.add(ActionName.VIEW_REPORTS);
                        trxInfo.setUserActions(userActions);                        
                        checkAccountAndUserEligibility(trxInfo);
                    
			QuotaHistory quotaHistory = quotaHistoryDao.findQuotaHistory(accountId);
			if (quotaHistory != null) {
				acctLogger.debug(trxInfo.logId() + quotaHistory);
				QuotaHistoryModel result = new QuotaHistoryModel(quotaHistory.getAccountId());
				result.setJan(quotaHistory.getJan());
				result.setFeb(quotaHistory.getFeb());
				result.setMar(quotaHistory.getMar());
				result.setApr(quotaHistory.getApr());
				result.setMay(quotaHistory.getMay());
				result.setJune(quotaHistory.getJune());
				result.setJuly(quotaHistory.getJuly());
				result.setAug(quotaHistory.getAug());
				result.setSept(quotaHistory.getSept());
				result.setOct(quotaHistory.getOct());
				result.setNov(quotaHistory.getNov());
				result.setDec(quotaHistory.getDec());
				result.setUpdateTimestamp(quotaHistory.getUpdateTimestamp());
				return result;
			}

			acctLogger.debug(trxInfo.logId() + "Account(" + accountId + ") has no quota history yet.");
			return null;
		} else
			throw new AccountNotFoundException(accountId);
	}

	@Override
	public List<Account> findAllAccounts(AccountAdminTrxInfo trxInfo, int first, int count) throws DBException {
		acctLogger.info(trxInfo.logInfo() + "Find accounts by range, first: " + first + ", count :" + count
				+ ", order by: companyName");
		return accountDao.findRange(first, count, "companyName");
	}

	@Override
	public int countAccounts(AccountTrxInfo trxInfo) throws DBException {
		acctLogger.info(trxInfo.logInfo() + "Get total accounts count");
		int count = accountDao.count();
		acctLogger.info(trxInfo.logId() + "Accounts count= " + count);
		return count;
	}

	@Override
	public void validateSMSSender(AccountTrxInfo trxInfo, String senderName) throws AccountManagInvalidSMSSender {
		SenderType senderType = SMSUtils.getSenderType(senderName);
		if (!senderType.equals(SenderType.ALPHANUMERIC)) {
			throw new AccountManagInvalidSMSSender("SMS sender name should be alphanumeric");
		}
	}

	private AccountSender persistSenderName(AccountProvTrxInfo provTrxInfo, String newSenderName) throws DBException {
		acctLogger.debug(provTrxInfo.logId() + "persisting the new sender");
		AccountSender senderName = new AccountSender();
		AccountSenderPK accountSenderPK = new AccountSenderPK();
		accountSenderPK.setSenderName(newSenderName);
		accountSenderPK.setAccountId(provTrxInfo.getAccountId());
		senderName.setAccountSendersPK(accountSenderPK);
		accountSenderDao.create(senderName);
		acctLogger.info(provTrxInfo.logId() + "Account sender name (" + newSenderName + ") persisted.");
		return senderName;
	}

	@Override
	public long countSearchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN)
			throws DBException {
		List<AccountStatus> statuses = new ArrayList<>();
		statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
		try {
			acctLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Counting for the searched account(s) with accountId=" + accountID + " or campanyName="
					+ companyName + " billingMsisdn=" + billingMSISDN);
			return accountDao.countSearchAccount(accountID, companyName, billingMSISDN, statuses);
		}// end try
		catch (Exception e) {
			acctLogger.error("Error while returning the count of searching for account(s) ", e);
			return 0;
		}// end catch
	}// end of method countSearchAccount

	@Override
	public List<Account> searchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName,
			String billingMSISDN, int first, int max) throws DBException {
		List<AccountStatus> statuses = new ArrayList<>();
		statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
		try {
			acctLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Customer care representative searching for account with accountId=" + accountID
					+ " or campanyName=" + companyName + " billingMsisdn=" + billingMSISDN + " paginated from=" + first
					+ " with max=" + max);
			return accountDao.searchAccount(accountID, companyName, billingMSISDN, statuses, first, max);
		}// end try
		catch (Exception e) {
			acctLogger.error("Error while searching for account ", e);
			return null;
		}// end catch
	}// end of method searchAccount

	public String logTrxId(String trxId) {
		StringBuilder sb = new StringBuilder();
		sb.append("Trx");
		sb.append("(");
		sb.append(trxId);
		sb.append("): ");
		return sb.toString();
	}

	public String userLogInfo(String id, String userName) {
		StringBuilder sb = new StringBuilder();
		sb.append("User");
		sb.append("(");
		sb.append(id);
		sb.append(",");
		sb.append(userName);
		sb.append("): ");
		return sb.toString();
	}

	@Override
	public List<AccountSender> getAccountSenderList(AccountTrxInfo trxInfo, String accountId)
			throws NoAttachedSendersException, DBException {
		List<AccountSender> senders = accountSenderDao.findByAccountId(accountId);
		acctLogger.info(trxInfo.logId() + "Retriveing all account senders");
		if (senders != null && !senders.isEmpty()) {
			acctLogger.debug(trxInfo.logId() + "Found (" + senders.size() + ") sender(s), returning the list");
			return senders;
		} else {
			NoAttachedSendersException e = new NoAttachedSendersException(accountId);
			acctLogger.error(trxInfo.logId() + e.getMessage());
			throw e;
		}
	}

	@Override
	public TierTypesEnum getTierTypeNameByTierId(AccountTrxInfo trxInfo, Integer TierId) {
		try {
			Tier tier = tierDao.find(TierId);
			if (tier != null)
				return tier.getTierType().getTierTypeName();
		} catch (DBException e) {
			acctLogger.error(trxInfo.logId() + e.getMessage());
		}
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removePrePaidAccount(AccountTrxInfo trxInfo, Account account) throws DBException {
		acctLogger.info("checking account tier type with id : [" + account.getAccountId() + "].");
		if (account.getTier().getTierType().getTierTypeName() == TierTypesEnum.PREPAID) {
			accountDao.remove(account);
			acctLogger.debug("removing one off ACCOUNT succefully.");
		}

	}

	@Override
	public List<Account> findAllAccounts(AccountTrxInfo trxInfo) throws DBException {
		acctLogger.info("find all accounts");
		List<Account> accoutns = accountDao.findAll();
		if (accoutns != null)
			acctLogger.debug("found : (" + accoutns.size() + " ) accounts.");
		else
			acctLogger.debug("No accounts are found.");

		return accoutns;
	}

	@Override
	public List<Account> searchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN)
			throws DBException {
		List<AccountStatus> statuses = new ArrayList<>();
		statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
		try {
			acctLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Customer care representative searching for account with accountId=" + accountID
					+ " or campanyName=" + companyName + " billingMsisdn=" + billingMSISDN);
			return accountDao.searchAccount(accountID, companyName, billingMSISDN, statuses);
		}// end try
		catch (Exception e) {
			acctLogger.error("Error while searching for account ", e);
			return null;
		}// end catch
	}// end of method searchAccount
        
    @Override
    public List<PrivilegeModel> getAllSystemPrivileges(AccountTrxInfo trxInfo) throws DBException {
        acctLogger.info(trxInfo.logInfo() + "Inquiry for All System not hidden Privileges");
        List<Privilege> systemPrivileges = privilegeDao.findByHiddenFlag(false);

        List<PrivilegeModel> returnedSystemPrivileges = new ArrayList<>();
        for (Privilege privilege : systemPrivileges) {
            returnedSystemPrivileges.add(new PrivilegeModel(privilege.getPrivilegeName(), privilege.getPrivilegeId().toPlainString()));
        }
        acctLogger.debug(trxInfo.logInfo() + "SystemPrivileges: " + returnedSystemPrivileges);
        return returnedSystemPrivileges;
    }
}
