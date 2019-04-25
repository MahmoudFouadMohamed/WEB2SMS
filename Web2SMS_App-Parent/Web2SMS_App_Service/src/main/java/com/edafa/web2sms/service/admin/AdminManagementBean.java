package com.edafa.web2sms.service.admin;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.AdminDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SystemRolesDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Admin;
import com.edafa.web2sms.dalayer.model.SystemRole;
import com.edafa.web2sms.service.admin.exception.AdminNotFoundException;
import com.edafa.web2sms.service.admin.exception.DuplicateUserNameException;
import com.edafa.web2sms.service.admin.exception.InvalidAdminRequest;
import com.edafa.web2sms.service.admin.interfaces.AdminManagementBeanLocal;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.utils.security.HashUtils;

/**
 * Session Bean implementation class AdminManagementBean
 */
@Stateless
@LocalBean
public class AdminManagementBean implements AdminManagementBeanLocal {

	@EJB
	private AdminDaoLocal adminDoa;
	@EJB
	private SystemRolesDaoLocal roleDao;

	/**
	 * Default constructor.
	 */
	public AdminManagementBean() {
		// TODO Auto-generated constructor stub
	}

	public void createAdmin(TrxInfo trxInfo, Admin admin) throws InvalidAdminRequest, DuplicateUserNameException, DBException {

		if (admin.isvalid()) {
			try {
				HashUtils hu = new HashUtils();
				admin.setPassword(hu.hashWord(admin.getPassword()));
				if (adminDoa.find(admin.getUsername()) == null) {
					adminDoa.create(admin);
				} else {
					throw new DuplicateUserNameException(admin.getUsername());
				}
			} catch (DBException e) {
				throw e;
			}
		} else {
			throw new InvalidAdminRequest();
		}
	}

	public Admin resetAdminPass(TrxInfo trxInfo, Admin admin) throws InvalidAdminRequest, DuplicateUserNameException, DBException,
			AdminNotFoundException {

		if (admin.isvalid()) {
			try {
				HashUtils hu = new HashUtils();
				admin.setPassword(hu.hashWord(admin.getPassword()));
				admin.setResetDate(new Date());
				admin.setResetFlag(true);
				if (adminDoa.find(admin.getUsername()) != null) {
					adminDoa.edit(admin);
				} else {
					throw new AdminNotFoundException(admin.getAdminId());
				}
			} catch (DBException e) {
				throw e;
			}
			return admin;
		} else {
			throw new InvalidAdminRequest();
		}
	}

	public Admin adminLogin(TrxInfo trxInfo, String username, String password) throws InvalidAdminRequest, DBException, AdminNotFoundException {

		if (!username.equals("") && !password.equals("")) {
			HashUtils hu = new HashUtils();
			try {
				Admin admin = adminDoa.find(username);
				if (admin == null) {
					throw new AdminNotFoundException(username);
				}
				if (!admin.getUsername().equals(username) || !admin.getPassword().equals(hu.hashWord(password))) {
					throw new InvalidAdminRequest("please enter the correct password");
				}

				return admin;

			} catch (DBException e) {
				throw e;
			}
		} else if (username.equals("") && password.equals("")) {
			throw new InvalidAdminRequest();
		} else if (username.equals("")) {
			throw new InvalidAdminRequest("Please enter username. ");
		} else {
			throw new InvalidAdminRequest("Please enter password. ");
		}
	}

	public SystemRole getSystemRole(AdminTrxInfo adminTrxInfo, Integer roleId) {
		SystemRole result = roleDao.findByRoleId(roleId);
		return result;

	}



}
