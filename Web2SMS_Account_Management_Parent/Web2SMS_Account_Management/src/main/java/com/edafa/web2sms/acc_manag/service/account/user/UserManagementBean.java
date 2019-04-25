package com.edafa.web2sms.acc_manag.service.account.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidMSISDNException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidRequestException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotEditableException;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountConversionBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountManegementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.AdminAlreadyGrantedException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.IneligibleUserException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserManagementServiceBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.local.UserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.model.UserResult;
import com.edafa.web2sms.acc_manag.service.conversoin.UserConversionBean;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;
import com.edafa.web2sms.acc_manag.utils.configs.enums.Configs;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.acc_manag.utils.sms.AccountManagMsisdnFormat;
import com.edafa.web2sms.acc_manag.utils.sms.SMSUtils;
import com.edafa.web2sms.dalayer.dao.ActionDaoLocal;
import com.edafa.web2sms.dalayer.dao.UserLoginStatusDao;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountGroupDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.IntraSenderDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.UserLoginStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.enums.UserLoginStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.AccountUserLogin;
import com.edafa.web2sms.dalayer.model.IntraSender;
import com.edafa.web2sms.dalayer.model.constants.AccountGroupConst;
import java.util.Calendar;
import java.util.logging.Level;

/**
 * Session Bean implementation class UserManagementBean
 */
@Stateless
public class UserManagementBean implements UserManagementBeanLocal, UserManagementServiceBeanLocal {

    Logger appLogger;
    Logger acctLogger;

    @EJB
    AccountUserDaoLocal accountUserDao;

    @EJB
    AccountStatusDaoLocal accountStatusDao;

    @EJB
    AccountDaoLocal accountDao;

    @EJB
    AccountGroupDaoLocal accountGroupDao;

    @EJB
    ActionDaoLocal actionDao;

    @EJB
    AccountManegementBeanLocal accountManegementBean;

    @EJB
    AccountManegementBeanLocal accountManagement;

    @EJB
    UserConversionBean userConversionBean;

    @EJB
    private AccountGroupDaoLocal acctGroupDao;

    @EJB
    private UserLoginStatusDaoLocal userLoginStatusDao;

    @EJB
    AccountConversionBeanLocal acctConversionBean;

    @EJB
    IntraSenderDaoLocal intraSenderDao;

    /**
     * Default constructor.
     */
    public UserManagementBean() {
        // zero argument constructor
    }

    @PostConstruct
    public void initLoggers() {
        appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
        acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
    }

    @Override
    public List<AccountUser> getAccountUsers(AccountProvTrxInfo trxInfo, List<AccountStatus> statuses) throws UserNotFoundException {

        acctLogger.info(trxInfo.logId() + "Retriving users of account(" + trxInfo.getAccountId() + ") with status: " + statuses);

        List<AccountUser> acctUser = accountUserDao.findAccountUsers(trxInfo.getAccountId(), statuses);
        if (acctUser == null) {
            acctLogger.error(trxInfo.logId() + "Account(" + trxInfo.getAccountId() + ") User(s) not found");
            throw new UserNotFoundException(trxInfo.getAccountId());
        }
        acctLogger.info(trxInfo.logId() + "Found(" + acctUser.size() + ") User(s)");

        return acctUser;
    }

    @Override
    public AccountUser getAccountUser(AccountProvTrxInfo trxInfo, String username) throws UserNotFoundException {

        acctLogger.info(trxInfo.logId() + "Retriving account(" + trxInfo.getAccountId() + ") user (" + username + ").");
        List<AccountStatus> statuses = new ArrayList<>();
        statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
        AccountUser acctUser = accountUserDao.findAccountUser(trxInfo.getAccountId(), username, statuses);
        if (acctUser == null) {
            acctLogger.error(trxInfo.logId() + "Account(" + trxInfo.getAccountId() + ") User(s) not found");
            throw new UserNotFoundException(trxInfo.getAccountId(), username);
        }
        acctLogger.info(trxInfo.logId() + acctUser + " Found.");

        return acctUser;
    }

