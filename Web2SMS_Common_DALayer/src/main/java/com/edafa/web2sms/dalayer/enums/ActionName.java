package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Action", namespace = "http://www.edafa.com/web2sms/service/model/enums/")
@XmlEnum
public enum ActionName {
	// Campaigns Actions
	CREATE_CAMPAIGN,
	DELETE_CAMPAIGN,
	EDIT_CAMPAIGN,
	UPDATE_CAMPAIGN_ACTION,
	VIEW_ACTIVE_CAMPAINGS,
	VIEW_REPORTS,
	VIEW_CAMPAIGNS_HISTORY,
	GENERATE_DETAILED_REPORT,
        VIEW_PENDING_CAMPAIGNS,

	// Lists Actions
	CREATE_LIST,
	DELETE_LIST,
	EDIT_LIST,
	REFRESH_LIST,
	VIEW_LISTS,
	VIEW_LIST_CONTACTS,
	EXPORT_LIST_TO_FILE,
	SEARCH_FOR_CONTCAT,
	EDIT_CONTCAT,

	// Templates Actions
	CREATE_TEMPLATE,
	DELETE_TEMPLATE,
	EDIT_TEMPLATE,
	VIEW_TEMPLATES,
	VIEW_ADMIN_TEMPLATES,

	//SMS API
	SEND_SMS,
	INQUIRY_SMS_ID,
	INQUIRY_SMS_DATES,

	//Account management
	VIEW_GROUPS,
	VIEW_OWN_GROUP,
	CREATE_GROUP,
	DELETE_GROUP,
	EDIT_GROUP,

	VIEW_USERS,
	VIEW_OWN_GROUP_USERS,
	VIEW_DEFAULT_GROUP_USERS,
	EDIT_USER_INFO,

//	ASSIGN_GROUP_USERS,
//	REMOVE_GROUP_USERS,
        EDIT_GROUP_USERS,

	EDIT_GROUP_PRIVILEGES,
	//REMOVE_GROUP_PRIVILEGES,

	MARK_GROUP_ADMIN,
	GET_CAMP_CREATE_NOTIFY,
	APPROVE_CAMPAIGN,
        REJECT_CAMPAIGN,
        SEND_CAMPAIGN,
    VIEW_QUOTA_INFO,
	// Default
	UNKNOWN,

	;

	public static void main(String[] args) {
		System.out.println(ActionName.values().length);
	}

}
