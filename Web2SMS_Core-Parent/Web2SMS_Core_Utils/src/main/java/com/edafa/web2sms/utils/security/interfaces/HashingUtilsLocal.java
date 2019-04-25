package com.edafa.web2sms.utils.security.interfaces;

import javax.ejb.Local;

@Local
public interface HashingUtilsLocal {
	
	public String hashWord(String word);

}
