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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ScheduleFrequencyDaoLocal;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ScheduleFrequency;

/**
 * 
 * @author yyaseen
 */
@Singleton
public class ScheduleFrequencyDao extends AbstractDao<ScheduleFrequency>
		implements ScheduleFrequencyDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<ScheduleFrequencyName, ScheduleFrequency> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ScheduleFrequencyDao() {
		super(ScheduleFrequency.class);
		cachedMap = new HashMap<ScheduleFrequencyName, ScheduleFrequency>();
	}

	@PostConstruct
	void init() {
		try {
			refreshCachedValues();
		} catch (Exception e) {
			throw new Error(
					"Cannot initialize the cached ScheduleFrequency Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		List<ScheduleFrequency> frequencies = findAll();
		for (ScheduleFrequency frequency : frequencies) {
			cachedMap.put(frequency.getScheduleFreqName(), frequency);
		}
	}

	@Override
	public ScheduleFrequency getCachedObjectById(Object id) {
		for (Iterator it = cachedMap.values().iterator(); it.hasNext();) {
			ScheduleFrequency frequency = (ScheduleFrequency) it.next();
			if (frequency.getScheduleFreqId().equals(id))
				return frequency;
		}
		return null;
	}

	@Override
	public ScheduleFrequency getCachedObjectByName(ScheduleFrequencyName name) {
		return cachedMap.get(name);
	}

	@Override
	public ScheduleFrequency findByScheduleFreqName(String scheduleFreqName)
			throws DBException {
		ScheduleFrequency frequency = null;
		try {
			List<ScheduleFrequency> frequencies = em
					.createNamedQuery(
							ScheduleFrequency.FIND_BY_SCHEDULE_FREQ_NAME,
							ScheduleFrequency.class)
					.setParameter(ScheduleFrequency.NAME, scheduleFreqName)
					.getResultList();
			if (frequencies != null && !frequencies.isEmpty()) {
				frequency = frequencies.get(0);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		return frequency;
	}

}
