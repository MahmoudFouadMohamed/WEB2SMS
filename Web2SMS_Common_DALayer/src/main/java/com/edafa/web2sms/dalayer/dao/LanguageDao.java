/*
 * To change this template, choose Tools | Templates
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
import com.edafa.web2sms.dalayer.dao.interfaces.LanguageDaoLocal;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Language;

/**
 * 
 * @author akhalifah
 */
@Singleton
@Startup
public class LanguageDao extends AbstractDao<Language> implements
		LanguageDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	private Map<LanguageNameEnum, Language> cachedMap;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public LanguageDao() {
		super(Language.class);
		cachedMap = new HashMap<LanguageNameEnum, Language>();

	}

	@PostConstruct
	private void init() {
		// appLogger.info("Initializing the cached Language Map");
		try {
			refreshCachedValues();
		} catch (Exception e) {
			// appLogger.error("Cannot initialize the cached Language Map", e);
			throw new Error("Cannot initialize the cached Language Map", e);
		}
	}

	@Override
	public void refreshCachedValues() throws DBException {
		List<Language> languageList = findAll();
		for (Language language : languageList) {
			cachedMap.put(language.getLanguageName(), language);
			language.getLanguageName().setLanguageId(language.getLanguageId());
		}
	}

	@Override
	public Language getCachedObjectById(Object id) {
		for (Iterator it = cachedMap.values().iterator(); it.hasNext();) {
			Language language = (Language) it.next();
			if (language.getLanguageId().equals(id))
				return language;
		}	
		return null;
	}

	@Override
	public Language getCachedObjectByName(LanguageNameEnum name) {
		return cachedMap.get(name);
	}

	@Override
	public Language findByName(LanguageNameEnum name) throws DBException {
		try {
			return em.createNamedQuery(Language.FIND_BY_NAME, Language.class)
					.setParameter(Language.LANGUAGE_NAME, name).getSingleResult();

		} catch (Exception e) {
			return null;
		}
	}

}
