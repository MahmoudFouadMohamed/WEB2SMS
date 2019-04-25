package com.edafa.web2sms.acc_manag.utils.security;

import javax.ejb.Local;

@Local
public interface EncryptionUtilLocal {

	String encryptString(String str);

	String decryptString(String str);

	String decrypt(byte[] text);

}
