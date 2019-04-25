/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;

/**
 * 
 * @author yyaseen
 */
@Local
public interface ProvRequestStatusDaoLocal extends Cachable<ProvRequestStatus, ProvReqStatusName> {

	void create(ProvRequestStatus provRequestStatus) throws DBException;

	void edit(ProvRequestStatus provRequestStatus) throws DBException;

	void remove(ProvRequestStatus provRequestStatus) throws DBException;

	ProvRequestStatus find(Object id) throws DBException;

	List<ProvRequestStatus> findAll() throws DBException;

	List<ProvRequestStatus> findRange(int[] range) throws DBException;

	int count() throws DBException;

}
