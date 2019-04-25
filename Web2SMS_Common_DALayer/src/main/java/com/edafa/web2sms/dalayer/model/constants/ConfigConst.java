package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Configuration;

public interface ConfigConst {
	String CLASS_NAME = Configuration.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_ALL = PREFIX + "findAll";
	String FIND_BY_ID = PREFIX + "findById";
	String FIND_BY_Key = PREFIX + "findByKey";
	String FIND_BY_VALUE = PREFIX + "findByValue";
	String FIND_BY_MODULE_NAME = PREFIX + "findByModuleName";
	String FIND_BY_KEY_AND_MODULE_NAME = PREFIX + "findByKeyAndModuleName";
	String FIND_ALL_EDITABLE = PREFIX + "findAllEditable";
	String COUNT_EDITABLE_CONFIGS = PREFIX + "findAllEditableCount";
	String FIND_EDITABLE_BY_MODULE_NAME = PREFIX + "findEditableByModuleName";

	String ID = "id";
	String KEY = "key";
	String VALUE = "value";
	String DESCRIPTION = "description";
	String MODULE_NAME = "moduleName";
	String EDIT_FLAG = "editFlag";

}
