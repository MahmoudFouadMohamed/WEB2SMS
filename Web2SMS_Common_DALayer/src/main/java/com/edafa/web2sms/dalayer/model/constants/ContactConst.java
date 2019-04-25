package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Contact;

public interface ContactConst {
	String CLASS_NAME = Contact.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_LIST_ID = PREFIX + "findByListId";
	String SEARCH_LIST = PREFIX + "searchList";
	String COUNT_BY_MSISDN_AND_LIST_ID = PREFIX + "countByMsisdnAndListId";
	String COUNT_BY_LIST_ID = PREFIX + "countByListId";
	String COPY_LIST = PREFIX + "copyList";
	String UPDATE_CONTACT = PREFIX + "updateContact";
	String REMOVE_BY_LIST_ID = PREFIX + "removeByListId";

	String FIND_CONTACT_BY_CAMPAIGN_ID_AND_STATUS = PREFIX + "findContactByCampIdAndStatus";
	String COMPARE_LISTS_BY_LIST_IDs = PREFIX +"compareListsByListIds";
	String LIST_ID = "listId";
	String CAMPAIGN_ID = "campaignId";
	String STATUS_LIST = "statusList";
	String DELETE_CONTACTS_FROM_LIST= PREFIX+"DeleteContactsFromList";
	String COMPARE_AND_DELETE_DIFF=PREFIX+"ComapreAndDeleteDifference";
	
	String FIRST_LIST_ID= "newSubListId";
	String INTERNAL_LIST_ID="InternalListId";

	String MSISDN = "msisdn";
	String LAST_NAME = "lastName";
	String FIRST_NAME = "firstName";
	String OLD_MSISDN = "oldMsisdn";
	String VALUE_1 = "value1";
	String VALUE_2 = "value2";
	String VALUE_3 = "value3";
	String VALUE_4 = "value4";
	String VALUE_5 = "value5";
}
