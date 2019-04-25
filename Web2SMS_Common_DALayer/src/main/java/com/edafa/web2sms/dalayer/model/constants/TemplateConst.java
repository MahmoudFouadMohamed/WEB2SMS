/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Template;

/**
 * 
 * @author yyaseen
 */
public interface TemplateConst {
	String CLASS_NAME = Template.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_SYSTEM_TEMPLATE_FLAG = PREFIX + "findBySystemTemplateFlag";

	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";

	String FIND_BY_ACCOUNT_AND_ADMIN = PREFIX + "findByAccountAndAdmin";
	
	String SYSTEM_TEMPLATE_FLAG = "systemTemplateFlag";
	
	String ACCOUNT_ID = "accountId";
	
	String FIND_ALL = PREFIX+"findAll";
	
	String FIND_ADMIN_TEMPLATES = "findAdminTemplates";
	
	String COUNT_BY_SYSTEM_TEMPLATE_FLAG = PREFIX + "countBySystemTemplateFlag";
}
