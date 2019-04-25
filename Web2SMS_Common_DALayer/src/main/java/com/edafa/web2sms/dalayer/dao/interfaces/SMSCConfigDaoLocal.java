package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSCConfig;

/**
 * 
 * @author akhalifah
 */
@Local
public interface SMSCConfigDaoLocal {

	void create(SMSCConfig sMSCConfig) throws DBException;

	void edit(SMSCConfig sMSCConfig) throws DBException;

	void remove(SMSCConfig sMSCConfig) throws DBException;

	SMSCConfig find(Object id) throws DBException;

	List<SMSCConfig> findAll() throws DBException;
	
	int findAllCount() throws DBException;

	List<SMSCConfig> findRange(int[] range) throws DBException;
	
	List<SMSCConfig> findAll(int max, int first) throws DBException;

	int count() throws DBException;

	List<SMSCConfig> findActive() throws DBException;

}
