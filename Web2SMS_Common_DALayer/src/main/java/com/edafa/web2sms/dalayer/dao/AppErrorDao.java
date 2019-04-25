/*
 * To change this template, choose Tools | Templates
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
import com.edafa.web2sms.dalayer.dao.interfaces.AppErrorDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AppErrorEntity;
import com.edafa.web2sms.dalayer.model.Module;

/**
 * 
 * @author akhalifah
 */
@Singleton
@Startup
public class AppErrorDao extends AbstractDao<AppErrorEntity> implements AppErrorDaoLocal {

	final String logClassName = "AppErrorDao: ";
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<String, AppErrorEntity> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AppErrorDao() {
		super(AppErrorEntity.class);
		cachedMap = new HashMap<String, AppErrorEntity>();
	}

	@PostConstruct
	private void init() {
		try {
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error("Cannot initialize the cached AppErrorEntity Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		System.out.println(logClassName + "Retrieve App Errors list from database");
		List<AppErrorEntity> alarmList = findAll();
		System.out.println(logClassName + "Retrieved App Errors list from database, found: " + alarmList.size()+ " elements.");
		for (AppErrorEntity appErrorEntity : alarmList) {
			cachedMap.put(appErrorEntity.getName(), appErrorEntity);
		}
	}

	@Override
	public AppErrorEntity getCachedObjectById(Object id) {
		for (AppErrorEntity appErrorEntity : cachedMap.values()) {
			if (appErrorEntity.getAppErrorId().equals(id)) {
				return appErrorEntity;
			}
		}
		return null;
	}

	@Override
	public AppErrorEntity getCachedObjectByName(String name) {
		return cachedMap.get(name);
	}

	@Override
	public List<AppErrorEntity> getModuleAlarms(Module module) {
		// TODO Auto-generated method stub
		return null;
	}

}
