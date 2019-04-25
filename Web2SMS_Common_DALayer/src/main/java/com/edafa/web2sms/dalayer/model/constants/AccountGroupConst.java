/**
 * 
 */
package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.AccountGroup;

/**
 * @author memad
 * 
 */
public interface AccountGroupConst {

	String CLASS_NAME = AccountGroup.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String FIND_BY_ACCOUNT_GROUP_ID = PREFIX + "findByAccountGroupId";
	String FIND_BY_ACCOUNT_ID_AND_GROUP_NAME = PREFIX + "findByAccountIdAndGroupName";
	String COUNT_BY_ACCOUNT_ID = PREFIX + "countByAccountId";
	String FIND_BY_ACCOUNT_ID_AND_DEFAULT_FLAG = PREFIX + "findByAccountIdAndDefaultFlag";

	String ACCOUNT_GROUP_ID = "accountGroupId";
	String ACCOUNT_ID = "accountId";
	String GROUP_NAME = "groupName";
	String HIDDEN_FLAG = "hiddenFlag";
	String DEFAULT_FLAG = "defaultFlag";
	
	String ADMINS_GROUP_NAME = "Admins";
	String GROUPS_ADMINS_GROUP_NAME = "Groups Admins";
}
