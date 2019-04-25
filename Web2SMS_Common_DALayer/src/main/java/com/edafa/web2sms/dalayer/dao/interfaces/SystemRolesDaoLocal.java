/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SystemRole;

/**
 *
 * @author yyaseen
 */
@Local
public interface SystemRolesDaoLocal {

    void create(SystemRole role) throws DBException;

    void edit(SystemRole role) throws DBException;

    void remove(SystemRole role) throws DBException;

    SystemRole find(Object id) throws DBException;

    List<SystemRole> findAll() throws DBException;

    List<SystemRole> findRange(int[] range) throws DBException;

    int count() throws DBException;

	SystemRole findByRoleId(Integer roleId);
    
}
