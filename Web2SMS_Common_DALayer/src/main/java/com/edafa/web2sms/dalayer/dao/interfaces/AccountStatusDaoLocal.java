/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.AccountStatus;

/**
 * 
 * @author yyaseen
 */
@Local
public interface AccountStatusDaoLocal extends Cachable<AccountStatus, AccountStatusName> {

	void create(AccountStatus accountStatus) throws DBException;

	void edit(AccountStatus accountStatus) throws DBException;

	void remove(AccountStatus accountStatus) throws DBException;

	AccountStatus find(Object id) throws DBException;

	List<AccountStatus> findAll() throws DBException;

	List<AccountStatus> findRange(int[] range) throws DBException;

	int count() throws DBException;

}
