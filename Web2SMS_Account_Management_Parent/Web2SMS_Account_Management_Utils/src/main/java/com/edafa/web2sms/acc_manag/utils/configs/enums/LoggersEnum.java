package com.edafa.web2sms.acc_manag.utils.configs.enums;

// SMPPModule(AccountManagModulesEnum.SMPPModule, "smppmodule"),
public enum LoggersEnum {

    ACCOUNT_MNGMT(AccountManagModulesEnum.AccountManagement, "web2sms_acct"),
    ACCOUNT_MNGMT_UTILS(AccountManagModulesEnum.AccountManagUtils, "web2sms_acct_app");

    private AccountManagModulesEnum module;
    private String logFileName;

    LoggersEnum(AccountManagModulesEnum module, String logFileName) {
        this.module = module;
        this.logFileName = logFileName;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public AccountManagModulesEnum getModule() {
        return module;
    }
}
