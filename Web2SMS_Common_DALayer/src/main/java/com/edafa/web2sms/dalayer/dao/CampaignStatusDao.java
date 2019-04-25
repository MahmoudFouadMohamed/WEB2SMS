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
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignStatus;

/**
 * Session t implementation class CampaignStatusDao
 */
@Singleton
@Startup
public class CampaignStatusDao extends AbstractDao<CampaignStatus> implements CampaignStatusDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<CampaignStatusName, CampaignStatus> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public CampaignStatusDao() {
		super(CampaignStatus.class);
		cachedMap = new HashMap<CampaignStatusName, CampaignStatus>();
	}

	@Override
	public CampaignStatus findByStatusName(CampaignStatusName statusName) {
		return em.createNamedQuery(CampaignStatus.FIND_BY_STATUS_NAME, CampaignStatus.class)
				.setParameter(CampaignStatus.STATUS_NAME, statusName).getSingleResult();
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
		List<CampaignStatus> allStatus = findAll();
		for (CampaignStatus status : allStatus) {
			cachedMap.put(status.getCampaignStatusName(), status);
		}
	}

	@Override
	public CampaignStatus getCachedObjectById(Object id) {
		for (CampaignStatus status : cachedMap.values()) {
			if (status.getCampaignStatusId().equals(id)) {
				return status;
			}
		}
		return null;
	}

	@Override
	public CampaignStatus getCachedObjectByName(CampaignStatusName name) {
		return cachedMap.get(name);
	}

}
