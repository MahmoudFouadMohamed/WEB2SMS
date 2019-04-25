/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user.interfaces.local;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.dalayer.exception.DBException;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface UserLoginBeanLocal {

    ResultStatus unblockUser(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws AccountNotFoundException, UserNotFoundException, DBException, Exception;
    ResultStatus generateTempPassword(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception;
}
