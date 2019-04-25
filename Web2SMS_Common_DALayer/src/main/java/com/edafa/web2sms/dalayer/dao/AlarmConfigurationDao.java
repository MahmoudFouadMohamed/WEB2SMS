package com.edafa.web2sms.dalayer.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.jee.alarms.vfeg.exception.AlarmRaiseLocallyException;
import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AlarmConfigurationDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AlarmConfiguration;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class AlarmConfigurationDao extends AbstractDao<AlarmConfiguration> implements AlarmConfigurationDaoLocal {

    private final String LOG_CLASS_NAME = "AlarmConfigurationDao: ";
    private final String LOG_ENTITY_NAME = "AlarmConfiguration";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<String, AlarmConfiguration> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlarmConfigurationDao() {
        super(AlarmConfiguration.class);
        cachedMap = new HashMap<String, AlarmConfiguration>();
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
        List<AlarmConfiguration> alarmConfigurationList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + alarmConfigurationList.size() + " elements.");

        for (AlarmConfiguration alarmConfiguration : alarmConfigurationList) {
            cachedMap.put(alarmConfiguration.getPropertyName(), alarmConfiguration);
        }
    }

    @Override
    public AlarmConfiguration getCachedObjectById(Object id) {
        for (AlarmConfiguration alarmConfiguration : cachedMap.values()) {
            if (alarmConfiguration.getPropertyId().equals(id)) {
                return alarmConfiguration;
            }
        }
        return null;
    }

    @Override
    public AlarmConfiguration getCachedObjectByName(String propertyName) {
        return cachedMap.get(propertyName);
    }

    @Override
    public String getProperty(String propertyName) throws AlarmRaiseLocallyException {
        AlarmConfiguration alarmConfiguration = getCachedObjectByName(propertyName);
        if (alarmConfiguration == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached " + LOG_ENTITY_NAME + " with propertyName = " + propertyName);
        }
        return alarmConfiguration.getPropertyValue();
    }

    @Override
    public int getPropertyInt(String propertyName) throws AlarmRaiseLocallyException {
        AlarmConfiguration alarmConfiguration = getCachedObjectByName(propertyName);
        if (alarmConfiguration == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached " + LOG_ENTITY_NAME + " with propertyName = " + propertyName);
        }
        String propertyValue = alarmConfiguration.getPropertyValue();

        try {
            return Integer.parseInt(propertyValue);
        } catch (Exception e) {
            throw new AlarmRaiseLocallyException(propertyName + " cached NOT valied, propertyValue = " + propertyValue);
        }
    }
}
