package com.edafa.web2sms.dalayer.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignSMSDetailsDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignSMSDetails;

/**
 * Session Bean implementation class CampaignSMSDetailsDao
 */
@Stateless
@LocalBean
public class CampaignSMSDetailsDao extends AbstractDao<CampaignSMSDetails> implements CampaignSMSDetailsDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public CampaignSMSDetailsDao() {
		super(CampaignSMSDetails.class);
	}

	@Override
	public String findSMSText(String campaignId) throws DBException {
		try {
			TypedQuery<String> q = em.createNamedQuery(CampaignSMSDetails.FIND_SMS_BY_CAMPAIGN_ID, String.class)
					.setParameter(CampaignSMSDetails.CAMPAIGN_ID, campaignId);
			String result = q.getSingleResult();
			if (result != null && !result.isEmpty())
				return result;
			return null;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}


}
