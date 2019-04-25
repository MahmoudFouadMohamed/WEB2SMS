package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Language;

public interface LanguageConst {
	String CLASS_NAME = Language.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_NAME = PREFIX + "findByLanguageName";

	String NAME = "name";
	
	String LANGUAGE_NAME = "languageName";

}
