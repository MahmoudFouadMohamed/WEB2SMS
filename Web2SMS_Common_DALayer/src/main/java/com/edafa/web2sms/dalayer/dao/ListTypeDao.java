package com.edafa.web2sms.dalayer.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ListTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ListType;

/**
 * Session Bean implementation class ListTypeDao
 */
@Stateless
public class ListTypeDao extends AbstractDao<ListType> implements ListTypeDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<ListTypeName, ListType> cachedMap;
	java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public ListTypeDao() {
		super(ListType.class);
		cachedMap = new HashMap<ListTypeName, ListType>();
//		logger.setLevel(Level.ALL);
	}

	@PostConstruct
	public void init() {
		try {
			logger.info("Initializing cached ListType map.");
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error("Cannot initialize the cached ListType Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		

		List<ListType> listTypes = findAll();
		
		for (ListType listType : listTypes) {
			
			cachedMap.put(listType.getListTypeName(), listType);
			
		}

	}

	@Override
	public ListType getCachedObjectById(Object id) {
		return null;
	}

	@Override
	public ListType getCachedObjectByName(ListTypeName name) {
		// TODO Auto-generated method stub
		return cachedMap.get(name);
	}

}
