/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Configuration;

/**
 * 
 * @author akhalifah
 */
@Local
public interface ConfigDaoLocal {

	void create(Configuration configuration) throws DBException;

	void edit(Configuration configuration) throws DBException;

	void remove(Configuration configuration) throws DBException;

	Configuration find(Object id) throws DBException;

	List<Configuration> findAll() throws DBException;

	List<Configuration> findRange(int[] range) throws DBException;

	int count() throws DBException;

	List<Configuration> findByKey(String key) throws DBException;

	Configuration findBykey(String key, String moduleName) throws DBException;

	List<Configuration> findByValue(String value) throws DBException;

	List<Configuration> findByModuleName(String moduleName) throws DBException;

	List<Configuration> findAllEditable() throws DBException;

	List<Configuration> findAllEditableRange(int max, int first) throws DBException;

	int findAllEditableCount() throws DBException;

	List<Configuration> findEditableByModuleName(String moduleName) throws DBException;

	void edit(List<Configuration> newConfigs) throws DBException;

}