    @Override
    public AccountUser getAccountUser(AccountUserTrxInfo trxInfo) throws UserNotFoundException {

        acctLogger.info(trxInfo.logId() + "Retriving account(" + trxInfo.getUser().getAccountId() + ") user ("
                + trxInfo.getUser().getUsername() + ").");
        List<AccountStatus> statuses = new ArrayList<>();
        statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
        AccountUser acctUser = accountUserDao.findAccountUser(trxInfo.getUser().getAccountId(), trxInfo.getUser()
                .getUsername(), statuses);
        if (acctUser == null) {
            acctLogger.error(trxInfo.logId() + "Account(" + trxInfo.getUser().getAccountId() + ") User(s) not found");
            throw new UserNotFoundException(trxInfo.getUser().getAccountId(), trxInfo.getUser().getUsername());
        }
        acctLogger.info(trxInfo.logId() + acctUser + " Found.");

        return acctUser;
    }

    @Override
    public AccountUser getAccountAdminUser(AccountProvTrxInfo trxInfo) {

        acctLogger.info(trxInfo.logInfo() + "Retriving account(" + trxInfo.getAccountId() + ") Admin user");
        List<AccountStatus> statuses = new ArrayList<>();
        statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
        AccountUser acctUser = accountUserDao.findAccountAdminUser(trxInfo.getAccountId(), statuses);
        if (acctUser == null) {
            acctLogger.error(trxInfo.logId() + "Account(" + trxInfo.getAccountId() + ") admin User not found");

        } else {
            acctLogger.info(trxInfo.logId() + acctUser + " Found.");
        }
        return acctUser;
    }

    @Override
    public AccountUser addAccountUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException {

        return addAccountUser(trxInfo, username, AccountStatusName.ACTIVE, userGroups, false);

    }

    @Override
    public AccountUser activateAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException {
        return addAccountUser(trxInfo, username, AccountStatusName.ACTIVE, userGroups, true);
    }

