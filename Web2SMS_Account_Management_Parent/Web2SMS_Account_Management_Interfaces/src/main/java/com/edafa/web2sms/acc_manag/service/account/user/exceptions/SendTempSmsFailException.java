/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author mahmoud
 */
@ApplicationException(rollback = true)
public class SendTempSmsFailException extends Exception {

    public SendTempSmsFailException(String message) {
        super(message);
    }

    public SendTempSmsFailException(Throwable cause) {
        super(cause);
    }

    public SendTempSmsFailException(String message, Throwable cause) {
        super(message, cause);
    }

}
