/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.remote;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountConversionBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountManegementBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AdminAccountManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.Remote.UserManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.accountQuota.interfaces.Remote.AccountQuotaManagementBeanRemote;

/**
 *
 * @author mahmoud
 */
@Local
public interface AccountManegementRemotePoolsLocal {

	public AccountManegementBeanRemote getAccountManegementBeanRemote() throws Exception;

	public void returnAccountManegementBeanRemote(AccountManegementBeanRemote accountManegementBeanRemote)
			throws Exception;

	public AdminAccountManagementBeanRemote getAdminAccountManagementBeanRemote() throws Exception;

	public void returnAdminAccountManagementBeanRemote(
			AdminAccountManagementBeanRemote adminAccountManagementBeanRemote) throws Exception;

	public AccountConversionBeanRemote getAccountConversionBeanRemote() throws Exception;

	public void returnAccountConversionBeanRemote(AccountConversionBeanRemote accountConversionBeanRemote)
			throws Exception;

	public UserManagementBeanRemote getUserManagementBeanRemote() throws Exception;

	public void returnUserManagementBeanRemote(UserManagementBeanRemote userManagementBeanRemote) throws Exception;

	public AccountQuotaManagementBeanRemote getAccountQuotaManagementBeanRemote() throws Exception;

	public void returnAccountQuotaManagementBeanRemote(
			AccountQuotaManagementBeanRemote accountQuotaManagementBeanRemote) throws Exception;

	public String getAccountManegementBeanRemotePoolData();
}
