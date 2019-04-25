package com.edafa.web2sms.dalayer.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignActionDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignAction;

/**
 * Session Bean implementation class CampaignActionDao
 */
@Singleton
public class CampaignActionDao extends AbstractDao<CampaignAction> implements CampaignActionDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	private Map<CampaignActionName, CampaignAction> cachedMap;

	/**
	 * Default constructor.
	 */
	public CampaignActionDao() {
		super(CampaignAction.class);
		cachedMap = new HashMap<CampaignActionName, CampaignAction>();
	}

	@PostConstruct
	void init() {
		try {
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error("Cannot initialize the cached CampaignAction Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		List<CampaignAction> actions = findAll();
		for (CampaignAction action : actions) {
			cachedMap.put(action.getCampaignActionName(), action);
		}
	}

	@Override
	public CampaignAction getCachedObjectById(Object id) {
		for (Iterator it = cachedMap.values().iterator(); it.hasNext();) {
			CampaignAction action = (CampaignAction) it.next();
			if (action.getCampaignActionId().equals(id))
				return action;
		}
		return null;
	}

	@Override
	public CampaignAction getCachedObjectByName(CampaignActionName name) {
		return cachedMap.get(name);
	}

}
