/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestActive;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.model.ProvRequestType;

/**
 * 
 * @author yyaseen
 */
@Local
public interface ProvRequestDaoLocal {

	void create(ProvRequestActive provRequestActives) throws DBException;

	void edit(ProvRequestActive provRequestActives) throws DBException;

	void remove(ProvRequestActive provRequestActives) throws DBException;

	ProvRequestActive find(Object id) throws DBException;

	List<ProvRequestActive> findAll() throws DBException;

	List<ProvRequestActive> findRange(int[] range) throws DBException;

	int count() throws DBException;

	int count(Object id) throws DBException;

	void updateStatus(String requestId, ProvRequestStatus status) throws DBException;

	List<ProvRequestActive> findByCompanyIdAndStatus(String companyId, List<ProvRequestStatus> statuses)
			throws DBException;

	List<ProvRequestActive> findByCompanyAdminAndStatus(String companyAdmin, List<ProvRequestStatus> statuses)
			throws DBException;

	int countByCompanyIdAndRequestTypeAndStatus(String companyId, ProvRequestType requestType, ProvRequestStatus status)
			throws DBException;

	int countByCompanyIdRequestTypeStatusAndSender(String companyId, ProvRequestType requestType,
			ProvRequestStatus status, String sender) throws DBException;

}
