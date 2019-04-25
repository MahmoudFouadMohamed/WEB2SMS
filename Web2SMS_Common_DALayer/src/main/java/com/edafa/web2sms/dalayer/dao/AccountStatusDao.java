/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountStatus;

/**
 * 
 * @author yyaseen
 */
@Singleton
@Startup
public class AccountStatusDao extends AbstractDao<AccountStatus> implements AccountStatusDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<AccountStatusName, AccountStatus> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AccountStatusDao() {
		super(AccountStatus.class);
		cachedMap = new HashMap<AccountStatusName, AccountStatus>();
	}

	@PostConstruct
	void init() {
		try {
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error("Cannot initialize the cached AccountStatus Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		List<AccountStatus> allStatus = findAll();
		for (AccountStatus status : allStatus) {
			cachedMap.put(status.getAccountStatusName(), status);
		}
	}

	@Override
	public AccountStatus getCachedObjectById(Object id) {
		for (AccountStatus status : cachedMap.values()) {
			if (status.getAccountStatusId().equals(id)) {
				return status;
			}
		}
		return null;
	}

	@Override
	public AccountStatus getCachedObjectByName(AccountStatusName name) {
		return cachedMap.get(name);
	}

}
