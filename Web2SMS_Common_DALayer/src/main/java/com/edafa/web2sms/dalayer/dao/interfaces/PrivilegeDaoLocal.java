/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Privilege;

/**
 *
 * @author mahmoud
 */
@Local
public interface PrivilegeDaoLocal {
        @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	void create(Privilege privilege) throws DBException;

	void edit(Privilege privilege) throws DBException;

	void remove(Privilege privilege) throws DBException;

	Privilege find(Object id) throws DBException;

	List<Privilege> findAll() throws DBException;

	List<Privilege> findRange(int[] range) throws DBException;

	public List<Privilege> findRange(int frist, int max, String order) throws DBException;
        
        public Privilege findByPrivilegeName(String privilegeName) throws DBException;
        
        public List<Privilege> findByHiddenFlag(boolean hiddenFlag) throws DBException;

	int count() throws DBException;

	int count(Object id) throws DBException;

}
