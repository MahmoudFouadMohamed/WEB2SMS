/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestArch;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.pojo.ProvisioningEvent;

/**
 *
 * @author yyaseen
 */
@Local
public interface ProvRequestsArchDaoLocal {

    void create(ProvRequestArch provRequestsArch) throws DBException;

    void edit(ProvRequestArch provRequestsArch) throws DBException;

    void remove(ProvRequestArch provRequestsArch) throws DBException;

    ProvRequestArch find(Object id) throws DBException;

    List<ProvRequestArch> findAll() throws DBException;

    List<ProvRequestArch> findRange(int[] range) throws DBException;

    int count() throws DBException;
    
    List<ProvRequestArch> findProvRequestByAccountId(String accountId) throws DBException;

	List<ProvisioningEvent> findProvRequestByDateAndStatus(Date startTime, Date endTime, ProvRequestStatus stauts) throws DBException;
    
}
