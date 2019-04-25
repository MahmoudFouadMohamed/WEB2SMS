package com.edafa.web2sms.service.admin.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Admin;
import com.edafa.web2sms.service.admin.exception.AdminNotFoundException;
import com.edafa.web2sms.service.admin.exception.DuplicateUserNameException;
import com.edafa.web2sms.service.admin.exception.InvalidAdminRequest;
import com.edafa.web2sms.service.model.TrxInfo;

@Local
public interface AdminManagementBeanLocal {
	
	void createAdmin(TrxInfo trxInfo, Admin admin) throws InvalidAdminRequest, DuplicateUserNameException, DBException;
	Admin adminLogin(TrxInfo trxInfo, String username, String password) throws InvalidAdminRequest, DBException, AdminNotFoundException;
	public Admin resetAdminPass(TrxInfo trxInfo, Admin admin) throws InvalidAdminRequest, DuplicateUserNameException,
	DBException, AdminNotFoundException ;

}
