package com.edafa.web2sms.dalayer.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignType;

@Stateless
public class CampaignTypeDao extends AbstractDao<CampaignType> implements CampaignTypeDaoLocal{

	
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<CampaignTypeName, CampaignType> cachedMap;
	java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public CampaignTypeDao() {
		super(CampaignType.class);
		cachedMap = new HashMap<CampaignTypeName, CampaignType>();
//		logger.setLevel(Level.ALL);
	}

	@PostConstruct
	public void init() {
		try {
			logger.info("Initializing cached CampaignType map.");
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error("Cannot initialize the cached CampaignType Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		

		List<CampaignType> campaignTypes = findAll();
		
		for (CampaignType campaignType : campaignTypes) {
			
			cachedMap.put(campaignType.getCampaignTypeName(), campaignType);
			
		}

	}

	@Override
	public CampaignType getCachedObjectById(Object id) {
		return null;
	}

	@Override
	public CampaignType getCachedObjectByName(CampaignTypeName name) {
		// TODO Auto-generated method stub
		return cachedMap.get(name);
	}
}
