/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Admin;

/**
 *
 * @author yyaseen
 */
@Local
public interface AdminDaoLocal {

    void create(Admin admin) throws DBException;

    void edit(Admin admin) throws DBException;

    void remove(Admin admin) throws DBException;

    Admin find(String username) throws DBException;

    List<Admin> findAll() throws DBException;

    List<Admin> findRange(int[] range) throws DBException;

    int count() throws DBException;
    
}
