/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.ProvRequestType;

/**
 * 
 * @author yyaseen
 */
@Local
public interface ProvRequestTypeDaoLocal extends Cachable<ProvRequestType, ProvRequestTypeName> {

	void create(ProvRequestType provRequestsTypes) throws DBException;

	void edit(ProvRequestType provRequestsTypes) throws DBException;

	void remove(ProvRequestType provRequestsTypes) throws DBException;

	ProvRequestType find(Object id) throws DBException;

	List<ProvRequestType> findAll() throws DBException;

	List<ProvRequestType> findRange(int[] range) throws DBException;

	int count() throws DBException;

}
