package com.edafa.web2sms.service.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.utils.configs.enums.ModulesEnum;

@XmlType(name = "ResponseStatus", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum ResponseStatus {

	// Common Status

	SUCCESS(ModulesEnum.AppUtils), FAIL(ModulesEnum.AppUtils), INVALID_REQUEST(ModulesEnum.AppUtils), INELIGIBLE_ACCOUNT(
			ModulesEnum.AppUtils),

	// List Management Response
	DUPLICATE_LIST_NAME(ModulesEnum.ListManagement), INVALED_FILE(ModulesEnum.ListManagement), INVALED_FILE_TYPE(
			ModulesEnum.ListManagement), LIST_NOT_FOUND(ModulesEnum.ListManagement), LOCKED_LIST(
			ModulesEnum.ListManagement), LIST_UPDATE_FAIL(ModulesEnum.ListManagement), CONTACTS_NOT_FOUND(
			ModulesEnum.ListManagement), DUPLICATE_CONTACT(ModulesEnum.ListManagement), PARTIAL_UPDATE(
			ModulesEnum.ListManagement), FULLY_UPDATED(ModulesEnum.ListManagement)

	// Campaign Management Response
	, INVALID_CAMPAIGN(ModulesEnum.CampaignManagement), INVALID_CAMPAIGN_STATE(ModulesEnum.CampaignManagement), CAMPAIGN_NOT_FOUND(
			ModulesEnum.CampaignManagement), NO_LOGS_FOR_CAMPAIGN(ModulesEnum.CampaignManagement), INVALID_CAMPAIGN_LIST(
			ModulesEnum.CampaignManagement),
			ACCOUNT_QUOTA_EXCEEDED(ModulesEnum.CampaignManagement),
			ACCOUNT_QUOTA_NOT_FOUND(ModulesEnum.CampaignManagement),
	// SMS API Response
			NO_LOGS_FOUND(ModulesEnum.SMSAPIManagement),

	// Template Management Response
	TEMPLATES_NOT_FOUND(ModulesEnum.TemplateManagement),

	;

	ModulesEnum module;

	private ResponseStatus(ModulesEnum module) {

		this.module = module;

	}

	public ModulesEnum getModule() {
		return module;
	}

	public void setModule(ModulesEnum module) {
		this.module = module;
	}

}
