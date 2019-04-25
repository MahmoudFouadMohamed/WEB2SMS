/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidMSISDNException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidRequestException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotEditableException;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.IneligibleUserException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.model.UserResult;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;
import com.edafa.web2sms.dalayer.exception.DBException;

/**
 *
 * @author mahmoud
 */
@Local
public interface UserManagementServiceBeanLocal {

	public UserResult findUserByCoName(AccountUserTrxInfo userTrxInfo, String companyName)
			throws AccountNotFoundException, DBException, UserNotFoundException;

	int countAccountUsers(AccountUserTrxInfo userTrxInfo, String userName)
			throws DBException, IneligibleAccountException;

	List<UserDetailsModel> getAccountUsers(AccountUserTrxInfo userTrxInfo, String search, int first, int max)
			throws DBException, IneligibleAccountException;

	int countDefaultGroupUsers(AccountUserTrxInfo userTrxInfo, String userName)
			throws DBException, IneligibleAccountException;

	List<UserDetailsModel> getDefaultGroupUsers(AccountUserTrxInfo userTrxInfo, String search, int first, int max)
			throws DBException, IneligibleAccountException;

	void updateUserData(AccountUserTrxInfo userTrxInfo, UserDetailsModel userModel)
			throws DBException, IneligibleAccountException, NotEditableException, IneligibleUserException,
			InvalidRequestException, InvalidMSISDNException;
}
