/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.group.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author mahmoud
 */
@ApplicationException(rollback = true)
public class SetGroupAdminException extends Exception {

    public SetGroupAdminException() {
    }

    public SetGroupAdminException(String message) {
        super(message);
    }

    public SetGroupAdminException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetGroupAdminException(Throwable cause) {
        super(cause);
    }

    public SetGroupAdminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
