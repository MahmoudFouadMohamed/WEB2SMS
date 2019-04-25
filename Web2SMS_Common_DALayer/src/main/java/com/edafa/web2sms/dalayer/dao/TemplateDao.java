package com.edafa.web2sms.dalayer.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.TemplateDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Template;

/**
 * 
 * @author yyaseen
 */
@Stateless
public class TemplateDao extends AbstractDao<Template> implements TemplateDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TemplateDao() {
		super(Template.class);
	}

	@Override
	public List<Template> findByAccountId(String accountId) throws DBException {
		List<Template> allTemps = findAll();
		List<Template> accTemps = new ArrayList<Template>();
		for (int i = 0; i < allTemps.size(); i++) {
			for (int j = 0; j < allTemps.get(i).getAccountsList().size(); j++)
				if (allTemps.get(i).getAccountsList().get(j).getAccountId().equals(accountId))
					accTemps.add(allTemps.get(i));
		}
	

		return accTemps;
		// return em.createNamedQuery(Template.FIND_BY_ACCOUNT_ID,
		// Template.class)
		// .setParameter(Template.ACCOUNT_ID, accountId).getResultList();
	}

	@Override
	public List<Template> findByUserAndAdmin(String accountId) throws DBException {
		List<Template> allTemps = findAll();
		List<Template> accTemps = new ArrayList<Template>();
		for (int i = 0; i < allTemps.size(); i++) {
			for (int j = 0; j < allTemps.get(i).getAccountsList().size(); j++)
				if (allTemps.get(i).getAccountsList().get(j).getAccountId().equals(accountId))
					accTemps.add(allTemps.get(i));
		}

		for (int i = 0; i < allTemps.size(); i++) {
			if (allTemps.get(i).getSystemTemplateFlag())
				accTemps.add(allTemps.get(i));
		}
		return accTemps;
		// return em.createNamedQuery(Template.FIND_BY_ACCOUNT_AND_ADMIN,
		// Template.class)
		// .setParameter(Template.ACCOUNT_ID, accountId).getResultList();
	}

	@Override
	public List<Template> findAdminTemplates() throws DBException {
		return em.createNamedQuery(Template.FIND_BY_SYSTEM_TEMPLATE_FLAG, Template.class)
				.setParameter(Template.SYSTEM_TEMPLATE_FLAG, true).getResultList();
	}

	@Override
	public List<Template> findAdminTemplates(int first, int max) throws DBException {
		return em.createNamedQuery(Template.FIND_BY_SYSTEM_TEMPLATE_FLAG, Template.class)
				.setParameter(Template.SYSTEM_TEMPLATE_FLAG, true).setFirstResult(first).setMaxResults(max)
				.getResultList();
	}

	@Override
	public List<Template> findAll(int first, int max) throws DBException {
		return em.createNamedQuery(Template.FIND_ALL, Template.class)
		.setFirstResult(first).setMaxResults(max)
		.getResultList();
	}

	@Override
	public int countAdminTemplates() throws DBException {
		Long templatesCount = 0l;

		try {
			templatesCount = em.createNamedQuery(Template.COUNT_BY_SYSTEM_TEMPLATE_FLAG, Long.class)
					.setParameter(Template.SYSTEM_TEMPLATE_FLAG, true).getSingleResult();
		}// end try
		catch (Exception e) {
			throw e;
		}// end catch

		return templatesCount.intValue();
	}// end of method countAdminTemplates

}
