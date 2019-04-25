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
import com.edafa.web2sms.dalayer.dao.interfaces.CategoriesDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Categories;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class CategoriesDao extends AbstractDao<Categories> implements CategoriesDaoLocal {

    private final String LOG_CLASS_NAME = "CategoriesDao: ";
    private final String LOG_ENTITY_NAME = "Categories";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<String, Categories> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriesDao() {
        super(Categories.class);
        cachedMap = new HashMap<String, Categories>();
    }

    @PostConstruct
    private void init() {
        try {
            refreshCachedValues();
        } catch (Exception e) {
            throw new Error("Cannot initialize the cached " + LOG_ENTITY_NAME + " Entity Map", e);
        }
    }

    @Override
    public void refreshCachedValues() throws DBException {
        System.out.println(LOG_CLASS_NAME + "Retrieve " + LOG_ENTITY_NAME + " list from database");
        List<Categories> categoriesList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + categoriesList.size() + " elements.");

        for (Categories category : categoriesList) {
            cachedMap.put(category.getCategoryName(), category);
        }
    }

    @Override
    public Categories getCachedObjectById(Object id) {
        for (Categories category : cachedMap.values()) {
            if (category.getCategoryId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public Categories getCachedObjectByName(String name) {
        return cachedMap.get(name);
    }

}
