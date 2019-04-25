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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;

/**
 * 
 * @author yyaseen
 */
@Singleton
public class ProvRequestStatusDao extends AbstractDao<ProvRequestStatus> implements ProvRequestStatusDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<ProvReqStatusName, ProvRequestStatus> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProvRequestStatusDao() {
		super(ProvRequestStatus.class);
		cachedMap = new HashMap<ProvReqStatusName, ProvRequestStatus>();
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
		List<ProvRequestStatus> provStatusList = findAll();
		for (ProvRequestStatus provRequestStatus : provStatusList) {
			cachedMap.put(provRequestStatus.getStatusName(), provRequestStatus);
		}
	}

	@Override
	public ProvRequestStatus getCachedObjectById(Object id) {
		for (ProvRequestStatus provRequestStatus : cachedMap.values()) {
			if (provRequestStatus.getProvStatusId().equals(id))
				return provRequestStatus;
		}
		return null;
	}

	@Override
	public ProvRequestStatus getCachedObjectByName(ProvReqStatusName name) {
		return cachedMap.get(name);
	}

}
