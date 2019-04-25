package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignExecutionDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignAction;
import com.edafa.web2sms.dalayer.model.CampaignExecution;
import com.edafa.web2sms.dalayer.model.CampaignStatus;

/**
 * Session Bean implementation class CampaignExecutionDao
 */
@Stateless
public class CampaignExecutionDao extends AbstractDao<CampaignExecution> implements CampaignExecutionDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Resource
	EJBContext context;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public CampaignExecutionDao() {
		super(CampaignExecution.class);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateCampaignAction(String campaignId, CampaignAction action) throws DBException {
		try {
			em.createNamedQuery(CampaignExecution.UPDATE_ACTION, CampaignExecution.class)
					.setParameter(CampaignExecution.ACTION, action)
					.setParameter(CampaignExecution.CAMPAIGN_ID, campaignId).executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateExecutionInfo(List<CampaignExecution> campExecutionList) throws DBException {
		Query q;
		try {
			for (CampaignExecution campaignExecution : campExecutionList) {
				updateExecutionState(campaignExecution);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateExecutionState(CampaignExecution campExe) throws DBException {
		Query q;
		try {
			q = em.createNamedQuery(CampaignExecution.UPDATE_CAMPAIGN_EXEC_STATE)
					.setParameter(CampaignExecution.CAMPAIGN_ID, campExe.getCampaign().getCampaignId())
					.setParameter(CampaignExecution.START_TIMESTAMP, campExe.getStartTimestamp())
					.setParameter(CampaignExecution.END_TIMESTAMP, campExe.getEndTimestamp())
					.setParameter(CampaignExecution.PROCESSING_TIMESTAMP, campExe.getProcessingTimestamp())
					.setParameter(CampaignExecution.SUBMITTED_SMS_COUNT, campExe.getSubmittedSmsCount())
					.setParameter(CampaignExecution.SUMITTED_SMS_SEG_COUNT, campExe.getSubmittedSmsSegCount())
					.setParameter(CampaignExecution.COMMENTS, campExe.getComments())
					.setParameter(CampaignExecution.EXECUTION_COUNT, campExe.getExecutionCount())
					.setParameter(CampaignExecution.HANDLER_ID, campExe.getHandlerId());
			q.executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateExecutionInfo(CampaignExecution campExe) throws DBException {
		Query q;
		try {
			q = em.createNamedQuery(CampaignExecution.UPDATE_CAMPAIGN_EXEC_INFO)
					.setParameter(CampaignExecution.CAMPAIGN_ID, campExe.getCampaign().getCampaignId())
					.setParameter(CampaignExecution.SUBMITTED_SMS_COUNT, campExe.getSubmittedSmsCount())
					.setParameter(CampaignExecution.SUMITTED_SMS_SEG_COUNT, campExe.getSubmittedSmsSegCount());
			q.executeUpdate();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public int updateCampaignsActionByAcctId(String accountId, CampaignAction action, List<CampaignStatus> statusList)
			throws DBException {
		Query q;
		try {
			q = em.createNamedQuery(CampaignExecution.UPDATE_ACTION_BY_ACCT_ID_AND_STATUSES)
					.setParameter(CampaignExecution.ACCOUNT_ID, accountId)
					.setParameter(CampaignExecution.ACTION, action).setParameter(Campaign.STATUSES, statusList);
			int updated = q.executeUpdate();
			return updated;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
}
