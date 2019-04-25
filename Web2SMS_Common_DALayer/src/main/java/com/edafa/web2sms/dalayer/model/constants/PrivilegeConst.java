/**
 * 
 */
package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Privilege;

/**
 * @author memad
 * 
 */
public interface PrivilegeConst {

	String CLASS_NAME = Privilege.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";
	String FIND_BY_ACCOUNT_GROUP_ID = PREFIX + "findByAccountGroupId";
	String FIND_BY_PRIVILEGE_NAME = PREFIX + "findByPrivilegeName";
	String FIND_BY_HIDDEN_FLAG = PREFIX + "findByHiddenFlag";

	String PRIVILEGE_ID = "privilegeId";
	String PRIVILEGE_NAME = "privilegeName";
	String HIDDEN_FLAG = "hiddenFlag";

	String GROUPS_ADMINS_PRIVILEGE = "Groups Admins";
}
