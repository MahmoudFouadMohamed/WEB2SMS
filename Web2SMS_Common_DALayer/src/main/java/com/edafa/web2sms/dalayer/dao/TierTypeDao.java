package com.edafa.web2sms.dalayer.dao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.TierTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.TierType;
import com.edafa.web2sms.dalayer.model.constants.TierTypeConst;

/**
 *
 * @author mayahmed
 */
@Stateless
public class TierTypeDao extends AbstractDao<TierType> implements
		TierTypeDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<TierTypesEnum, TierType> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TierTypeDao() {
		super(TierType.class);
		cachedMap = new HashMap<TierTypesEnum, TierType>();

	}

	public List<TierType> findAllTiers() {
		List<TierType> resultList;
		TypedQuery<TierType> q = em.createNamedQuery(TierTypeConst.FIND_All,
				TierType.class);
		resultList = q.getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList;
		} else
			return null;
	}

	@PostConstruct
	public void init() {
		try {
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error("Cannot initialize the cached tier type Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		List<TierType> allTiers = findAllTiers();
		for (TierType tier : allTiers) {
			cachedMap.put(tier.getTierTypeName(), tier);
		}
	}

	@Override
	public TierType getCachedObjectById(Object id) {
		List<TierType> allTiers = findAllTiers();

		return cachedMap.get(allTiers.get((int) id));
	}

	@Override
	public TierType getCachedObjectByName(TierTypesEnum name) {
		return cachedMap.get(name);
	}

	@Override
	public List<TierTypesEnum> getCachedList() {
		List<TierTypesEnum> tierTypes = new ArrayList<TierTypesEnum>();

		tierTypes.addAll(cachedMap.keySet());
		return tierTypes;
	}

	public void setCachedMap(Map<TierTypesEnum, TierType> cachedMap) {
		this.cachedMap = cachedMap;
	}

}
