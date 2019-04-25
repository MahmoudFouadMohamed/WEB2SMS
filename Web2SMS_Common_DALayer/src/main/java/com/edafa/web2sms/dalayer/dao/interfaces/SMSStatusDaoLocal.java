/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.SMSStatus;

/**
 * 
 * @author akhalifah
 */
@Local
public interface SMSStatusDaoLocal extends Cachable<SMSStatus, SMSStatusName> {

	void create(SMSStatus sMSStatus) throws DBException;

	void edit(SMSStatus sMSStatus) throws DBException;

	void remove(SMSStatus sMSStatus) throws DBException;

	SMSStatus find(Object id) throws DBException;

	List<SMSStatus> findAll() throws DBException;

	List<SMSStatus> findRange(int[] range) throws DBException;

	int count() throws DBException;

	SMSStatus findByStatusName(SMSStatusName statusName) throws DBException;

	void refreshCachedValues() throws DBException;

}
