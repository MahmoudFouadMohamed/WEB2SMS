/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestType;

/**
 * 
 * @author yyaseen
 */
@Singleton
@Startup
public class ProvRequestTypeDao extends AbstractDao<ProvRequestType> implements ProvRequestTypeDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<ProvRequestTypeName, ProvRequestType> cahchedObjects;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProvRequestTypeDao() {
		super(ProvRequestType.class);
		cahchedObjects = new HashMap<ProvRequestTypeName, ProvRequestType>();
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
		List<ProvRequestType> types = findAll();
		for (ProvRequestType provRequestType : types) {
			cahchedObjects.put(provRequestType.getProvReqTypeName(), provRequestType);
		}
	}

	@Override
	public ProvRequestType getCachedObjectById(Object id) {
		for (Iterator it = cahchedObjects.values().iterator(); it.hasNext();) {
			ProvRequestType type = (ProvRequestType) it.next();
			if (type.getProvReqTypeId().equals(id)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public ProvRequestType getCachedObjectByName(ProvRequestTypeName name) {
		return cahchedObjects.get(name);
	}
}
