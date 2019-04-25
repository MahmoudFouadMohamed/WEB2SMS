/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Action;

/**
 *
 * @author mahmoud
 */
@Stateless
public class ActionDao extends AbstractDao<Action> implements ActionDaoLocal {

    @PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
    private EntityManager em;

    public ActionDao() {
        super(Action.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<ActionName> getUserAllowedActions(String accountId, String userName) throws DBException {
        try {
            TypedQuery<String> q = em.createNamedQuery(Action.FIND_BY_ACCOUNT_ID_AND_USERNAME , String.class).setParameter(
                    1, accountId).setParameter(2, userName).setParameter(3, accountId);
            List<String> list = q.getResultList();
            if (list != null && list.size() > 0) {
                List<ActionName> actionNames = new ArrayList<>();
                 for (String action : list) {                     
                     actionNames.add(ActionName.valueOf(action));
                 }
                 return actionNames;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

}
