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
import com.edafa.web2sms.dalayer.dao.interfaces.ModuleDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Module;

/**
 * Session Bean implementation class ModuleDao
 */
@Singleton
@Startup
public class ModuleDao extends AbstractDao<Module> implements ModuleDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<String, Module> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public ModuleDao() {
		super(Module.class);
		cachedMap = new HashMap<String, Module>();
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
		List<Module> modules = findAll();
		for (Module module : modules) {
			cachedMap.put(module.getName(), module);
		}
	}

	@Override
	public Module getCachedObjectById(Object id) {
		for (Module module : cachedMap.values()) {
			if (module.getId().equals(id)) {
				return module;
			}
		}
		return null;
	}

	@Override
	public Module getCachedObjectByName(String name) {
		return cachedMap.get(name);
	}

}