    // Buggy method it's never called and need refinement before using it.
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser addAccountAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException,
            AdminAlreadyGrantedException {

        acctLogger.info(trxInfo.logInfo() + "Add admin user to account(" + trxInfo.getAccountId() + "), username="
                + username);
        AccountUser admin = getAccountAdminUser(trxInfo);
        if (admin != null) {
            if (admin.getUsername().equals(username)) {
                acctLogger.error(trxInfo.logId() + "Alreading user with same username is the account admin.");
                throw new AdminAlreadyGrantedException(trxInfo.getAccountId(), username);
            }
            // null pointer exception is here 
            admin.setAdminRoleFlag(false);
            // delete old admin
            accountUserDao.edit(admin);

        }

        return addAccountUser(trxInfo, username, AccountStatusName.ACTIVE, userGroups, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private AccountUser addAccountUser(AccountProvTrxInfo trxInfo, String username, AccountStatusName acctStatusName,
            List<AccountGroup> userGroups, boolean adminFlag) throws DBException {

        acctLogger.info(trxInfo.logInfo() + "Add user to account(" + trxInfo.getAccountId()
                + ") with parameter[username=" + username + ", acctStatusName=" + acctStatusName + " and adminFlag= "
                + adminFlag + "].");
        acctLogger.debug(trxInfo.logId() + "Check username availability ");
        List<AccountStatus> statuses = new ArrayList<>();
        statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
        AccountUser acctUser = accountUserDao.findAccountUser(trxInfo.getAccountId(), username, statuses);
        if (acctUser == null) {
            acctLogger.debug(trxInfo.logId() + "username available. ");
            acctUser = new AccountUser(accountDao.findByAccountId(trxInfo.getAccountId()), username);
            acctUser.setAdminRoleFlag(adminFlag);
            Account acct = accountDao.findByAccountId(trxInfo.getAccountId());
            acctUser.setAccount(acct);
            acctUser.setAccountId(trxInfo.getAccountId());
            acctUser.setStatus(accountStatusDao.getCachedObjectByName(acctStatusName));

//            AccountUserLogin accountUserLogin = new AccountUserLogin();
//            accountUserLogin.setAccountUserId(acctUser);
//            accountUserLogin.setUserLoginStatus(userLoginStatusDao.getCachedObjectByName(UserLoginStatusName.INITIAL));
//            acctUser.setAccountUserLogin(accountUserLogin);

            if (userGroups != null) {
                acctUser.setAccountGroups(userGroups);
            } else {
                acctLogger.debug(trxInfo.logId() + "Get account Default group");
                AccountGroup defaultAccountGroup = accountGroupDao.findAccountDefaultGroup(trxInfo.getAccountId());
                if (defaultAccountGroup != null) {
                    acctLogger.debug(trxInfo.logId() + "Set " + defaultAccountGroup.getGroupName() + " for user " + username);
                    List<AccountGroup> accountGroups = new ArrayList<>();
                    accountGroups.add(defaultAccountGroup);
                    acctUser.setAccountGroups(accountGroups);
                }
            }
            accountUserDao.create(acctUser);
            acctLogger.info(trxInfo.logId() + acctUser + " persisted to DB.");
            return acctUser;
        }
        // In case addAdminUser() used with exist user to grant admin role
        acctLogger.debug(trxInfo.logId() + "username already exists ");
        if (acctUser.getAdminRoleFlag() != adminFlag) {
            if (acctUser.getAdminRoleFlag()) {
                acctLogger.debug(trxInfo.logId() + "user admin Role will be removed.");
            }
            acctLogger.debug(trxInfo.logId() + "user admin Role will be granted.");
            acctUser.setAdminRoleFlag(adminFlag);
            accountUserDao.edit(acctUser);
            acctLogger.debug(trxInfo.logId() + "user successfully updated.");
        }
        return acctUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deactivateAccountUser(AccountProvTrxInfo trxInfo, String username) throws DBException,
            AdminAlreadyGrantedException, UserNotFoundException {

        acctLogger.info(trxInfo.logInfo() + "delete user from account(" + trxInfo.getAccountId() + "), username="
                + username);
        AccountUser admin = getAccountAdminUser(trxInfo);
        if (admin.getUsername().equals(username)) {
            acctLogger.error(trxInfo.logId() + "This user is the account admin.");
            throw new AdminAlreadyGrantedException(trxInfo.getAccountId(), username);
        }
        List<AccountStatus> statuses = new ArrayList<>();
        statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
        AccountUser acctUser = accountUserDao.findAccountUser(trxInfo.getAccountId(), username.toLowerCase(), statuses);

        if (acctUser == null) {
            throw new UserNotFoundException(trxInfo.getAccountId(), username);
        }
        AccountStatus acctStatus = accountStatusDao.getCachedObjectByName(AccountStatusName.INACTIVE);

        acctUser.setStatus(acctStatus);
        if (acctUser.getAccountGroups() != null) {
            acctUser.getAccountGroups().clear();
        }

        accountUserDao.edit(acctUser);

    }

    @Override
    public List<AccountUser> getAccountUsers(AccountAdminTrxInfo trxInfo, int first, int max) throws UserNotFoundException {
        try {
            acctLogger.info(trxInfo.logId() + "Retriving account user(s)");
            List<AccountUser> acctUser = accountUserDao.findRange(first, max, AccountUser.ACCOUNTUSER_ID);
            if (acctUser == null) {
                acctLogger.error(trxInfo.logId() + "Account User(s) list not found");
                return null;
            }
            acctLogger.info(trxInfo.logId() + "Found(" + acctUser.size() + ") User(s)");

            return acctUser;
        } catch (Exception e) {
            acctLogger.info(trxInfo.logId() + "Error while getting user list", e);
            return null;
        }
    }

    @Override
    public List<AccountUser> searchAccountUsers(AccountAdminTrxInfo trxInfo, String userName, String accountID,
            String companyName, String billingMSISDN, String userMSISDN, int first, int max) throws UserNotFoundException {
        try {
            acctLogger.info(trxInfo.logId() + "Searching account user(s)");

            List<AccountStatus> statuses = new ArrayList<>();
            statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));

            List<AccountUser> acctUser = accountUserDao.searchAccountUser(userName, accountID, companyName, billingMSISDN, userMSISDN, statuses, first, max);
            if (acctUser == null) {
                acctLogger.error(trxInfo.logId() + "Account User(s) list not found");
                return null;
            }// end if

            acctLogger.info(trxInfo.logId() + "Found(" + acctUser.size() + ") User(s)");

            return acctUser;
        }// end try 
        catch (Exception e) {
            acctLogger.info(trxInfo.logId() + "Error while getting user list", e);
            return null;
        }// end catch
    }// end of method searchAccountUsers

    @Override
    public long countSearchAccountUsers(AccountAdminTrxInfo trxInfo, String userName, String accountID, String companyName,
            String billingMSISDN, String userMSISDN) throws UserNotFoundException {
        try {
            acctLogger.info(trxInfo.logId() + "counting search account user(s)");

            List<AccountStatus> statuses = new ArrayList<>();
            statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));

            long acctUsersCount = accountUserDao.countSearchAccountUser(userName, accountID, companyName, billingMSISDN, userMSISDN, statuses);

            acctLogger.info(trxInfo.logId() + "Found(" + acctUsersCount + ") User(s)");

            return acctUsersCount;
        }// end try 
        catch (Exception e) {
            acctLogger.info(trxInfo.logId() + "Error while getting user list", e);
            return 0;
        }// end catch
    }// end of method countSearchAccountUsers

    @Override
    public UserResult findUserByCoName(AccountUserTrxInfo userTrxInfo, String companyName) throws AccountNotFoundException, DBException, UserNotFoundException {

        acctLogger.info(userTrxInfo.logInfo() + "Retriving user");
        Account acct = accountDao.findByCompanyName(companyName.toLowerCase());
        if (acct == null) {
            throw new AccountNotFoundException();
        }
        List<AccountUser> accountUsers = acct.getAccountUsers();
        if (accountUsers == null) {
            acctLogger.error(userTrxInfo.logInfo() + "User(" + userTrxInfo.getUser().getUsername() + ") not found");
            throw new UserNotFoundException(userTrxInfo.logInfo(), userTrxInfo.getUser().getUsername());
        }

        AccountUser acctUser = null;
        for (AccountUser user : accountUsers) {
            if ((user.getUsername().equals(userTrxInfo.getUser().getUsername())) && (user.getStatus().getAccountStatusName().equals(AccountStatusName.ACTIVE))) {
                acctUser = user;
            }
        }

        if (acctUser == null) {
            acctLogger.error(userTrxInfo.logInfo() + "User(" + userTrxInfo.getUser().getUsername() + ") not found or not actve user");
            throw new UserNotFoundException(userTrxInfo.logInfo(), userTrxInfo.getUser().getUsername());
        }
        acctLogger.debug(userTrxInfo.logInfo() + "Get User Allowed Actions");
        List<ActionName> allowedActionNames = actionDao.getUserAllowedActions(acct.getAccountId(), userTrxInfo.getUser().getUsername());

        if (allowedActionNames != null && !allowedActionNames.isEmpty()) {
            List<ActionName> addedActions = new ArrayList<>();
            Iterator<ActionName> allowedAction = allowedActionNames.iterator();
            while (allowedAction.hasNext()) {
                ActionName actionName = allowedAction.next();
                if (actionName.equals(ActionName.VIEW_OWN_GROUP)) {
                    allowedAction.remove();
                    addedActions.add(ActionName.VIEW_GROUPS);
                } else if (actionName.equals(ActionName.VIEW_OWN_GROUP_USERS)) {
                    allowedAction.remove();
                    addedActions.add(ActionName.VIEW_USERS);
                }
            }
            for (ActionName actionName : addedActions) {
                if (!allowedActionNames.contains(actionName)) {
                    allowedActionNames.add(actionName);
                }
            }
        }
        UserResult userResult = new UserResult(ResponseStatus.SUCCESS);
        AccManagUserModel userModel = new AccManagUserModel();
        userModel.setUsername(userTrxInfo.getUser().getUsername());
        userModel.setAccountId(acct.getAccountId());
        userModel.setUserActions(allowedActionNames);

        acctLogger.info(userTrxInfo.logInfo() + userModel + " Found.");

        userResult.setUser(userModel);
        userResult.setNumberOfAccountUsers(accountUsers.size());

        return userResult;
    }

    @Override
    public int countAccountUsers(AccountUserTrxInfo userTrxInfo, String userName) throws DBException, IneligibleAccountException {

        List<ActionName> actionList = new ArrayList<>();
        actionList.add(ActionName.VIEW_USERS);
        actionList.add(ActionName.VIEW_OWN_GROUP_USERS);

        userTrxInfo.setUserActions(actionList);

        //check eligibility
        List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);
        //check if should return all groups for "Admin" or single group for "Group admin" 

        String accountId = userTrxInfo.getUser().getAccountId();
        int count = 0;

        if (eligibileActions.contains(ActionName.VIEW_USERS)) {
            acctLogger.debug(userTrxInfo.logInfo() + "Count account users with search user name(" + (userName == null ? "NULL" : userName) + ")");
            count = accountUserDao.countActiveAccountUsers(accountId, userName, null);
        } else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP_USERS)) {
            List<AccountStatus> statuses = new ArrayList<>();
            statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
            AccountUser accountUser = accountUserDao.findAccountUser(accountId, userTrxInfo.getUser().getUsername(),
                    statuses);
            String userGroupId = accountUser.getVisibleAccountGroups().get(0).getAccountGroupId();
            acctLogger.debug(userTrxInfo.logInfo() + "Count group with id(" + userGroupId + ") users with search user name(" + (userName == null ? "NULL" : userName) + ")");
            count = accountUserDao.countActiveAccountUsers(accountId, userName, userGroupId);
        }

        acctLogger.info(userTrxInfo.logInfo() + "Count of users(" + count + ")");

        return count;
    }

    @Override
    public List<UserDetailsModel> getAccountUsers(AccountUserTrxInfo userTrxInfo, String search, int first, int max)
            throws DBException, IneligibleAccountException {

        List<ActionName> actionList = new ArrayList<>();
        actionList.add(ActionName.VIEW_USERS);
        actionList.add(ActionName.VIEW_OWN_GROUP_USERS);

        userTrxInfo.setUserActions(actionList);

        //check eligibility
        List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);
        //check if should return all groups for "Admin" or single group for "Group admin" 

        String accountId = userTrxInfo.getUser().getAccountId();
        List<AccountUser> accountUsers = null;

        List<AccountStatus> acctStatus = new ArrayList<AccountStatus>();
        acctStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));

        if (eligibileActions.contains(ActionName.VIEW_USERS)) {
            acctLogger.debug(userTrxInfo.logInfo() + "Get accout users with search user name(" + (search == null ? "NULL" : search) + ") and first(" + first + ") and max(" + max + ")");
            accountUsers = accountUserDao.searchActiveAccountUsers(accountId, search, null, first, max);

        } else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP_USERS)) {
            AccountUser accountUser = accountUserDao.findAccountUser(accountId, userTrxInfo.getUser().getUsername(),
                    acctStatus);
            String userGroupId = accountUser.getVisibleAccountGroups().get(0).getAccountGroupId();
            acctLogger.info(userTrxInfo.logInfo() + "Get group with id(" + userGroupId + " users with search user name(" + (search == null ? "NULL" : search) + ") and first(" + first + ") and max(" + max + ")");
            accountUsers = accountUserDao.searchActiveAccountUsers(accountId, search, userGroupId, first, max);
        } else {
            throw new IneligibleAccountException("Ineligible");
        }
        acctLogger.trace(userTrxInfo.logInfo() + "Convert AccountUser to UserDetailsModel");
        List<UserDetailsModel> users = userConversionBean.getUsersModel(accountUsers);

        acctLogger.info(userTrxInfo.logInfo() + "(" + users.size() + ") Users retrived successfully");
        acctLogger.debug(userTrxInfo.logInfo() + "(" + users + ") Users retrived successfully");

        return users;
    }

    @Override
    public int countDefaultGroupUsers(AccountUserTrxInfo userTrxInfo, String userName) throws DBException, IneligibleAccountException {

        List<ActionName> actionList = new ArrayList<>();
        actionList.add(ActionName.VIEW_DEFAULT_GROUP_USERS);
        userTrxInfo.setUserActions(actionList);
        accountManagement.checkAccountAndUserEligibility(userTrxInfo);

        String accountId = userTrxInfo.getUser().getAccountId();
        acctLogger.debug(userTrxInfo.logInfo() + "Get account default group id");
        AccountGroup accountGroup = acctGroupDao.findAccountDefaultGroup(accountId);

        acctLogger.debug(userTrxInfo.logInfo() + "Count group with id(" + accountGroup.getAccountGroupId() + ") users with search user name(" + (userName == null ? "NULL" : userName) + ")");
        int count = accountUserDao.countActiveAccountUsers(accountId, userName, accountGroup.getAccountGroupId());

        acctLogger.info(userTrxInfo.logInfo() + "Count of users(" + count + ")");
        return count;
    }

    @Override
    public List<UserDetailsModel> getDefaultGroupUsers(AccountUserTrxInfo userTrxInfo, String search, int first,
            int max) throws DBException, IneligibleAccountException {

        List<ActionName> actionList = new ArrayList<>();
        actionList.add(ActionName.VIEW_DEFAULT_GROUP_USERS);

        userTrxInfo.setUserActions(actionList);

        //check eligibility
        accountManagement.checkAccountAndUserEligibility(userTrxInfo);

        String accountId = userTrxInfo.getUser().getAccountId();
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving default group");
        AccountGroup accountGroup = acctGroupDao.findAccountDefaultGroup(accountId);

        acctLogger.info(userTrxInfo.logInfo() + "Get group with id(" + accountGroup.getAccountGroupId() + " users with search user name(" + (search == null ? "NULL" : search) + ") and first(" + first + ") and max(" + max + ")");
        List<AccountUser> accountUsers = accountUserDao.searchActiveAccountUsers(accountId, search, accountGroup.getAccountGroupId(), first, max);

        acctLogger.trace(userTrxInfo.logInfo() + "Convert AccountUser to UserDetailsModel");
        List<UserDetailsModel> users = userConversionBean.getUsersModel(accountUsers);

        acctLogger.debug(userTrxInfo.logInfo() + "(" + users.size() + ") Users retrived successfully");
        acctLogger.debug(userTrxInfo.logInfo() + "(" + users + ") retrived successfully");

        return users;
    }

    private List<AccountUser> searchUsers(String search, int first, int max, List<AccountUser> accountUsers) {

        //TODO check boundaries 
        if (max > 0 && accountUsers.size() > 0) {
            int toIndex = max + first;

            if (toIndex < accountUsers.size()) {
                accountUsers = accountUsers.subList(first, toIndex);
            } else {
                accountUsers = accountUsers.subList(first, accountUsers.size());
            }

        }

        accountUsers = containsIgnoreCase(accountUsers, search);
        return accountUsers;
    }

    @Override
    public void updateUserData(AccountUserTrxInfo userTrxInfo, UserDetailsModel userModel)
            throws DBException, IneligibleAccountException, NotEditableException, IneligibleUserException, InvalidRequestException, InvalidMSISDNException {

        validateUpdateUserDataRequest(userTrxInfo.logInfo(), userModel);

        //check eligibility
        List<ActionName> actionList = new ArrayList<>();
        actionList.add(ActionName.VIEW_USERS);
        actionList.add(ActionName.VIEW_OWN_GROUP_USERS);
        actionList.add(ActionName.EDIT_USER_INFO);
        actionList.add(ActionName.EDIT_GROUP_USERS);

        actionList.add(ActionName.VIEW_GROUPS);
        actionList.add(ActionName.EDIT_GROUP);

        userTrxInfo.setUserActions(actionList);
        List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);

        String userId = userModel.getUserId();

        // get the ediatable user
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving user with id(" + userId + ")");
        AccountUser editableUser = accountUserDao.findAccountUserById(userId);
        if (editableUser == null) {
            throw new IneligibleUserException("Account User with ID[" + userId + "] not found");
        }
        // prevent non admin user from edit users
        boolean groupAdmin = false;
        if (!eligibileActions.contains(ActionName.VIEW_OWN_GROUP_USERS) && !eligibileActions.contains(ActionName.VIEW_USERS)) {
            throw new IneligibleUserException("Can't edit users without being admin of their group ");
        } else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP_USERS) && !eligibileActions.contains(ActionName.VIEW_USERS)) {
            // make sure this is the group admin of editable user group.
            acctLogger.debug(userTrxInfo.logInfo() + "Retriving user(" + userTrxInfo.getUser().getUsername() + ")");
            List<AccountStatus> statusList = new ArrayList<>();
            statusList.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
            AccountUser adminAccountUser = accountUserDao.findAccountUser(userTrxInfo.getUser().getAccountId(), userTrxInfo.getUser().getUsername(), statusList);
            if (!adminAccountUser.getVisibleAccountGroups().get(0).getAccountGroupId().equals(editableUser.getVisibleAccountGroups().get(0).getAccountGroupId())) {
                throw new IneligibleUserException("Can't edit users without being admin of their group ");
            }
            groupAdmin = true;
        }

        // user doesn't belong to any group so set the default group (Ui make sure the user belong to some group)
