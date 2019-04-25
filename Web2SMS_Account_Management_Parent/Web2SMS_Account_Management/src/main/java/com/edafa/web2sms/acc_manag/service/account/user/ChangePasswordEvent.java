/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user;

/**
 *
 * @author mahmoud
 */
public class ChangePasswordEvent extends LoginEvent {
    
    private String oldPassword;
    private String newPassword;

    public ChangePasswordEvent(UserLoginEventsEnum loginEventName, String oldPassword, String newPassword, String userLang) {
        super(loginEventName, userLang);
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
    
    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }    
}
