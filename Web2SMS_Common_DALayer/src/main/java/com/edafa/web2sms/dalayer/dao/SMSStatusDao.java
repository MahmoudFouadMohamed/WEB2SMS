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
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSStatus;

/**
 * 
 * @author akhalifah
 */
@Singleton
@Startup
public class SMSStatusDao extends AbstractDao<SMSStatus> implements SMSStatusDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<SMSStatusName, SMSStatus> cachedMap;
	private Map<Integer, SMSStatus> cachedMapWithId;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SMSStatusDao() {
		super(SMSStatus.class);
		cachedMap = new HashMap<SMSStatusName, SMSStatus>();
		cachedMapWithId = new HashMap<Integer, SMSStatus>();
	}

	@PostConstruct
	public void init() {
		// appLogger.info("Initializing the cached SMSStatus Map");
		try {
			refreshCachedValues();
		} catch (Exception e) {
			// appLogger.error("Cannot initialize the cached SMSStatus Map", e);
			throw new Error("Cannot initialize the cached SMSStatus Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {

		List<SMSStatus> statusList = findAll();
		for (SMSStatus status : statusList) {
			cachedMap.put(status.getName(), status);
			cachedMapWithId.put(status.getId(), status);
			status.getName().setDbId(status.getId());
		}

	}

	@Override
	public SMSStatus getCachedObjectById(Object id) {
		return cachedMapWithId.get(id);
	}

	@Override
	public SMSStatus findByStatusName(SMSStatusName statusName) {
		// try {
		return em.createNamedQuery(SMSStatus.FIND_BY_STATUS_NAME, SMSStatus.class)
				.setParameter(SMSStatus.STATUS_NAME, statusName).getSingleResult();
		// } catch (Exception e) {
		// throw new DBException(e);
		// }
	}

	@Override
	public SMSStatus getCachedObjectByName(SMSStatusName name) {
		return cachedMap.get(name);
	}
}