//                if (userModel.getGroupId() == null || userModel.getGroupId().isEmpty()){
//                    acctLogger.debug(userTrxInfo.logInfo() + "No group in the received user group model, set the default group" );
//                    AccountGroup defaultGroup = accountGroupDao.findAccountDefaultGroup(userTrxInfo.getUser().getAccountId());
//                    userModel.setGroupId(defaultGroup.getAccountGroupId());
//                }
        acctLogger.debug(userTrxInfo.logInfo() + "Updating AccountUser[" + editableUser);
        AccountGroup userGroup = editableUser.getVisibleAccountGroups().get(0);

        if (eligibileActions.contains(ActionName.EDIT_USER_INFO) && eligibileActions.contains(ActionName.EDIT_GROUP_USERS)) {
            if (groupAdmin) {
                if (!userModel.getGroupId().equals(userGroup.getAccountGroupId())) {
                    if (eligibileActions.contains(ActionName.VIEW_GROUPS) && eligibileActions.contains(ActionName.EDIT_GROUP)) {
                        editableUser = userConversionBean.updateAccountUser(userModel, editableUser);
                    } else {
                        throw new IneligibleUserException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                                + " is ineligible to  [" + ActionName.EDIT_GROUP_USERS + "] on user(" + userModel.getUsername() + ")");
                    }
                } else {
                    editableUser = userConversionBean.updateAccountUserBasicInfo(userModel, editableUser);
                }
            } else {
                if (userGroup.getGroupName().equals(AccountGroupConst.ADMINS_GROUP_NAME) && userGroup.getAccountUsers().size() < 2) {
                    if (!userModel.getGroupId().equals(userGroup.getAccountGroupId())) {
                        throw new NotEditableException("Can't remove last user at the " + AccountGroupConst.ADMINS_GROUP_NAME + " group");
                    }
                }
                editableUser = userConversionBean.updateAccountUser(userModel, editableUser);
            }

        } else if (eligibileActions.contains(ActionName.EDIT_USER_INFO)) {
            if (!userModel.getGroupId().equals(userGroup.getAccountGroupId())) {
                throw new IneligibleUserException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                        + " is ineligible to  [" + ActionName.EDIT_GROUP_USERS + "] on user(" + userModel.getUsername() + ")");
            }
            editableUser = userConversionBean.updateAccountUserBasicInfo(userModel, editableUser);

        } else if (eligibileActions.contains(ActionName.EDIT_GROUP_USERS)) {
            if ((userModel.getEmail() != null && !userModel.getEmail().equals(editableUser.getEmail()))
                    || (userModel.getName() != null && !userModel.getName().equals(editableUser.getName()))
                    || (userModel.getPhoneNumber() != null && !userModel.getPhoneNumber().equals(editableUser.getPhoneNumber()))) {
                throw new IneligibleUserException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                        + " is ineligible to  [" + ActionName.EDIT_USER_INFO + "] on user(" + userModel.getUsername() + ")");
            }
            if (userGroup.getGroupName().equals(AccountGroupConst.ADMINS_GROUP_NAME) && userGroup.getAccountUsers().size() < 2) {
                if (!userModel.getGroupId().equals(userGroup.getAccountGroupId())) {
                    throw new NotEditableException("Can't remove last user at the " + AccountGroupConst.ADMINS_GROUP_NAME + " group");
                }
            }

            if (groupAdmin) {
                if (!userModel.getGroupId().equals(userGroup.getAccountGroupId())) {
                    if (eligibileActions.contains(ActionName.VIEW_GROUPS) && eligibileActions.contains(ActionName.EDIT_GROUP)) {
                        editableUser = userConversionBean.updateAccountUsergroups(userModel, editableUser);
                    } else {
                        throw new IneligibleUserException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                                + " is ineligible to  [" + ActionName.EDIT_GROUP_USERS + "] on user(" + userModel.getUsername() + ")");
                    }
                }
            } else {
                editableUser = userConversionBean.updateAccountUsergroups(userModel, editableUser);
            }
        }
        //Persistence 
        accountUserDao.edit(editableUser);
        acctLogger.info(userTrxInfo.logInfo() + "AccountUser[" + editableUser + " updated successfully");
    }

    @Override
    public void updateUserData(AccountAdminTrxInfo userTrxInfo, UserDetailsModel userModel) throws IneligibleUserException, DBException, InvalidRequestException, InvalidMSISDNException {

        validateUpdateUserDataRequest(userTrxInfo.logInfo(), userModel);

        String userId = userModel.getUserId();
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving user with id(" + userId + ")");
        AccountUser editableUser = accountUserDao.findAccountUserById(userId);
        if (editableUser == null) {
            throw new IneligibleUserException("Account User with ID[" + userId + "] not found");
        }

        editableUser = userConversionBean.updateAccountUserBasicInfo(userModel, editableUser);
        accountUserDao.edit(editableUser);
        acctLogger.info(userTrxInfo.logInfo() + "AccountUser[" + editableUser + " updated successfully");
    }

    private void validateUpdateUserDataRequest(String userTrxInfo, UserDetailsModel userModel) throws InvalidRequestException, InvalidMSISDNException {
        // validate request
        if (userModel == null || !userModel.isValid()) {
            throw new InvalidRequestException("One or more request param is null");
        } else {
            // validate the user phone number
            if (userModel.getPhoneNumber() != null && !userModel.getPhoneNumber().isEmpty()) {
                acctLogger.debug(userTrxInfo + "Validate and format user Phone number(" + userModel.getPhoneNumber() + ")");
                String validMsisdn = null;
                if ((validMsisdn = validateAndFormatMSISDN(userTrxInfo, userModel.getPhoneNumber())) != null) {
                    acctLogger.trace(userTrxInfo + "User Phone number formated to (" + userModel.getPhoneNumber() + ")");
                    userModel.setPhoneNumber(validMsisdn);
                } else {
                    throw new InvalidMSISDNException("Invalid user phone number");
                }
            }
            // validate the user Email
            if (userModel.getEmail() != null && !userModel.getEmail().isEmpty()) {
                try {
                    acctLogger.debug(userTrxInfo + "Validate user Email(" + userModel.getEmail() + ")");
                    InternetAddress emailAddr = new InternetAddress(userModel.getEmail());
                    emailAddr.validate();
                } catch (AddressException e) {
                    throw new InvalidRequestException("Invalid user Email " + e.getMessage());
                }
            }
        }
    }

    public String validateAndFormatMSISDN(String trxId, String msisdn) {
        String validMsisdn = null;

        try {
            if (msisdn != null) {
                if (SMSUtils.validateLocalAddress(msisdn)) {
                    if (acctLogger.isTraceEnabled()) {
                        acctLogger.trace(trxId + " valid local MSISDN: " + msisdn);
                    }
                    validMsisdn = SMSUtils.formatAddress(msisdn, AccountManagMsisdnFormat.INTER_CC_LOCAL);
                } else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
                        && SMSUtils.validateInternationalAddress(msisdn)) {
                    if (acctLogger.isTraceEnabled()) {
                        acctLogger.trace(trxId + " valid international MSISDN: " + msisdn);
                    }
                    validMsisdn = msisdn;
                } else {
                    if (acctLogger.isDebugEnabled()) {
                        acctLogger.debug(trxId + " Invalid MSISDN: " + msisdn);
                    }
                }
            } else {
                acctLogger.warn(trxId + " MSISDN is null.");
            }

        } catch (Exception e) {
            acctLogger.warn(trxId + " Exception: " + e.getMessage());
        }
        return validMsisdn;
    }

    private List<AccountUser> containsIgnoreCase(List<AccountUser> users, String search) {
        List<AccountUser> result = new ArrayList<>();

        if (users == null || search == null) {
            return users;
        }
        if (search.length() == 0) {
            return users;
        }

        for (Iterator<AccountUser> i = users.iterator(); i.hasNext();) {
            AccountUser user = i.next();

            if (user.getUsername().toLowerCase().contains(search.toLowerCase())) {
                result.add(user);
            }
        }

        return result;
    }
}// end of class UserManagementBean
