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
public class UserLoginEvent extends LoginEvent {

    private String password;

    public UserLoginEvent(UserLoginEventsEnum loginEventName, String password, String userLang) {
        super(loginEventName, userLang);
        this.password = password;
    }    
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }    
    
}
