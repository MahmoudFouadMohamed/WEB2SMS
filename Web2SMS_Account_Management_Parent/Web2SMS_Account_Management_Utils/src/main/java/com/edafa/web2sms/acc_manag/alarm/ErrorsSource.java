/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.alarm;

/**
 *
 * @author mahmoud
 */
public enum ErrorsSource {

    ACCOUNT_MANAGEMENT("Account_Management");

    String sourceName;

    private ErrorsSource() {
    }

    private ErrorsSource(String sourceName) {
        this.sourceName = sourceName;
    }

}
