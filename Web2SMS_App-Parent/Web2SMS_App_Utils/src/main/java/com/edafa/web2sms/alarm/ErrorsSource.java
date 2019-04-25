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

    APP_SMSAPI("App_SMSAPI"), CAMPAIGN_MANAGEMENT("Campaign_Management"), ACCOUNT_MANAGEMENT("Account_Management"),
    LIST_MANAGEMENT("List_Management"), REPORT_MANAGEMENT("Report_Management"),
    TEMPLATE_MANAGEMENT("Template_Management"), CONFIGS_MANAGEMENT("Configs_Management");

    String sourceName;

    private ErrorsSource() {
    }

    private ErrorsSource(String sourceName) {
        this.sourceName = sourceName;
    }

}
