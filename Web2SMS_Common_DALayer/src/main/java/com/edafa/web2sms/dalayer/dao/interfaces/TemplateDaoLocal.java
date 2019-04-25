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
import com.edafa.web2sms.dalayer.model.Template;

/**
 *
 * @author yyaseen
 */
@Local
public interface TemplateDaoLocal {

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    void create(Template Templates) throws DBException;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    void edit(Template Templates) throws DBException;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    void remove(Template Templates) throws DBException;

    Template find(Object id) throws DBException;

    List<Template> findAll() throws DBException;
    


    List<Template> findRange(int[] range) throws DBException;

    int count() throws DBException;
    
    int count(Object id) throws DBException;
    
    List<Template> findByUserAndAdmin(String accountId) throws DBException;
    
    List<Template> findAdminTemplates() throws DBException;

	List<Template> findAdminTemplates(int first, int max) throws DBException;

	List<Template> findByAccountId(String accountId) throws DBException;
	
	int countAdminTemplates() throws DBException;

	List<Template> findAll(int first, int max) throws DBException;
    
}
