/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.alarm;

/**
 *
 * @author mahmoud
 */
public enum ErrorsSource {

    WEB_PROV("WEB_PROV");

    String sourceName;

    private ErrorsSource() {
    }

    private ErrorsSource(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
    
    
}
