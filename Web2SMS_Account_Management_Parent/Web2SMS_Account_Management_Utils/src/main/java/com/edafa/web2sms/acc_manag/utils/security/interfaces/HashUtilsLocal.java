package com.edafa.web2sms.acc_manag.utils.security.interfaces;

import java.util.Map;

import javax.ejb.Local;

@Local
public interface HashUtilsLocal {
	
	public String hashWord(String word);
	public String SHA256(Map<String, String> fields, String secureSecret);
	public String SHA256(String hashedFields, String secureSecret);

}
