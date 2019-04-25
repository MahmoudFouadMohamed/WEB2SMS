/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.ContactList;

/**
 * 
 * @author yyaseen
 */
public interface ContactListConst {
	String CLASS_NAME = ContactList.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_LIST_NAME = PREFIX + "findByListName";
	String FIND_BY_LIST_IDS = PREFIX + "findByListIds";
	String FIND_BY_LIST_ID = PREFIX + "findByListId";
	String GET_LIST_ID_BY_NAME = PREFIX + "getListIdByName";
	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String FIND_BY_ACCOUNT_ID_AND_TYPE = PREFIX + "findByAccountIdAndType";
	String FIND_BY_ACCOUNT_ID_AND_TYPES = PREFIX + "findByAccountIdAndTypes";

//	String FIND_BY_ACCOUNT_ID_AND_TYPE_WITH_COUNTS = PREFIX + "findByAccountIdAndTypeWithCounts";
//	String FIND_BY_ACCOUNT_ID_AND_TYPES_WITH_COUNTS = PREFIX + "findByAccountIdAndTypesWithCounts";
	String FIND_BY_LIST_NAME_AND_ACCOUNT_ID = PREFIX + "findByListNameAndAccountId";
	String UPDATE_LIST_NAME = PREFIX + "updateListName";
	String COUNT_CONTACTS_IN_LIST = PREFIX + "countContactsInList";
	String COUNT_CONTACTS_IN_LISTS = PREFIX + "countContactsInLists";
	String COUNT_BY_ACCOUNT_ID_AND_TYPE = PREFIX + "countAccountIdAndType";
	String COUNT_BY_ACCOUNT_ID_AND_TYPES = PREFIX + "countAccountIdAndTypes";
	String COUNT_BY_LIST_ID_AND_ACCOUNT_ID = PREFIX + "countByListIdAndAccountId";
	String COUNT_BY_LIST_NAME_AND_ACCOUNT_ID = PREFIX + "countByListNameAndAccountId";
	String FIND_BY_LIST_ID_AND_ACCOUNT_ID = PREFIX + "findByListIdAndAccountId";
	String SEARCH_BY_LIST_NAME_AND_TYPE = PREFIX + "searchByListNameAndType";
//	String SEARCH_BY_LIST_NAME_AND_TYPE_WITH_COUNTS = PREFIX + "searchByListNameAndTypeWithCounts";
	String REMOVE_BY_LIST_ID = PREFIX + "removeByListId";
	String FIND_BY_LIST_NAME_AND_ACCOUNT_ID_SQL= PREFIX+"findByListNameAndAccountIdNativeSql";

	String FIND_MSISDNs_BY_ACCOUNT_ID_AND_LIST_ID = PREFIX+ "findMsisdnsByAccountIdAndListsId";
	String LIST_NAME = "listName";
	String ACCOUNT_ID = "accountId";
	String LIST_ID = "listId";
	String LIST_IDS = "listIds";
	String ACCOUNT = "account";
	String LIST_TYPE = "listType";
	String LIST_TYPES = "listTypes";
}
