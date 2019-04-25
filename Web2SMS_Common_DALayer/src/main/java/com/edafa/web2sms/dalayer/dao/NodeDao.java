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
import com.edafa.web2sms.dalayer.dao.interfaces.NodeDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Node;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class NodeDao extends AbstractDao<Node> implements NodeDaoLocal {

    private final String LOG_CLASS_NAME = "NodeDao: ";
    private final String LOG_ENTITY_NAME = "Node";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<String, Node> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NodeDao() {
        super(Node.class);
        cachedMap = new HashMap<String, Node>();
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
        List<Node> nodeList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + nodeList.size() + " elements.");

        for (Node node : nodeList) {
            cachedMap.put(node.getDescription(), node);
        }
    }

    @Override
    public Node getCachedObjectById(Object id) {
        for (Node node : cachedMap.values()) {
            if (node.getNodeId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public Node getCachedObjectByName(String description) {
        return cachedMap.get(description);
    }

}
