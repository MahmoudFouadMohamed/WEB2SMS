package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountIP;

@Local
public interface AccountIPDaoLocal {

	public void create(AccountIP entity) throws DBException;
	
	public void remove (AccountIP entity) throws DBException;
}
