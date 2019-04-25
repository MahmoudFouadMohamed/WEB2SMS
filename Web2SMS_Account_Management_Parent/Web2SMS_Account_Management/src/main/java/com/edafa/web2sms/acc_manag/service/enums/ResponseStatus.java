package com.edafa.web2sms.acc_manag.service.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.utils.configs.enums.AccountManagModulesEnum;

@XmlType(name = "ResponseStatus", namespace = "http://www.edafa.com/web2sms/service/acc_manag/enums/")
@XmlEnum
public enum ResponseStatus {

	// Common Status
	SUCCESS(AccountManagModulesEnum.AccountManagUtils),
	FAIL(AccountManagModulesEnum.AccountManagUtils),
        
	INVALID_REQUEST(AccountManagModulesEnum.AccountManagUtils),
	INELIGIBLE_ACCOUNT(AccountManagModulesEnum.AccountManagUtils),
	INVALID_ACCOUNT_STATE(AccountManagModulesEnum.AccountManagement),
	USER_NOT_EXIST(AccountManagModulesEnum.AccountManagement),
	ACCT_NOT_EXIST(AccountManagModulesEnum.AccountManagement),
	DUPLICATE_REQUEST(AccountManagModulesEnum.AccountManagement),
	ACCOUNT_ALREADY_ACTIVE(AccountManagModulesEnum.AccountManagement),
	GROUP_NOT_EXIST(AccountManagModulesEnum.AccountManagement),
	GROUP_ALREADY_EXIST(AccountManagModulesEnum.AccountManagement),
	INELIGIBLE_USER(AccountManagModulesEnum.AccountManagement),
	INVALID_MSISDN(AccountManagModulesEnum.AccountManagement),
	GROUP_NOT_EDITABLE(AccountManagModulesEnum.AccountManagement),
	GROUP_NOT_REMOVABLE(AccountManagModulesEnum.AccountManagement),
	LAST_ADMIN_NOT_REMOVABLE(AccountManagModulesEnum.AccountManagement),
        
    FAILED_LOGIN(AccountManagModulesEnum.AccountManagement),
    PASSWORD_REQUIRED(AccountManagModulesEnum.AccountManagement),
    TEMP_PASSWORD_REQUIRED(AccountManagModulesEnum.AccountManagement),
    CUSTOMER_CARE(AccountManagModulesEnum.AccountManagement),
    USER_CONTACTED(AccountManagModulesEnum.AccountManagement),
    CHANGE_PASSWORD(AccountManagModulesEnum.AccountManagement),
    CHANGE_PASSWORD_OLD_REQUIRED(AccountManagModulesEnum.AccountManagement),
    BAD_PASSWORD(AccountManagModulesEnum.AccountManagement),
    SAME_OLD_PASSWORD(AccountManagModulesEnum.AccountManagement);

	AccountManagModulesEnum module;

	private ResponseStatus(AccountManagModulesEnum module) {

		this.module = module;

	}

	public AccountManagModulesEnum getModule() {
		return module;
	}

	public void setModule(AccountManagModulesEnum module) {
		this.module = module;
	}

}
