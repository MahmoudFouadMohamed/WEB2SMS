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
import com.edafa.web2sms.dalayer.dao.interfaces.AlarmDefinitionsDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AlarmDefinitions;
import com.edafa.web2sms.dalayer.model.Categories;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class AlarmDefinitionsDao extends AbstractDao<AlarmDefinitions> implements AlarmDefinitionsDaoLocal {

    private final String LOG_CLASS_NAME = "AlarmDefinitionsDao: ";
    private final String LOG_ENTITY_NAME = "AlarmDefinitions";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<Integer, AlarmDefinitions> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlarmDefinitionsDao() {
        super(AlarmDefinitions.class);
        cachedMap = new HashMap<Integer, AlarmDefinitions>();
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
        List<AlarmDefinitions> alarmDefinitionsList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + alarmDefinitionsList.size() + " elements.");

        for (AlarmDefinitions alarmDefinition : alarmDefinitionsList) {
            cachedMap.put(alarmDefinition.getAlarmId(), alarmDefinition);
        }
    }

    @Override
    public AlarmDefinitions getCachedObjectById(Object alarmId) {
        return cachedMap.get(alarmId);
    }

    @Override
    public AlarmDefinitions getCachedObjectByNodeId(Categories category) {
        for (AlarmDefinitions alarmDefinition : cachedMap.values()) {
            if (alarmDefinition.getCategoryId().equals(category)) {
                return alarmDefinition;
            }
        }
        return null;
    }
    
    @Override
    public AlarmDefinitions getCachedObjectByName(Integer alarmId) {
        return cachedMap.get(alarmId);
    }


}
