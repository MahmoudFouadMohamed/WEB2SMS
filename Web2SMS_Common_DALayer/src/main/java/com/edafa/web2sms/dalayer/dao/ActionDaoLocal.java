/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Action;

/**
 *
 * @author mahmoud
 */
@Local
public interface ActionDaoLocal {
    	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	void create(Action action) throws DBException;

	void edit(Action action) throws DBException;

	void remove(Action action) throws DBException;

	Action find(Object id) throws DBException;

	List<Action> findAll() throws DBException;

	List<Action> findRange(int[] range) throws DBException;

	public List<Action> findRange(int frist, int max, String order) throws DBException;

	int count() throws DBException;

	int count(Object id) throws DBException;
        
        public List<ActionName> getUserAllowedActions(String accountId, String userName) throws DBException;

}
