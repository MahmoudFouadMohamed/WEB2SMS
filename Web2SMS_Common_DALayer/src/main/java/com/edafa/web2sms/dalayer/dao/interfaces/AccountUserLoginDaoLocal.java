/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao.interfaces;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountUserLogin;

/**
 *
 * @author mahmoud
 */
public interface AccountUserLoginDaoLocal {

    void edit(AccountUserLogin accountUserLogin) throws DBException;

}
