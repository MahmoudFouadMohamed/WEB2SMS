/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusUserActionDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountStatusUserAction;

/**
 * 
 * @author akhalifah
 */
@Singleton
@Startup
public class AccountStatusUserActionDao extends AbstractDao<AccountStatusUserAction> implements
		AccountStatusUserActionDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<AccountStatusName, List<ActionName>> cachedMap;
	java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AccountStatusUserActionDao() {
		super(AccountStatusUserAction.class);
		cachedMap = new HashMap<AccountStatusName, List<ActionName>>();
		logger.setLevel(Level.ALL);
	}

	@PostConstruct
	public void init() {
		try {
			logger.info("Initializing cached AccountStatusUserAction map.");
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error("Cannot initialize the cached AccountStatusUserAction Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {

		// getting all AccountStatusUserAction records
		List<AccountStatusUserAction> statusList = findAll();

		List<ActionName> actionNameList;
		logger.info("AccountStatusUserAction rows are:" + statusList.size());
		for (AccountStatusUserAction accountStatusAction : statusList) {
			AccountStatus accountStatus = accountStatusAction.getAccountStatus();
			if (cachedMap.containsKey(accountStatus.getAccountStatusName())) {
				actionNameList = cachedMap.get(accountStatus.getAccountStatusName());
				actionNameList.add(accountStatusAction.getAction().getActionName());
			} else {
				actionNameList = new ArrayList<>();
				actionNameList.add(accountStatusAction.getAction().getActionName());
				cachedMap.put(accountStatus.getAccountStatusName(), actionNameList);
			}
			// System.out.println("action id : "+accountStatusAction.getUserAction().getActionId()
			// +" Action Name : "+accountStatusAction.getUserAction().getActionName());
		}
	}

	@Lock(LockType.READ)
	@Override
	public List<ActionName> getCachedObjectById(Object id) {
		return cachedMap.get(id);
	}

	@Lock(LockType.READ)
	@Override
	public List<ActionName> getCachedObjectByName(AccountStatusName acctStatusName) {
		return cachedMap.get(acctStatusName);
	}

}
