/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing.interfaces;

import com.edafa.web2sms.acc_manag.service.account.user.exceptions.AdminAlreadyGrantedException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface UserManagementFacingLocal {
    	List<AccountUser> getAccountUsers(AccountProvTrxInfo trxInfo, List<AccountStatus> statuses) throws UserNotFoundException;

	AccountUser getAccountAdminUser(AccountProvTrxInfo trxInfo);

	AccountUser addAccountUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException;

	AccountUser addAccountAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException,
			AdminAlreadyGrantedException;

	AccountUser getAccountUser(AccountProvTrxInfo trxInfo, String username) throws UserNotFoundException;

	AccountUser getAccountUser(AccountUserTrxInfo trxInfo) throws UserNotFoundException;

	List<AccountUser> getAccountUsers(AccountAdminTrxInfo trxInfo, int first, int max) throws UserNotFoundException;

	AccountUser activateAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException;

	void deactivateAccountUser(AccountProvTrxInfo trxInfo, String username) throws DBException, AdminAlreadyGrantedException,
			UserNotFoundException;

}
