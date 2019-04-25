/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user.interfaces;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.dalayer.exception.DBException;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface UserLoginServiceBeanLocal {

    public ResultStatus checkUserForLogin(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception;

    public AccountResultFullInfo userLogin(AccountUserTrxInfo userTrxInfo, String companyName, String password, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception;

    public AccountResultFullInfo directUserLogin(AccountUserTrxInfo userTrxInfo, String companyName) throws AccountNotFoundException, UserNotFoundException, DBException, Exception;
    
    public ResultStatus changeUserPassword(AccountUserTrxInfo userTrxInfo, String oldPassword, String newPassword, String secureToken) throws AccountNotFoundException, UserNotFoundException, DBException, Exception, SendTempSmsFailException;

    public ResultStatus userForgetPassword(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception;

    public ResultStatus resendTempPassword(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception;

}
