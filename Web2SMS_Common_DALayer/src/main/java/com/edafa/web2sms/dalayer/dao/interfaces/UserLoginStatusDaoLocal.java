/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao.interfaces;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.UserLoginStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.UserLoginStatus;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface UserLoginStatusDaoLocal extends Cachable<UserLoginStatus, UserLoginStatusName> {

    void create(UserLoginStatus userLoginStatus) throws DBException;

    void edit(UserLoginStatus userLoginStatus) throws DBException;

    void remove(UserLoginStatus userLoginStatus) throws DBException;

    UserLoginStatus find(Object id) throws DBException;

    List<UserLoginStatus> findAll() throws DBException;

    List<UserLoginStatus> findRange(int[] range) throws DBException;

    int count() throws DBException;

}
